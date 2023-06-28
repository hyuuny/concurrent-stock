package com.hyuuny.concurrentstock.domain

import org.springframework.data.jpa.repository.JpaRepository

interface StockRepository : JpaRepository<Stock, Long>