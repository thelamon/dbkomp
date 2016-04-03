package ru.onec.core.exchange.checkObjects

import org.rapidoid.http.Req

/**
 * Created by thelamon on 27.03.16.
 */

class CheckController(val checkService: CheckService) {
    fun check(req: Req): String {
        val source = req.param("source", "")
        val target = req.param("target", "")

        val result = checkService.check(source, target)
        return result
    }
}
