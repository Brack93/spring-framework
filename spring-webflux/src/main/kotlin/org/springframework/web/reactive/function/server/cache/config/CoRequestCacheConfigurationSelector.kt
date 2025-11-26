package org.springframework.web.reactive.function.server.cache.config

import org.springframework.context.annotation.AdviceMode
import org.springframework.context.annotation.AdviceModeImportSelector
import org.springframework.context.annotation.AutoProxyRegistrar
import org.springframework.web.reactive.function.server.cache.EnableCoRequestCaching
import kotlin.reflect.jvm.jvmName

private const val UNSUPPORTED_ADVISE_MODE_MESSAGE = "CoRequestCaching only support proxy mode"

internal class CoRequestCacheConfigurationSelector: AdviceModeImportSelector<EnableCoRequestCaching>() {
	override fun selectImports(adviceMode: AdviceMode): Array<out String> =
		when (adviceMode) {
			AdviceMode.PROXY -> arrayOf(AutoProxyRegistrar::class.jvmName, CoRequestCacheConfiguration::class.jvmName)
			AdviceMode.ASPECTJ -> throw UnsupportedOperationException(UNSUPPORTED_ADVISE_MODE_MESSAGE)
		}
}
