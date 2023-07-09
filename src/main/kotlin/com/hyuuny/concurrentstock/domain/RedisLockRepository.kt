package com.hyuuny.concurrentstock.domain

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class RedisLockRepository(
    private val redisTemplate: RedisTemplate<String, String>,
) {

    fun lock(key: Long) = redisTemplate
        .opsForValue()
        .setIfAbsent(generateKey(key), "lock", Duration.ofMillis(3_000))!!

    fun unLock(key: Long) = redisTemplate.delete(generateKey(key))

    private fun generateKey(key: Long) = key.toString()

}