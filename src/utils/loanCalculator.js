/**
 * Loan Calculator - French Amortization Method (Ammortamento alla Francese)
 */

export function calculateLoan({
  amount = 10000,
  annualRate = 6.0,
  durationMonths = 60,
}) {
  const principal = Math.max(0, Number(amount) || 0);
  const rate = Math.max(0, Number(annualRate) || 0);
  const months = Math.max(1, Math.round(Number(durationMonths) || 1));

  if (principal <= 0) {
    return {
      monthlyPayment: 0,
      totalInterest: 0,
      totalRepayment: 0,
      monthlyBreakdown: [],
      yearlyBreakdown: [],
    };
  }

  const monthlyRate = rate / 100 / 12;

  let monthlyPayment = 0;
  if (monthlyRate === 0) {
    monthlyPayment = principal / months;
  } else {
    const factor = Math.pow(1 + monthlyRate, months);
    monthlyPayment = (principal * (monthlyRate * factor)) / (factor - 1);
  }

  const monthlyBreakdown = [];
  let remainingDebt = principal;
  let totalInterest = 0;

  for (let month = 1; month <= months; month++) {
    const interestPayment = monthlyRate > 0 ? remainingDebt * monthlyRate : 0;
    let principalPayment = monthlyPayment - interestPayment;

    if (month === months || principalPayment > remainingDebt) {
      principalPayment = remainingDebt;
      remainingDebt = 0;
    } else {
      remainingDebt -= principalPayment;
    }

    totalInterest += interestPayment;

    monthlyBreakdown.push({
      month,
      installment: principalPayment + interestPayment,
      principalPayment,
      interestPayment,
      remainingDebt: Math.max(0, remainingDebt),
    });
  }

  const totalRepayment = principal + totalInterest;

  // Yearly grouping
  const yearlyBreakdown = [];
  const totalYears = Math.ceil(months / 12);

  for (let year = 1; year <= totalYears; year++) {
    const startIdx = (year - 1) * 12;
    const endIdx = Math.min(year * 12, months);
    const yearSlices = monthlyBreakdown.slice(startIdx, endIdx);

    const yearPrincipal = yearSlices.reduce((acc, cur) => acc + cur.principalPayment, 0);
    const yearInterest = yearSlices.reduce((acc, cur) => acc + cur.interestPayment, 0);
    const yearInstallment = yearPrincipal + yearInterest;
    const endRemainingDebt = yearSlices[yearSlices.length - 1]?.remainingDebt || 0;

    yearlyBreakdown.push({
      year,
      installment: yearInstallment,
      principalPayment: yearPrincipal,
      interestPayment: yearInterest,
      remainingDebt: endRemainingDebt,
    });
  }

  return {
    monthlyPayment,
    totalInterest,
    totalRepayment,
    principal,
    monthlyBreakdown,
    yearlyBreakdown,
  };
}

export function formatCurrency(value) {
  return new Intl.NumberFormat('it-IT', {
    style: 'currency',
    currency: 'EUR',
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(value || 0);
}

export function formatPercent(value) {
  return new Intl.NumberFormat('it-IT', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(value || 0) + ' %';
}
