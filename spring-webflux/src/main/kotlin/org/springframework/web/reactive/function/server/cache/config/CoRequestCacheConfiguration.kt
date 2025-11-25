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
import org.springframework.web.reactive.function.server.cache.CoRequestCacheable
import org.springframework.web.reactive.function.server.cache.context.CoRequestCacheWebFilter
import org.springframework.web.reactive.function.server.cache.interceptor.CoRequestCacheInterceptor
import org.springframework.web.reactive.function.server.cache.interceptor.CoRequestCacheKeyGenerator
import org.springframework.web.server.CoWebFilter

private const val CO_REQUEST_CACHE_ADVISOR_PREFIX = "coRequestCache"

@Configuration(proxyBeanMethods = false)
internal class CoRequestCacheConfiguration {
	@Bean
	fun coRequestCacheInterceptor(): MethodInterceptor =
		CoRequestCacheInterceptor(
			CoRequestCacheKeyGenerator(
				SpelExpressionParser(),
				DefaultParameterNameDiscoverer()
			)
		)

	@Bean
	fun coRequestCachePointcut(): Pointcut =
		AnnotationMatchingPointcut(null, CoRequestCacheable::class.java)

	@Bean("$CO_REQUEST_CACHE_ADVISOR_PREFIX.advisor")
	fun coRequestCacheAdvisor(
		coRequestCacheInterceptor: MethodInterceptor,
		coRequestCachePointcut: Pointcut,
	): Advisor = DefaultPointcutAdvisor(coRequestCachePointcut, coRequestCacheInterceptor)

	@Bean
	fun coRequestCacheAdvisorAutoProxyCreator(): DefaultAdvisorAutoProxyCreator =
		DefaultAdvisorAutoProxyCreator().apply {
			isUsePrefix = true
			advisorBeanNamePrefix = CO_REQUEST_CACHE_ADVISOR_PREFIX
			isProxyTargetClass = true
		}

	@Bean
	fun coRequestCacheWebFilter(): CoWebFilter = CoRequestCacheWebFilter()
}
