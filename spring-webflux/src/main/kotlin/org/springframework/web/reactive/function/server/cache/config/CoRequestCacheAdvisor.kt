package org.springframework.web.reactive.function.server.cache.config

import org.aopalliance.aop.Advice
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor
import org.springframework.web.reactive.function.server.cache.CoRequestCacheable
import java.lang.reflect.Method

internal class CoRequestCacheAdvisor(coRequestCacheAdvice: Advice) : StaticMethodMatcherPointcutAdvisor(coRequestCacheAdvice) {
	override fun matches(method: Method, targetClass: Class<*>): Boolean {
		return method.isAnnotationPresent(CoRequestCacheable::class.java)
	}
}
