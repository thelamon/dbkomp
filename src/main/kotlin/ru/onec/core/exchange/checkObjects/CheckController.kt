package ru.onec.core.exchange.checkObjects

import com.github.salomonbrys.kodein.Kodein

/**
 * Created by thelamon on 27.03.16.
 */

class CheckController(private val kodein: Kodein) {
    val checkService: CheckService = kodein.instance()

    fun check(): String {
        val result = checkService.check()
        return result
    }
}
