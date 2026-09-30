package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.LoanCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Calcolo Rata", appName)
  }

  @Test
  fun `calculate loan installment French amortization`() {
    // 10.000 € at 6% for 60 months
    val result = LoanCalculator.calculate(
      amount = 10000.0,
      annualRatePercent = 6.0,
      totalMonths = 60
    )

    // Expected monthly installment is approx 193.33 €
    assertEquals(193.33, result.installment, 0.05)
    assertEquals(60, result.installments.size)
    assertEquals(5, result.yearlySummaries.size)
    assertTrue(result.totalPaid > 10000.0)
    assertEquals(0.0, result.installments.last().remainingDebt, 0.01)
  }
}
