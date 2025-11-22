/*
 * Copyright 2002-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.reactive.function.server.cache.interceptor

import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.web.reactive.function.server.cache.context.CoCacheContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.coroutines.Continuation
import kotlin.reflect.jvm.jvmName

internal class CoCacheInterceptor(private val keyGenerator: KeyGenerator) : MethodInterceptor {
	override fun invoke(invocation: MethodInvocation): Any? {
		val coCache =
			(invocation.arguments.lastOrNull() as? Continuation<*>)
				?.context[CoCacheContext.Key]?.cache
				?: return invocation.proceed()

		val targetObject = invocation.getThis() ?: throw IllegalStateException()

		val coCacheKey = keyGenerator.generate(targetObject, invocation.method, *invocation.arguments)

		return coCache.computeIfAbsent(coCacheKey) {
			when (val publisher = invocation.proceed()) {
				is Mono<*> -> publisher.share()
				is Flux<*> -> publisher.buffer()
					.flatMapIterable { it }
					.replay()
					.refCount(1)

				else -> throw IllegalArgumentException("Unexpected type ${publisher?.let { it::class.jvmName }}")
			}
		}
	}
}