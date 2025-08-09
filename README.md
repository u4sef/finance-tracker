# Ledger — Personal Finance (CAD)

![Java](https://img.shields.io/badge/Java-21-blue?logo=java&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-UI-green)
![Gradle](https://img.shields.io/badge/Build-Gradle-02303A?logo=gradle)
![SQLite](https://img.shields.io/badge/Database-SQLite-blue?logo=sqlite)
![Status](https://img.shields.io/badge/Status-Building%20MVP-yellow)

A clean, offline desktop app for tracking accounts, transactions, and budgets.  
Built with **Java 21**, **JavaFX**, and **SQLite**. All data stays on your device.

> Status: Building the **visual MVP**. Core screens scaffolded; features added incrementally.

---

## 🚀 Run
1. Open in **IntelliJ IDEA** with **JDK 21** (Eclipse Temurin recommended).
2. Let IntelliJ sync Gradle automatically.
3. Run: **Gradle → Tasks → application → run** (or right-click `MainApp` → Run).

💡 If JavaFX errors appear, ensure you’re running via **Gradle**.

---

## 📂 Data Location
- Database file:  
  `C:\Users\<you>\AppData\Local\Ledger\ledger.db`
- Seeded on first run with:
    - Accounts: Chequing, Cash, Credit (CAD)
    - Categories: Salary, Rent, Groceries, etc.

---

## 🗺 Roadmap — MVP v0.1.0
- [ ] **Transactions:** read-only table view
- [ ] **Transactions:** add/edit dialog
- [ ] **Accounts & Categories:** management UI
- [ ] **Budgets:** monthly limits + progress bars
- [ ] **Reports (basic):** pie + bar charts
- [ ] **CSV export:** transactions
- [ ] **Settings:** theme toggle, data folder

Progress is tracked in **GitHub Issues** & **Milestone: MVP v0.1.0**.

---

## 🛠 Tech Stack
- Java 21 (LTS)
- JavaFX
- SQLite (local storage)
- Gradle
- Windows target (cross-platform later)

---

## 📌 Development Workflow
- **Branch per feature:** `feature/<issue>-<slug>` (e.g., `feature/2-transactions-table`)
- **Conventional Commits:** `feat:`, `fix:`, `docs:`, `refactor:`, `chore:`
- **Pull Requests:** include screenshots for UI changes + “Closes #<issue>”
- **Merges:** squash for tidy history

---
