package ru.onec.core.exchange.checkObjects

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.provider
import com.github.salomonbrys.kodein.singleton
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.rapidoid.http.fast.On
import org.rapidoid.http.fast.ReqHandler

/**
 * Created by thelamon on 22.03.16.
 */
object Main {
    val kodein = Kodein {
        bind<CheckService>() with singleton { CheckService() }

        bind<HikariDataSource>("users_test") with provider {
            val config = HikariConfig();
            config.minimumIdle = 1;
            config.maximumPoolSize = 4;
            config.connectionTestQuery = "VALUES 1";
            config.driverClassName = "org.postgresql.Driver";
            config.jdbcUrl = "jdbc:postgresql://localhost:5432/users_test";
            config.username = "test";
            config.password = "test";

            HikariDataSource(config);
        }
        bind<HikariDataSource>("warehouse_test") with provider {
            val config = HikariConfig();
            config.minimumIdle = 1;
            config.maximumPoolSize = 4;
            config.connectionTestQuery = "VALUES 1";
            config.driverClassName = "org.postgresql.Driver";
            config.jdbcUrl = "jdbc:postgresql://localhost:5432/warehouse_test";
            config.username = "test";
            config.password = "test";

            HikariDataSource(config);
        }
    }
    val checkController: CheckController = ru.onec.core.exchange.checkObjects.CheckController(kodein)

    @JvmStatic
    fun main(args: Array<String>) {
        On.get("/check").html<String> (ReqHandler { checkController.check() })
    }
}

