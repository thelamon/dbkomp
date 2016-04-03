package ru.onec.core.exchange.checkObjects

import com.github.andrewoma.kwery.core.SessionFactory
import com.github.andrewoma.kwery.core.dialect.PostgresDialect

/**
 * Created by thelamon on 03.04.16.
 */

public class UinTableDao(val databaseServer: DatabaseServer) {
    val logger = logger()
    val sessionFactory = SessionFactory(databaseServer.ds, PostgresDialect())

    fun tables(): Set<String> =
            sessionFactory.use { session ->
                session.select("SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' ")
                { row -> row.string("table_name") }.toSet()
            }

    fun rows(tableName: String): Set<String> {
        val uinName = databaseServer.uinName(tableName)
        return sessionFactory.use { session ->
            session.select("SELECT $uinName AS _uin FROM $tableName") { row -> row.string("_uin") }
                    .toSet()
        }
    }
}
