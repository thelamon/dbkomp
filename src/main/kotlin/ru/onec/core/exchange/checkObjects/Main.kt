package ru.onec.core.exchange.checkObjects

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.singleton
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.rapidoid.http.fast.On
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

/**
 * Created by thelamon on 22.03.16.
 */
object Main {
    val kodein: Kodein = Kodein {
        bind<CheckService>() with singleton { CheckService(instance("users_test"), instance("warehouse_test")) }
        bind<CheckController>() with singleton { CheckController(instance()) }

        bind<HikariDataSource>("users_test") with singleton {
            val config = HikariConfig();
            config.minimumIdle = 1;
            config.maximumPoolSize = 20;
            config.connectionTestQuery = "SELECT 1";
            config.driverClassName = "org.postgresql.Driver";
            config.jdbcUrl = "jdbc:postgresql://localhost:5432/users_test";
            config.username = "test";
            config.password = "test";

            HikariDataSource(config);
        }
        bind<HikariDataSource>("warehouse_test") with singleton {
            val config = HikariConfig();
            config.minimumIdle = 1;
            config.maximumPoolSize = 20;
            config.connectionTestQuery = "SELECT 1";
            config.driverClassName = "org.postgresql.Driver";
            config.jdbcUrl = "jdbc:postgresql://localhost:5432/warehouse_test";
            config.username = "test";
            config.password = "test";

            HikariDataSource(config);
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val checkController = kodein.instance<CheckController>()
        On.get("/check").html<String> { req -> checkController.check(req) }
    }
}

fun <T : Any> logger(forClass: Class<T>): Logger {
    return LoggerFactory.getLogger(unwrapCompanionClass(forClass).name)
}

// Return logger for Kotlin class
fun <T : Any> logger(forClass: KClass<T>): Logger {
    return logger(forClass.java)
}

// unwrap companion class to enclosing class given a Java Class
fun <T : Any> unwrapCompanionClass(ofClass: Class<T>): Class<*> {
    return if (ofClass.enclosingClass != null && ofClass.enclosingClass.kotlin.objectInstance?.javaClass == ofClass) {
        ofClass.enclosingClass
    } else {
        ofClass
    }
}

// unwrap companion class to enclosing class given a Kotlin Class
fun <T : Any> unwrapCompanionClass(ofClass: KClass<T>): KClass<*> {
    return unwrapCompanionClass(ofClass.java).kotlin
}

fun <T : Any> T.logger(): Logger {
    return logger(this.javaClass)
}
