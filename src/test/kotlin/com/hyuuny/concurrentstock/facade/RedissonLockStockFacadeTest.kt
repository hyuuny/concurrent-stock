package com.hyuuny.concurrentstock.facade

import com.hyuuny.concurrentstock.domain.Stock
import com.hyuuny.concurrentstock.domain.StockRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RedissonLockStockFacadeTest(
    private val redissonLockStockFacade: RedissonLockStockFacade,
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
     * Redisson
     * - 락 획득 재시도를 기본으로 제공한다.
     * - pub-sub 기반으로 Lock 구현하기 때문에 Redis의 부하를 줄여준다.
     * - Lettuce에 비해 구현이 디소 복잡하고, 별도의 라이브러리를 사용해야 한다.
     */
    @Test
    fun `동시에 100개의 요청`() {
        val threadCount = 100
        val executorService = Executors.newFixedThreadPool(32)
        val latch = CountDownLatch(threadCount)

        for (i: Int in 0..threadCount) {
            executorService.submit {
                try {
                    redissonLockStockFacade.decrease(1L, 1L)
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