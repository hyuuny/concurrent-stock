package com.hyuuny.concurrentstock.application

import com.hyuuny.concurrentstock.domain.StockRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OptimisticLockStockService(
    private val stockRepository: StockRepository,
) {

    @Transactional
    fun decrease(id: Long, quantity: Long) {
        val existingStock = stockRepository.findByIdWithOptimisticLock(id)
        existingStock.decrease(quantity)
        stockRepository.saveAndFlush(existingStock)
    }

}