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
     * @Transactional은 트랜잭션 종료 시점에 DB에 update query를 전달한다.
     * 그렇기 때문에 쓰레드가 업데이트되기 전의 재고 수량을 가져가서 처리하므로, 테스트가 실패한다.
     * 만약 @Transactional을 제거하면 테스트가 성공한다.
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