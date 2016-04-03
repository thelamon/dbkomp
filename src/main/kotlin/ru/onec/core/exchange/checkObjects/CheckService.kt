package ru.onec.core.exchange.checkObjects

import com.zaxxer.hikari.HikariDataSource
import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.all
import nl.komponents.kovenant.combine.and
import nl.komponents.kovenant.task

/**
 * Created by thelamon on 27.03.16.
 */
class CheckService(val usersDS: HikariDataSource, val warehouseDS: HikariDataSource) {
    val logger = logger()

    val users = DatabaseServer("users", usersDS)
    val warehouse = DatabaseServer("warehouse", warehouseDS)
    val serverRegistry = mapOf(users.name to users, warehouse.name to warehouse)

    fun check(source: String, target: String): String {
        val sourceServer = serverRegistry[source]!!
        val targetServer = serverRegistry[target]!!

        logger.info("Comparing servers: {} <-> {}", sourceServer, targetServer)
        val sourceDao = UinTableDao(sourceServer)
        val targetDao = UinTableDao(targetServer)

        val (sourceTables, targetTables) = (task { sourceDao.tables() } and task { targetDao.tables() }).get()
        val workingTables = sourceTables.intersect(targetTables)
        val differenceTasks = mutableListOf<Promise<TableDifferenceResult, Exception>>()
        for (tableName in workingTables) {
            differenceTasks.add(task {
                calculateDiff(tableName, sourceDao, targetDao)
            } fail {
                logger.error("Unable to calculate difference for table $tableName", it)
            })
        }
        return all(differenceTasks).get().joinToString("<br/>")
    }

    private fun calculateDiff(tableName: String, sourceDao: UinTableDao, targetDao: UinTableDao): TableDifferenceResult {
        val sourceRows = sourceDao.rows(tableName)
        val targetRows = targetDao.rows(tableName)
        val added = sourceRows.minus(targetRows)
        val deleted = targetRows.minus(sourceRows)
        return TableDifferenceResult(tableName, added.toList(), deleted.toList())
    }
}
