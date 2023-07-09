package com.hyuuny.concurrentstock.facade

import com.hyuuny.concurrentstock.application.StockService
import com.hyuuny.concurrentstock.domain.RedisLockRepository
import org.springframework.stereotype.Component

@Component
class LettuceLockStockFacade(
    private val redisLockRepository: RedisLockRepository,
    private val stockService: StockService,
) {

    fun decrease(key: Long, quantity: Long) {
        while (!redisLockRepository.lock(key)) {
            Thread.sleep(100)
        }

        try {
            stockService.decrease(key, quantity)
        } finally {
            redisLockRepository.unLock(key)
        }

    }

}