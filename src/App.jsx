import React, { useState, useMemo, useEffect } from 'react';
import { 
  Calculator, 
  Sun, 
  Moon, 
  Printer, 
  Download, 
  Plus, 
  Minus, 
  Calendar, 
  Percent, 
  Euro, 
  HelpCircle,
  TrendingDown
} from 'lucide-react';
import { calculateLoan, formatCurrency, formatPercent } from './utils/loanCalculator';

export default function App() {
  const [amount, setAmount] = useState(15000);
  const [durationMode, setDurationMode] = useState('years'); // 'years' | 'months'
  const [durationYears, setDurationYears] = useState(5);
  const [durationMonths, setDurationMonths] = useState(60);
  const [interestRate, setInterestRate] = useState(5.5);
  const [tableView, setTableView] = useState('yearly'); // 'yearly' | 'monthly'
  const [darkMode, setDarkMode] = useState(false);

  // Sync years and months
  const handleYearsChange = (val) => {
    const y = Math.max(1, Math.min(30, Number(val) || 1));
    setDurationYears(y);
    setDurationMonths(y * 12);
  };

  const handleMonthsChange = (val) => {
    const m = Math.max(1, Math.min(360, Number(val) || 1));
    setDurationMonths(m);
    setDurationYears(Math.max(1, Math.round(m / 12)));
  };

  const currentTotalMonths = durationMode === 'years' ? durationYears * 12 : durationMonths;

  // Calculate loan results
  const result = useMemo(() => {
    return calculateLoan({
      amount,
      annualRate: interestRate,
      durationMonths: currentTotalMonths,
    });
  }, [amount, interestRate, currentTotalMonths]);

  // Dark mode effect
  useEffect(() => {
    if (darkMode) {
      document.documentElement.setAttribute('data-theme', 'dark');
    } else {
      document.documentElement.removeAttribute('data-theme');
    }
  }, [darkMode]);

  // Export CSV
  const exportCSV = () => {
    const headers = ['Mese', 'Rata', 'Quota Capitale', 'Quota Interessi', 'Debito Residuo'];
    const rows = result.monthlyBreakdown.map(item => [
      item.month,
      item.installment.toFixed(2),
      item.principalPayment.toFixed(2),
      item.interestPayment.toFixed(2),
      item.remainingDebt.toFixed(2),
    ]);

    const csvContent = 'data:text/csv;charset=utf-8,' + 
      [headers.join(';'), ...rows.map(e => e.join(';'))].join('\n');
    
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement('a');
    link.setAttribute('href', encodedUri);
    link.setAttribute('download', `piano_ammortamento_${amount}euro_${currentTotalMonths}mesi.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  // Percentages for bar
  const principalPercent = result.totalRepayment > 0 
    ? ((result.principal / result.totalRepayment) * 100).toFixed(1) 
    : 100;
  const interestPercent = (100 - principalPercent).toFixed(1);

  return (
    <div className="app-container">
      {/* Header */}
      <header className="app-header">
        <div className="brand">
          <div className="brand-icon">
            <Calculator size={24} />
          </div>
          <div className="brand-text">
            <h1>Calcolo Rata</h1>
            <p>Simulatore di finanziamento con ammortamento alla francese</p>
          </div>
        </div>

        <div className="header-actions">
          <button 
            className="icon-btn" 
            onClick={() => window.print()} 
            title="Stampa o Salva PDF"
          >
            <Printer size={16} />
            <span>Stampa / PDF</span>
          </button>

          <button 
            className="icon-btn" 
            onClick={exportCSV} 
            title="Esporta piano in CSV"
          >
            <Download size={16} />
            <span>Esporta CSV</span>
          </button>

          <button 
            className="icon-btn" 
            onClick={() => setDarkMode(!darkMode)}
            title="Cambia tema"
          >
            {darkMode ? <Sun size={16} /> : <Moon size={16} />}
          </button>
        </div>
      </header>

      {/* Main Grid */}
      <div className="calculator-grid">
        {/* Left Column: Controls */}
        <section className="card">
          <h2 className="card-title">
            <Euro size={20} color="var(--primary)" />
            Parametri del Prestito
          </h2>

          {/* Amount input */}
          <div className="form-group">
            <div className="label-row">
              <span className="label-title">Importo Finanziamento</span>
              <span className="label-value-badge">{formatCurrency(amount)}</span>
            </div>
            <div className="input-container">
              <span className="input-prefix">€</span>
              <input 
                type="number"
                min="500"
                max="1000000"
                step="500"
                value={amount || ''}
                onChange={(e) => setAmount(Number(e.target.value))}
                className="styled-input"
                placeholder="10000"
              />
            </div>
            <div className="chips-row">
              {[5000, 10000, 25000, 50000, 100000].map(val => (
                <button 
                  key={val}
                  type="button"
                  className={`chip-btn ${amount === val ? 'active' : ''}`}
                  onClick={() => setAmount(val)}
                >
                  {val >= 1000 ? `${val / 1000}k €` : `${val} €`}
                </button>
              ))}
            </div>
          </div>

          {/* Duration */}
          <div className="form-group">
            <div className="label-row">
              <span className="label-title">Durata del Rimborso</span>
              <span className="label-value-badge">
                {durationMode === 'years' ? `${durationYears} Anni (${durationYears * 12} Mesi)` : `${durationMonths} Mesi`}
              </span>
            </div>

            <div className="unit-switch">
              <button 
                type="button" 
                className={`unit-tab ${durationMode === 'years' ? 'active' : ''}`}
                onClick={() => setDurationMode('years')}
              >
                In Anni
              </button>
              <button 
                type="button" 
                className={`unit-tab ${durationMode === 'months' ? 'active' : ''}`}
                onClick={() => setDurationMode('months')}
              >
                In Mesi
              </button>
            </div>

            {durationMode === 'years' ? (
              <>
                <div className="stepper-row">
                  <button 
                    type="button" 
                    className="step-btn" 
                    onClick={() => handleYearsChange(durationYears - 1)}
                  >
                    <Minus size={18} />
                  </button>
                  <div className="input-container" style={{ flex: 1 }}>
                    <input 
                      type="number"
                      min="1"
                      max="30"
                      value={durationYears}
                      onChange={(e) => handleYearsChange(e.target.value)}
                      className="styled-input"
                    />
                    <span style={{ fontSize: '0.9rem', color: 'var(--text-secondary)' }}>Anni</span>
                  </div>
                  <button 
                    type="button" 
                    className="step-btn" 
                    onClick={() => handleYearsChange(durationYears + 1)}
                  >
                    <Plus size={18} />
                  </button>
                </div>
                <div className="chips-row">
                  {[2, 3, 5, 7, 10, 15, 20].map(yr => (
                    <button 
                      key={yr}
                      type="button"
                      className={`chip-btn ${durationYears === yr ? 'active' : ''}`}
                      onClick={() => handleYearsChange(yr)}
                    >
                      {yr} {yr === 1 ? 'anno' : 'anni'}
                    </button>
                  ))}
                </div>
              </>
            ) : (
              <>
                <div className="stepper-row">
                  <button 
                    type="button" 
                    className="step-btn" 
                    onClick={() => handleMonthsChange(durationMonths - 6)}
                  >
                    <Minus size={18} />
                  </button>
                  <div className="input-container" style={{ flex: 1 }}>
                    <input 
                      type="number"
                      min="6"
                      max="360"
                      step="6"
                      value={durationMonths}
                      onChange={(e) => handleMonthsChange(e.target.value)}
                      className="styled-input"
                    />
                    <span style={{ fontSize: '0.9rem', color: 'var(--text-secondary)' }}>Mesi</span>
                  </div>
                  <button 
                    type="button" 
                    className="step-btn" 
                    onClick={() => handleMonthsChange(durationMonths + 6)}
                  >
                    <Plus size={18} />
                  </button>
                </div>
                <div className="chips-row">
                  {[24, 36, 48, 60, 84, 120].map(m => (
                    <button 
                      key={m}
                      type="button"
                      className={`chip-btn ${durationMonths === m ? 'active' : ''}`}
                      onClick={() => handleMonthsChange(m)}
                    >
                      {m} mesi
                    </button>
                  ))}
                </div>
              </>
            )}
          </div>

          {/* Interest Rate */}
          <div className="form-group" style={{ marginBottom: 0 }}>
            <div className="label-row">
              <span className="label-title">Tasso Annuo (TAN %)</span>
              <span className="label-value-badge">{formatPercent(interestRate)}</span>
            </div>

            <div className="stepper-row">
              <button 
                type="button" 
                className="step-btn" 
                onClick={() => setInterestRate(Math.max(0, +(interestRate - 0.25).toFixed(2)))}
              >
                <Minus size={18} />
              </button>
              <div className="input-container" style={{ flex: 1 }}>
                <input 
                  type="number"
                  min="0"
                  max="25"
                  step="0.05"
                  value={interestRate}
                  onChange={(e) => setInterestRate(Math.max(0, Number(e.target.value)))}
                  className="styled-input"
                />
                <span style={{ fontSize: '1rem', fontWeight: '700', color: 'var(--text-secondary)' }}>%</span>
              </div>
              <button 
                type="button" 
                className="step-btn" 
                onClick={() => setInterestRate(+(interestRate + 0.25).toFixed(2))}
              >
                <Plus size={18} />
              </button>
            </div>

            <input 
              type="range"
              min="0"
              max="15"
              step="0.1"
              value={interestRate}
              onChange={(e) => setInterestRate(Number(e.target.value))}
              className="styled-slider"
            />

            <div className="chips-row">
              {[2.5, 3.8, 5.0, 6.5, 7.9, 9.9].map(r => (
                <button 
                  key={r}
                  type="button"
                  className={`chip-btn ${interestRate === r ? 'active' : ''}`}
                  onClick={() => setInterestRate(r)}
                >
                  {r}%
                </button>
              ))}
            </div>
          </div>
        </section>

        {/* Right Column: Results Hero */}
        <section>
          <div className="hero-card">
            <div className="hero-installment-label">Rata Mensile Costante</div>
            <div className="hero-installment-value">
              {formatCurrency(result.monthlyPayment)}
            </div>
            <div className="hero-subtext">
              Per {currentTotalMonths} rate mensili al {formatPercent(interestRate)} TAN
            </div>

            {/* Breakdown progress bar */}
            <div className="breakdown-bar-container">
              <div className="bar-labels">
                <span style={{ color: '#34D399' }}>● Capitale ({principalPercent}%)</span>
                <span style={{ color: '#FB923C' }}>● Interessi ({interestPercent}%)</span>
              </div>
              <div className="progress-track">
                <div 
                  className="progress-principal" 
                  style={{ width: `${principalPercent}%` }} 
                />
                <div 
                  className="progress-interest" 
                  style={{ width: `${interestPercent}%` }} 
                />
              </div>
            </div>

            {/* Stat row */}
            <div className="stat-row">
              <div className="stat-item">
                <div className="stat-item-label">Capitale Finanziato</div>
                <div className="stat-item-value">{formatCurrency(result.principal)}</div>
              </div>
              <div className="stat-item">
                <div className="stat-item-label">Totale Interessi</div>
                <div className="stat-item-value">{formatCurrency(result.totalInterest)}</div>
              </div>
              <div className="stat-item">
                <div className="stat-item-label">Totale da Rimborsare</div>
                <div className="stat-item-value">{formatCurrency(result.totalRepayment)}</div>
              </div>
              <div className="stat-item">
                <div className="stat-item-label">Durata Complessiva</div>
                <div className="stat-item-value">
                  {Math.floor(currentTotalMonths / 12)}a {currentTotalMonths % 12 > 0 ? `${currentTotalMonths % 12}m` : ''}
                </div>
              </div>
            </div>
          </div>
        </section>
      </div>

      {/* Amortization Schedule Table */}
      <section className="schedule-section">
        <div className="schedule-header">
          <div>
            <h3 style={{ fontSize: '1.25rem', fontWeight: 800 }}>Piano di Ammortamento</h3>
            <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
              Ammortamento progressivo alla francese con quota capitale crescente e quota interessi decrescente.
            </p>
          </div>

          <div className="table-view-toggle">
            <button 
              type="button"
              className={`table-toggle-btn ${tableView === 'yearly' ? 'active' : ''}`}
              onClick={() => setTableView('yearly')}
            >
              Riepilogo Annuale ({result.yearlyBreakdown.length} anni)
            </button>
            <button 
              type="button"
              className={`table-toggle-btn ${tableView === 'monthly' ? 'active' : ''}`}
              onClick={() => setTableView('monthly')}
            >
              Dettaglio Mensile ({result.monthlyBreakdown.length} rate)
            </button>
          </div>
        </div>

        <div className="table-wrapper">
          <table className="schedule-table">
            <thead>
              <tr>
                <th>{tableView === 'yearly' ? 'Anno' : 'Mese'}</th>
                <th>Rata Totale</th>
                <th>Quota Capitale</th>
                <th>Quota Interessi</th>
                <th>Debito Residuo</th>
              </tr>
            </thead>
            <tbody>
              {tableView === 'yearly' ? (
                result.yearlyBreakdown.map((item) => (
                  <tr key={item.year}>
                    <td><strong>Anno {item.year}</strong></td>
                    <td><strong>{formatCurrency(item.installment)}</strong></td>
                    <td className="td-principal">+{formatCurrency(item.principalPayment)}</td>
                    <td className="td-interest">-{formatCurrency(item.interestPayment)}</td>
                    <td><strong>{formatCurrency(item.remainingDebt)}</strong></td>
                  </tr>
                ))
              ) : (
                result.monthlyBreakdown.map((item) => (
                  <tr key={item.month}>
                    <td>Mese {item.month}</td>
                    <td>{formatCurrency(item.installment)}</td>
                    <td className="td-principal">+{formatCurrency(item.principalPayment)}</td>
                    <td className="td-interest">-{formatCurrency(item.interestPayment)}</td>
                    <td>{formatCurrency(item.remainingDebt)}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  );
}
