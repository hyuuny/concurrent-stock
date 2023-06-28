package com.hyuuny.concurrentstock.application

import com.hyuuny.concurrentstock.domain.Stock
import com.hyuuny.concurrentstock.domain.StockRepository
import org.assertj.core.api.Assertions.assertThat
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
class StockServiceTest(
    private val stockRepository: StockRepository,
    private val stockService: StockService,
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

    @Test
    fun `재고 감소`() {
        stockService.decrease(1L, 1L)

        // 재고 100 - 감소 1 -> 재고 수량 99
        val stock = stockRepository.findById(1L).orElseThrow()
        assertThat(stock.quantity).isEqualTo(99)
    }

    /**
     * 레이스 컨디션(Race Condition)으로 인해 테스트 실패
     * - 레이스 컨디션이란, 둘 이상의 스레드가 공유 데이터(재고 데이터)에 액세스할 수 있고, 동시에 변경하려고 할 때 발생한다.
     *
     * 이 문제를 해결하기 위해서는 하나의 스레드만 데이터에 액세스하도록 해야 한다.
     */
    @Test
    fun `동시에 100개의 요청`() {
        val threadCount = 100
        val executorService = Executors.newFixedThreadPool(32)
        val latch = CountDownLatch(threadCount)

        for (i: Int in 0..threadCount) {
            executorService.submit {
                try {
                    stockService.decrease(1L, 1L)
                } finally {
                    latch.countDown()
                }
            }
        }
            latch.await()
            val stock = stockRepository.findById(1L).orElseThrow()
            assertThat(stock.quantity).isEqualTo(0)
    }


}