package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.model.InstallmentRow
import com.example.model.LoanCalculator
import com.example.model.LoanResult
import com.example.model.YearlySummary
import com.example.ui.theme.AmberInterest
import com.example.ui.theme.EmeraldAccent
import com.example.viewmodel.LoanUiState
import com.example.viewmodel.LoanViewModel
import com.example.viewmodel.ScheduleViewMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanCalculatorScreen(
    viewModel: LoanViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalance,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = stringResource(id = R.string.app_name),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = stringResource(id = R.string.app_subtitle),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Constrain width on large screens / tablets
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 640.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. Result Hero Card
                        HeroInstallmentCard(result = uiState.calculationResult)

                        // 2. Input Parameters Card
                        ParametersInputCard(
                            uiState = uiState,
                            viewModel = viewModel,
                            onDone = { focusManager.clearFocus() }
                        )

                        // 3. Amortization Schedule Card
                        AmortizationScheduleSection(
                            uiState = uiState,
                            viewModel = viewModel
                        )

                        // 4. Educational explanation card
                        FrenchAmortizationInfoCard()

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun HeroInstallmentCard(
    result: LoanResult,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("hero_result_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.monthly_installment).uppercase(),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Installment Amount
            Text(
                text = LoanCalculator.formatCurrency(result.installment),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("calculated_installment_text")
            )

            Text(
                text = "al mese per ${result.totalMonths} mesi",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Visual Progress Bar (Capitale vs Interessi)
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(EmeraldAccent)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${stringResource(id = R.string.quota_capital)} ${String.format(java.util.Locale.US, "%.1f", result.principalPercentage)}%",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(AmberInterest)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${stringResource(id = R.string.quota_interest)} ${String.format(java.util.Locale.US, "%.1f", result.interestPercentage)}%",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Dual color segmented progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        val principalWeight = result.principalPercentage.coerceAtLeast(0.1f)
                        val interestWeight = result.interestPercentage.coerceAtLeast(0.1f)

                        Box(
                            modifier = Modifier
                                .weight(principalWeight)
                                .fillMaxSize()
                                .background(EmeraldAccent)
                        )
                        Box(
                            modifier = Modifier
                                .weight(interestWeight)
                                .fillMaxSize()
                                .background(AmberInterest)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))

            // 3 KPI summary metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricColumn(
                    title = stringResource(id = R.string.principal_borrowed),
                    value = LoanCalculator.formatCurrency(result.principal),
                    accentColor = EmeraldAccent,
                    modifier = Modifier.weight(1f)
                )
                MetricColumn(
                    title = stringResource(id = R.string.total_interest),
                    value = LoanCalculator.formatCurrency(result.totalInterest),
                    accentColor = AmberInterest,
                    modifier = Modifier.weight(1f)
                )
                MetricColumn(
                    title = stringResource(id = R.string.total_paid),
                    value = LoanCalculator.formatCurrency(result.totalPaid),
                    accentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun MetricColumn(
    title: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = accentColor,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParametersInputCard(
    uiState: LoanUiState,
    viewModel: LoanViewModel,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("parameters_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = stringResource(id = R.string.section_parameters),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            // --- 1. IMPORTO FINANZIAMENTO ---
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.loan_amount),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }

                OutlinedTextField(
                    value = uiState.amountInput,
                    onValueChange = { viewModel.onAmountChanged(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("amount_input"),
                    leadingIcon = {
                        Text(
                            text = "€",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 12.dp, end = 4.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )

                // Preset amount chips
                val amountPresets = listOf(5000.0, 10000.0, 25000.0, 50000.0, 100000.0)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    amountPresets.forEach { amount ->
                        val isSelected = uiState.amountInput == amount.toLong().toString()
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setAmountPreset(amount) },
                            label = { Text(text = "${amount.toLong() / 1000}k €") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("preset_amount_${amount.toLong()}")
                        )
                    }
                }
            }

            // --- 2. DURATA ---
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.duration),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )

                    // Unit Segmented Button (Anni / Mesi)
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.height(36.dp)
                    ) {
                        SegmentedButton(
                            selected = uiState.isDurationInYears,
                            onClick = { viewModel.setDurationInYears(true) },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            modifier = Modifier.testTag("duration_unit_years")
                        ) {
                            Text(text = stringResource(id = R.string.duration_years), fontSize = 12.sp)
                        }
                        SegmentedButton(
                            selected = !uiState.isDurationInYears,
                            onClick = { viewModel.setDurationInYears(false) },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            modifier = Modifier.testTag("duration_unit_months")
                        ) {
                            Text(text = stringResource(id = R.string.duration_months), fontSize = 12.sp)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilledTonalIconButton(
                        onClick = {
                            val current = uiState.durationInput.toIntOrNull() ?: 1
                            if (current > 1) {
                                viewModel.onDurationChanged((current - 1).toString())
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("duration_decrement_button")
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Diminuisci durata")
                    }

                    OutlinedTextField(
                        value = uiState.durationInput,
                        onValueChange = { viewModel.onDurationChanged(it) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("duration_input"),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            Text(
                                text = if (uiState.isDurationInYears) "Anni" else "Mesi",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    FilledTonalIconButton(
                        onClick = {
                            val current = uiState.durationInput.toIntOrNull() ?: 1
                            viewModel.onDurationChanged((current + 1).toString())
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("duration_increment_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Aumenta durata")
                    }
                }

                // Preset duration chips
                if (uiState.isDurationInYears) {
                    val yearPresets = listOf(2, 3, 5, 7, 10, 15, 20, 30)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        yearPresets.forEach { years ->
                            val isSelected = uiState.durationInput == years.toString()
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setDurationPreset(years, isYears = true) },
                                label = { Text(text = "$years anni") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.testTag("preset_duration_${years}_years")
                            )
                        }
                    }
                } else {
                    val monthPresets = listOf(12, 24, 36, 48, 60, 84, 120)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        monthPresets.forEach { months ->
                            val isSelected = uiState.durationInput == months.toString()
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setDurationPreset(months, isYears = false) },
                                label = { Text(text = "$months mesi") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.testTag("preset_duration_${months}_months")
                            )
                        }
                    }
                }
            }

            // --- 3. TASSO ANNUO (TAN) ---
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.interest_rate),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { viewModel.adjustRateBy(-0.25) }
                        ) {
                            Text(
                                text = "-0.25%",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { viewModel.adjustRateBy(0.25) }
                        ) {
                            Text(
                                text = "+0.25%",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = uiState.rateInput,
                    onValueChange = { viewModel.onRateChanged(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("rate_input"),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Percent,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        Text(
                            text = "% TAN",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { onDone() }),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Slider for fine tuning
                val currentRate = uiState.rateInput.replace(',', '.').toFloatOrNull() ?: 4.5f
                Slider(
                    value = currentRate.coerceIn(0.1f, 15f),
                    onValueChange = { newValue ->
                        viewModel.onRateChanged(String.format(java.util.Locale.US, "%.2f", newValue))
                    },
                    valueRange = 0.5f..15f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("rate_slider")
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmortizationScheduleSection(
    uiState: LoanUiState,
    viewModel: LoanViewModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("schedule_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Clickable header to expand/collapse
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.toggleScheduleExpanded() }
                    .padding(20.dp)
                    .testTag("toggle_schedule_button"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.TableChart,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(id = R.string.amortization_schedule_title),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (uiState.isScheduleExpanded) {
                                stringResource(id = R.string.hide_table)
                            } else {
                                "${uiState.calculationResult.installments.size} rate calcolate"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Icon(
                    imageVector = if (uiState.isScheduleExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (uiState.isScheduleExpanded) "Comprimi tabella" else "Espandi tabella",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(
                visible = uiState.isScheduleExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Segmented view toggle: Yearly vs Monthly
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        SegmentedButton(
                            selected = uiState.scheduleViewMode == ScheduleViewMode.YEARLY,
                            onClick = { viewModel.setScheduleViewMode(ScheduleViewMode.YEARLY) },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            modifier = Modifier.testTag("schedule_view_mode_yearly")
                        ) {
                            Text(text = stringResource(id = R.string.group_by_year), fontSize = 12.sp)
                        }
                        SegmentedButton(
                            selected = uiState.scheduleViewMode == ScheduleViewMode.MONTHLY,
                            onClick = { viewModel.setScheduleViewMode(ScheduleViewMode.MONTHLY) },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            modifier = Modifier.testTag("schedule_view_mode_monthly")
                        ) {
                            Text(text = stringResource(id = R.string.all_installments), fontSize = 12.sp)
                        }
                    }

                    // Table Header
                    TableHeaderRow(isYearly = uiState.scheduleViewMode == ScheduleViewMode.YEARLY)

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        thickness = 1.dp
                    )

                    // Table Content Rows
                    if (uiState.scheduleViewMode == ScheduleViewMode.YEARLY) {
                        uiState.calculationResult.yearlySummaries.forEachIndexed { index, yearSummary ->
                            YearlySummaryRow(
                                summary = yearSummary,
                                isEven = index % 2 == 0
                            )
                        }
                    } else {
                        // Monthly rows
                        uiState.calculationResult.installments.forEachIndexed { index, row ->
                            InstallmentMonthlyRow(
                                row = row,
                                isEven = index % 2 == 0
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun TableHeaderRow(isYearly: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isYearly) "Anno" else stringResource(id = R.string.num_short),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.9f)
        )
        Text(
            text = stringResource(id = R.string.quota_capital),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = EmeraldAccent,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.3f)
        )
        Text(
            text = stringResource(id = R.string.quota_interest),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = AmberInterest,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.3f)
        )
        Text(
            text = stringResource(id = R.string.residual_debt),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.5f)
        )
    }
}

@Composable
fun YearlySummaryRow(summary: YearlySummary, isEven: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isEven) Color.Transparent
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
            )
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Anno ${summary.year}",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.9f)
        )
        Text(
            text = LoanCalculator.formatNumber(summary.totalPrincipal) + " €",
            style = MaterialTheme.typography.bodySmall,
            color = EmeraldAccent,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.3f)
        )
        Text(
            text = LoanCalculator.formatNumber(summary.totalInterest) + " €",
            style = MaterialTheme.typography.bodySmall,
            color = AmberInterest,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.3f)
        )
        Text(
            text = LoanCalculator.formatNumber(summary.endingRemainingDebt) + " €",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.5f)
        )
    }
}

@Composable
fun InstallmentMonthlyRow(row: InstallmentRow, isEven: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isEven) Color.Transparent
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
            )
            .padding(vertical = 6.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${row.number}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.9f)
        )
        Text(
            text = LoanCalculator.formatNumber(row.principal) + " €",
            style = MaterialTheme.typography.bodySmall,
            color = EmeraldAccent,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.3f)
        )
        Text(
            text = LoanCalculator.formatNumber(row.interest) + " €",
            style = MaterialTheme.typography.bodySmall,
            color = AmberInterest,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.3f)
        )
        Text(
            text = LoanCalculator.formatNumber(row.remainingDebt) + " €",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.5f)
        )
    }
}

@Composable
fun FrenchAmortizationInfoCard(modifier: Modifier = Modifier) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(20.dp)
                    .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Come funziona l'Ammortamento Francese?",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(id = R.string.disclaimer),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
