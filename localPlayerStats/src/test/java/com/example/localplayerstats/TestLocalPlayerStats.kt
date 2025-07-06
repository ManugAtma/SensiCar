package com.example.localplayerstats

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import sensicar.localplayerstats.contract.LocalPlayerStats



class TestLocalPlayerStats {

    @Test
    fun getSingleton_ObjectEquality() {
        val mockContext = mock(Context::class.java)
        val stats1 = LocalPlayerStats.getSingleton(mockContext, 600000)
        val stats2 = LocalPlayerStats.getSingleton(mockContext, 500000)

        assertEquals(stats1, stats2)
    }
}