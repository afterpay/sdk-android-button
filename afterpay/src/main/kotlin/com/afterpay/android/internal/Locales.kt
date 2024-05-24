package com.afterpay.android.internal

import java.util.Locale

private val validRegionLanguages = mapOf(
    Locales.EN_AU.country to setOf(Locales.EN_AU),
    Locales.EN_GB.country to setOf(Locales.EN_GB),
    Locales.EN_NZ.country to setOf(Locales.EN_NZ),
    Locales.EN_US.country to setOf(Locales.EN_US),
    Locales.EN_CA.country to setOf(Locales.EN_CA, Locales.FR_CA),
)

internal object Locales {
    val EN_AU: Locale = Locale("en", "AU")
    val EN_CA: Locale = Locale.CANADA
    val FR_CA: Locale = Locale.CANADA_FRENCH
    val EN_NZ: Locale = Locale("en", "NZ")
    val EN_GB: Locale = Locale.UK
    val EN_US: Locale = Locale.US

    val validSet = setOf(
        EN_AU,
        EN_CA,
        FR_CA,
        EN_GB,
        EN_NZ,
        EN_US,
    )
}

internal fun getRegionLanguage(merchantLocale: Locale, consumerLocale: Locale): Locale? {
    return validRegionLanguages[merchantLocale.country]?.find {
        consumerLocale.language == it.language
    }
}
