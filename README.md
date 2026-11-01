# Calcolo Rata - Ammortamento alla Francese

Simulatore completo per il calcolo della rata del prestito o finanziamento con piano di ammortamento alla francese, ripartizione capitale/interessi, esportazione CSV e stampa PDF.

## 🚀 Distribuzione su Vercel

Questo progetto è già predisposto per il deploy immediato su **Vercel**:

### Opzione 1: Da GitHub a Vercel (Consigliata)
1. Esporta o sincronizza questo repository su **GitHub** (tramite il pulsante GitHub in alto a destra).
2. Vai su [Vercel](https://vercel.com) e clicca su **"Add New..."** > **"Project"**.
3. Seleziona il repository GitHub importato.
4. Vercel rileverà automaticamente la configurazione grazie al file `vercel.json` presente nel progetto:
   - **Framework Preset**: `Vite`
   - **Build Command**: `npm run build` (o `vite build`)
   - **Output Directory**: `dist`
5. Clicca su **"Deploy"**: in pochi secondi la tua applicazione web sarà online su `https://tuo-progetto.vercel.app`.

### Opzione 2: Tramite Vercel CLI
Se hai installato la Vercel CLI sul tuo terminale locale:
```bash
npm i -g vercel
vercel
```

---

## 🛠️ Funzionalità Web App
- **Importo (€)**: Input libero e selettori rapidi (5k, 10k, 25k, 50k, 100k).
- **Durata**: Commutabile tra **Anni** e **Mesi** con pulsanti +/- e chip rapidi.
- **Tasso Annuo (TAN %)**: Slider continuo, variazione fine (±0,25%) e input manuale.
- **Rata Costante & Grafico Proporzioni**: Visualizzazione immediata della rata mensile e barra percentuale Capitale vs Interessi.
- **Piano di Ammortamento Dettagliato**: Tabella consultabile in modalità **Riepilogo Annuale** o **Dettaglio Mensile**.
- **Esportazione & Stampa**: Pulsante per generare/scaricare il file CSV e layout ottimizzato per la stampa o salvataggio PDF.
- **Dark Mode**: Modalità chiara e scura integrata.
