package org.springframework.web.reactive.function.server.cache.interceptor

data class NullaryMethodIdentity(
	val clazz: Class<*>,
	val methodName: String
)

