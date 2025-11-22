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

import org.aopalliance.intercept.MethodInterceptor
import org.springframework.aop.Advisor
import org.springframework.aop.Pointcut
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator
import org.springframework.aop.support.DefaultPointcutAdvisor
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.DefaultParameterNameDiscoverer
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.web.reactive.function.server.cache.CoCacheable
import org.springframework.web.reactive.function.server.cache.context.CoCacheWebFilter
import org.springframework.web.reactive.function.server.cache.interceptor.CoCacheInterceptor
import org.springframework.web.reactive.function.server.cache.interceptor.CoCacheKeyGenerator
import org.springframework.web.server.CoWebFilter

private const val CO_CACHE_ADVISOR_PREFIX = "coCache"

@Configuration(proxyBeanMethods = false)
internal class CoCacheConfiguration {
	@Bean
	fun coCacheInterceptor(): MethodInterceptor =
		CoCacheInterceptor(
			CoCacheKeyGenerator(
				SpelExpressionParser(),
				DefaultParameterNameDiscoverer()
			)
		)

	@Bean
	fun coCachePointcut(): Pointcut =
		AnnotationMatchingPointcut(null, CoCacheable::class.java)

	@Bean("$CO_CACHE_ADVISOR_PREFIX.advisor")
	fun coCacheAdvisor(
		coCacheInterceptor: MethodInterceptor,
		coCachePointcut: Pointcut,
	): Advisor = DefaultPointcutAdvisor(coCachePointcut, coCacheInterceptor)

	@Bean
	fun coCacheAdvisorAutoProxyCreator(): DefaultAdvisorAutoProxyCreator =
		DefaultAdvisorAutoProxyCreator().apply {
			isUsePrefix = true
			advisorBeanNamePrefix = CO_CACHE_ADVISOR_PREFIX
			isProxyTargetClass = true
		}

	@Bean
	fun coCacheWebFilter(): CoWebFilter = CoCacheWebFilter()
}
