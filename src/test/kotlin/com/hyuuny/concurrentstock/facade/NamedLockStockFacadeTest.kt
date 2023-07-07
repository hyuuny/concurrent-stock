package com.hyuuny.concurrentstock.facade

import com.hyuuny.concurrentstock.domain.Stock
import com.hyuuny.concurrentstock.domain.StockRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
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
class NamedLockStockFacadeTest(
    private val namedLockStockFacade: NamedLockStockFacade,
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
     * 네임드 락(Named Lock)
     * - 이름을 가진 metadata locking이다.
     * - 이름을 가진 lock 을 획득한 후 해제할때까지 다른 세션은 이 lock 을 획득할 수 없도록 한다.
     * - 주의할점으로는 transaction 이 종료될 때 lock 이 자동으로 해제되지 않는다.
     * - 별도의 명령어로 해제를 수행해주거나 선점시간이 끝나야 해제된다.
     */
    @Test
    fun `동시에 100개의 요청`() {
        val threadCount = 100
        val executorService = Executors.newFixedThreadPool(32)
        val latch = CountDownLatch(threadCount)

        for (i: Int in 0..threadCount) {
            executorService.submit {
                try {
                    namedLockStockFacade.decrease(1L, 1L)
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