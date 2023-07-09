package com.hyuuny.concurrentstock.facade

import com.hyuuny.concurrentstock.domain.Stock
import com.hyuuny.concurrentstock.domain.StockRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@TestConstructor(autowireMode = AutowireMode.ALL)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class LettuceLockStockFacadeTest(
    private val lettuceLockStockFacade: LettuceLockStockFacade,
    private val stockRepository: StockRepository,
) {

    @BeforeEach
    fun before() {
        val newStock = Stock(productId = 1L, quantity = 100L)
        stockRepository.saveAndFlush(newStock)
    }

    @AfterEach
    fun after() {
        stockRepository.deleteAll()
    }

    /**
     * Lettuce
     * - setnx 명령어를 활용하여 분산락 구현
     * - 구현이 간단하지만, Spin Lock 방식이므로 Redis에 부하를 줄 수 있다.
     */
    @Test
    fun `동시에 100개의 요청`() {
        val threadCount = 100
        val executorService = Executors.newFixedThreadPool(32)
        val latch = CountDownLatch(threadCount)

        for (i: Int in 0..threadCount) {
            executorService.submit {
                try {
                    lettuceLockStockFacade.decrease(1L, 1L)
                } finally {
                    latch.countDown()
                }
            }
        }
        latch.await()
        val stock = stockRepository.findById(1L).orElseThrow()
        Assertions.assertThat(stock.quantity).isEqualTo(0)
    }
}