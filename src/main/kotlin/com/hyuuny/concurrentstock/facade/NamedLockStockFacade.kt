package com.hyuuny.concurrentstock.facade

import com.hyuuny.concurrentstock.application.StockService
import com.hyuuny.concurrentstock.domain.LockRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class NamedLockStockFacade(
    private val lockRepository: LockRepository,
    private val stockService: StockService,
) {

    @Transactional
    fun decrease(id: Long, quantity: Long) {
        try {
            lockRepository.getLock(id.toString())
            stockService.decrease(id, quantity)
        } finally {
            lockRepository.releaseLock(id.toString())
        }
    }

}