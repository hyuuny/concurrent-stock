package com.hyuuny.concurrentstock.facade

import com.hyuuny.concurrentstock.application.OptimisticLockStockService
import org.springframework.stereotype.Component

@Component
class OptimisticLockStockFacade(
    private val optimisticLockService: OptimisticLockStockService,
) {

    fun decrease(id: Long, quantity: Long) {
        while (true) {
            try {
                optimisticLockService.decrease(id, quantity)
                break
            } catch (e: Exception) {
                Thread.sleep(50)
            }
        }
    }

}