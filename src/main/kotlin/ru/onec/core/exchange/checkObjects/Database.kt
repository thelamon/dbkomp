package ru.onec.core.exchange.checkObjects

import com.zaxxer.hikari.HikariDataSource

/**
 * Created by thelamon on 03.04.16.
 */
data class DatabaseServer(val name: String,
                          val ds: HikariDataSource,
                          val uinNames: Map<String, String> = mapOf()) {
    fun uinName(tableName: String) = uinNames.getOrElse(tableName, { "data" })
}

data class TableDifferenceResult(val tableName: String,
                                 val addedUins: List<String>,
                                 val deletedUins: List<String>,
                                 val changedUins: List<String> = listOf()) {
}
