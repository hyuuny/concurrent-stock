package com.hyuuny.concurrentstock.domain

import jakarta.persistence.*

@Entity
class Stock(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0L,
    var productId: Long,
    var quantity: Long,
    @Version var version: Long = 1,
) {
    fun decrease(quantity: Long) {
        if (this.quantity - quantity < 0) {
            throw RuntimeException("재고가 부족합니다.")
        }

        this.quantity = this.quantity - quantity
    }
}