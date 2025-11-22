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

import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.cache.interceptor.SimpleKey
import org.springframework.context.expression.MethodBasedEvaluationContext
import org.springframework.core.ParameterNameDiscoverer
import org.springframework.expression.Expression
import org.springframework.expression.ExpressionParser
import org.springframework.web.reactive.function.server.cache.CoCacheable
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.Continuation

class CoCacheKeyGenerator(
	private val expressionParser: ExpressionParser,
	private val parameterNameDiscoverer: ParameterNameDiscoverer,
	private val bakedExpressions: ConcurrentHashMap<String, Expression> = ConcurrentHashMap()
) : KeyGenerator {

	override fun generate(target: Any, method: Method, vararg params: Any?): Any {
		check(params.lastOrNull() is Continuation<*>)

		val targetClass = target::class
		val methodName = method.name

		if (params.size == 1) {
			return SimpleKey(targetClass, methodName)
		}

		val expressionString = method.getAnnotation(CoCacheable::class.java).key

		return if (expressionString.isBlank()) {
			SimpleKey(targetClass, methodName, params.copyOfRange(0, params.size - 1))
		} else {
			val context =
				MethodBasedEvaluationContext(
					target,
					method,
					params,
					parameterNameDiscoverer,
				)

			val expression =
				bakedExpressions.computeIfAbsent(expressionString) {
					expressionParser.parseExpression(it)
				}

			SimpleKey(targetClass, methodName, expression.getValue(context))
		}
	}
}