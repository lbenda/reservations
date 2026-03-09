package cz.lbenda.reservation.util

import java.util.UUID
import java.util.concurrent.ThreadLocalRandom

object Uuid7 {
    fun new(): UUID {
        val timestampMillis = System.currentTimeMillis()
        val randA = ThreadLocalRandom.current().nextLong(0, 1L shl 12)
        val randB = ThreadLocalRandom.current().nextLong()

        val mostSigBits = (timestampMillis shl 16) or (0x7L shl 12) or randA
        val leastSigBits = (randB and 0x3FFFFFFFFFFFFFFFL) or Long.MIN_VALUE

        return UUID(mostSigBits, leastSigBits)
    }
}
