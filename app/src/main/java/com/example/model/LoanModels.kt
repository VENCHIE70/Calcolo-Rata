package com.example.model

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.pow

data class InstallmentRow(
    val number: Int,
    val payment: Double,
    val principal: Double,
    val interest: Double,
    val remainingDebt: Double
)

data class YearlySummary(
    val year: Int,
    val totalPayment: Double,
    val totalPrincipal: Double,
    val totalInterest: Double,
    val endingRemainingDebt: Double
)

data class LoanResult(
    val installment: Double,
    val totalPaid: Double,
    val totalInterest: Double,
    val principal: Double,
    val totalMonths: Int,
    val installments: List<InstallmentRow>,
    val yearlySummaries: List<YearlySummary>
) {
    val principalPercentage: Float
        get() = if (totalPaid > 0.0) ((principal / totalPaid) * 100f).toFloat().coerceIn(0f, 100f) else 100f

    val interestPercentage: Float
        get() = if (totalPaid > 0.0) ((totalInterest / totalPaid) * 100f).toFloat().coerceIn(0f, 100f) else 0f
}

object LoanCalculator {

    fun calculate(
        amount: Double,
        annualRatePercent: Double,
        totalMonths: Int
    ): LoanResult {
        if (amount <= 0.0 || totalMonths <= 0) {
            return LoanResult(
                installment = 0.0,
                totalPaid = 0.0,
                totalInterest = 0.0,
                principal = amount.coerceAtLeast(0.0),
                totalMonths = totalMonths.coerceAtLeast(0),
                installments = emptyList(),
                yearlySummaries = emptyList()
            )
        }

        val monthlyRate = (annualRatePercent / 100.0) / 12.0
        val installment: Double = if (monthlyRate > 0.0) {
            val factor = (1.0 + monthlyRate).pow(totalMonths.toDouble())
            if (factor.isInfinite() || factor.isNaN() || (factor - 1.0) == 0.0) {
                amount / totalMonths
            } else {
                amount * (monthlyRate * factor) / (factor - 1.0)
            }
        } else {
            amount / totalMonths
        }

        val rows = ArrayList<InstallmentRow>(totalMonths)
        var currentBalance = amount
        var accumulatedInterest = 0.0
        var accumulatedPaid = 0.0

        for (k in 1..totalMonths) {
            val interest = if (monthlyRate > 0.0) currentBalance * monthlyRate else 0.0
            var principal = installment - interest
            var actualPayment = installment

            if (k == totalMonths || principal > currentBalance) {
                // Adjust final installment so debt reaches zero exactly
                principal = currentBalance
                actualPayment = principal + interest
                currentBalance = 0.0
            } else {
                currentBalance -= principal
            }

            accumulatedInterest += interest
            accumulatedPaid += actualPayment

            rows.add(
                InstallmentRow(
                    number = k,
                    payment = actualPayment,
                    principal = principal,
                    interest = interest,
                    remainingDebt = currentBalance.coerceAtLeast(0.0)
                )
            )
        }

        val yearlySummaries = rows.groupBy { (it.number - 1) / 12 + 1 }
            .map { (year, yearRows) ->
                YearlySummary(
                    year = year,
                    totalPayment = yearRows.sumOf { it.payment },
                    totalPrincipal = yearRows.sumOf { it.principal },
                    totalInterest = yearRows.sumOf { it.interest },
                    endingRemainingDebt = yearRows.last().remainingDebt
                )
            }

        return LoanResult(
            installment = installment,
            totalPaid = accumulatedPaid,
            totalInterest = accumulatedInterest,
            principal = amount,
            totalMonths = totalMonths,
            installments = rows,
            yearlySummaries = yearlySummaries
        )
    }

    private val italianCurrencyFormat = NumberFormat.getCurrencyInstance(Locale.ITALY).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
    }

    private val italianNumberFormat = NumberFormat.getNumberInstance(Locale.ITALY).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 0
    }

    fun formatCurrency(value: Double): String {
        return italianCurrencyFormat.format(value)
    }

    fun formatNumber(value: Double): String {
        return italianNumberFormat.format(value)
    }
}
