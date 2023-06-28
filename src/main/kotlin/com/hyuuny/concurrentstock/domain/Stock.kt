package com.hyuuny.concurrentstock.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Stock(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0L,
    var productId: Long,
    var quantity: Long,
) {
    fun decrease(quantity: Long) {
        if (this.quantity - quantity < 0) {
            throw RuntimeException("재고가 부족합니다.")
        }

        this.quantity = this.quantity - quantity
    }
}