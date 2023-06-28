package com.hyuuny.concurrentstock.application

import com.hyuuny.concurrentstock.domain.StockRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class StockService(
    private val stockRepository: StockRepository,
) {

    fun decrease(id: Long, quantity: Long) {
        val existingStock = loadStock(id)
        existingStock.decrease(quantity)
        stockRepository.saveAndFlush(existingStock)
    }

    private fun loadStock(id: Long) =
        stockRepository.findByIdOrNull(id) ?: throw NoSuchElementException("${id}번 재고를 찾을 수 없습니다.")

}