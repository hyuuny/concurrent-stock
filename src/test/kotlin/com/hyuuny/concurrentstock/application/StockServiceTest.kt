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
    private val pessimisticLockStockService: PessimisticLockStockService,
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
        pessimisticLockStockService.decrease(1L, 1L)

        // 재고 100 - 감소 1 -> 재고 수량 99
        val stock = stockRepository.findById(1L).orElseThrow()
        assertThat(stock.quantity).isEqualTo(99)
    }

    /**
     * 비관적 락(Pessimistic Lock)
     * - 실제로 데이터에 Lock 을 걸어서 정합성을 맞추는 방법
     * - 배타적 잠금(쓰기 잠금)을 걸게되며, 다른 트랜잭션에서는 lock 이 해제되기전에 데이터를 가져갈 수 없다.
     * - 데드락이 걸릴 수 있기때문에 주의하여 사용해야 한다.
     */
    @Test
    fun `동시에 100개의 요청`() {
        val threadCount = 100
        val executorService = Executors.newFixedThreadPool(32)
        val latch = CountDownLatch(threadCount)

        for (i: Int in 0..threadCount) {
            executorService.submit {
                try {
                    pessimisticLockStockService.decrease(1L, 1L)
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