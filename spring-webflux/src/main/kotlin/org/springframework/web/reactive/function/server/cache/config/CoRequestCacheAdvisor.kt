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

package org.springframework.web.reactive.function.server.cache.config

import org.aopalliance.aop.Advice
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.web.reactive.function.server.cache.CoRequestCacheable
import org.springframework.web.reactive.function.server.cache.interceptor.NullaryMethodIdentity
import java.lang.reflect.Method

internal class CoRequestCacheAdvisor(
	val coRequestCacheableInstances: MutableMap<NullaryMethodIdentity, CoRequestCacheable>,
	coRequestCacheAdvice: Advice
) : StaticMethodMatcherPointcutAdvisor(coRequestCacheAdvice) {
	override fun matches(method: Method, targetClass: Class<*>): Boolean {
		val coRequestCacheable = AnnotatedElementUtils
			.findMergedAnnotation(method, CoRequestCacheable::class.java)

		if (coRequestCacheable == null) return false

		val nullaryMethodIdentity = NullaryMethodIdentity(targetClass, method.name)

		coRequestCacheableInstances[nullaryMethodIdentity] = coRequestCacheable

		return true
	}
}
