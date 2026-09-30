package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.model.LoanCalculator
import com.example.model.LoanResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class ScheduleViewMode {
    YEARLY,
    MONTHLY
}

data class LoanUiState(
    val amountInput: String = "15000",
    val durationInput: String = "5",
    val isDurationInYears: Boolean = true,
    val rateInput: String = "4.50",
    val isScheduleExpanded: Boolean = false,
    val scheduleViewMode: ScheduleViewMode = ScheduleViewMode.YEARLY,
    val calculationResult: LoanResult = LoanResult(
        installment = 0.0,
        totalPaid = 0.0,
        totalInterest = 0.0,
        principal = 0.0,
        totalMonths = 0,
        installments = emptyList(),
        yearlySummaries = emptyList()
    )
)

class LoanViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoanUiState())
    val uiState: StateFlow<LoanUiState> = _uiState.asStateFlow()

    init {
        recalculate()
    }

    fun onAmountChanged(newAmount: String) {
        val sanitized = newAmount.filter { it.isDigit() || it == '.' || it == ',' }
        _uiState.update { it.copy(amountInput = sanitized) }
        recalculate()
    }

    fun setAmountPreset(amount: Double) {
        _uiState.update { it.copy(amountInput = amount.toLong().toString()) }
        recalculate()
    }

    fun onDurationChanged(newDuration: String) {
        val sanitized = newDuration.filter { it.isDigit() }
        _uiState.update { it.copy(durationInput = sanitized) }
        recalculate()
    }

    fun setDurationInYears(isYears: Boolean) {
        if (_uiState.value.isDurationInYears == isYears) return

        val currentVal = _uiState.value.durationInput.toIntOrNull() ?: 1
        val convertedVal = if (isYears) {
            // from months to years
            (currentVal / 12).coerceAtLeast(1)
        } else {
            // from years to months
            (currentVal * 12).coerceAtLeast(1)
        }

        _uiState.update {
            it.copy(
                isDurationInYears = isYears,
                durationInput = convertedVal.toString()
            )
        }
        recalculate()
    }

    fun setDurationPreset(value: Int, isYears: Boolean) {
        _uiState.update {
            it.copy(
                isDurationInYears = isYears,
                durationInput = value.toString()
            )
        }
        recalculate()
    }

    fun onRateChanged(newRate: String) {
        val sanitized = newRate.filter { it.isDigit() || it == '.' || it == ',' }
        _uiState.update { it.copy(rateInput = sanitized) }
        recalculate()
    }

    fun adjustRateBy(delta: Double) {
        val currentRate = parseNumber(_uiState.value.rateInput) ?: 0.0
        val newRate = (currentRate + delta).coerceIn(0.1, 30.0)
        _uiState.update {
            it.copy(rateInput = String.format(java.util.Locale.US, "%.2f", newRate))
        }
        recalculate()
    }

    fun toggleScheduleExpanded() {
        _uiState.update { it.copy(isScheduleExpanded = !it.isScheduleExpanded) }
    }

    fun setScheduleViewMode(mode: ScheduleViewMode) {
        _uiState.update { it.copy(scheduleViewMode = mode) }
    }

    private fun recalculate() {
        val state = _uiState.value
        val amount = parseNumber(state.amountInput) ?: 0.0
        val durationVal = state.durationInput.toIntOrNull() ?: 0
        val totalMonths = if (state.isDurationInYears) durationVal * 12 else durationVal
        val ratePercent = parseNumber(state.rateInput) ?: 0.0

        val result = LoanCalculator.calculate(
            amount = amount,
            annualRatePercent = ratePercent,
            totalMonths = totalMonths
        )

        _uiState.update { it.copy(calculationResult = result) }
    }

    private fun parseNumber(text: String): Double? {
        if (text.isBlank()) return null
        val normalized = text.replace(',', '.')
        return normalized.toDoubleOrNull()
    }
}
