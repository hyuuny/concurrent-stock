package com.hyuuny.concurrentstock

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ConcurrentStockApplication

fun main(args: Array<String>) {
    runApplication<ConcurrentStockApplication>(*args)
}
