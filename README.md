# Ledger — Personal Finance (CAD)

![Java](https://img.shields.io/badge/Java-21-blue?logo=java&logoColor=white)  
![JavaFX](https://img.shields.io/badge/JavaFX-UI-green)  
![Gradle](https://img.shields.io/badge/Build-Gradle-02303A?logo=gradle)  
![SQLite](https://img.shields.io/badge/Database-SQLite-blue?logo=sqlite)  
![Status](https://img.shields.io/badge/Status-MVP%20v0.1.0%20Complete-brightgreen)

A clean, offline desktop app for tracking accounts, transactions, and budgets.  
Built with **Java 21**, **JavaFX**, and **SQLite**. All data stays on your device.

> Status: **MVP v0.1.0 Complete**  
> Core functionality is implemented; future enhancements can build from here.

---

## 🚀 Run
1. Open in **IntelliJ IDEA** with **JDK 21** (Eclipse Temurin recommended).
2. Let IntelliJ sync Gradle automatically.
3. Run: **Gradle → Tasks → application → run**  
   *(or right-click `MainApp` → Run).*

💡 If JavaFX errors appear, ensure you’re running via **Gradle**.

---

## 📂 Data Location
- Database file:  
  `C:\Users\<you>\AppData\Local\Ledger\ledger.db`
- Seeded on first run with:
  - Accounts: Chequing, Cash, Credit (CAD)
  - Categories: Salary, Rent, Groceries, etc.

---

## ✅ MVP v0.1.0 Features
- **Transactions**
  - View all transactions in a table
  - Add new transactions via dialog
- **Accounts & Categories**
  - Manage (add/edit/delete) accounts
  - Manage categories for organizing spending
- **Budgets**
  - Monthly limits per category
  - Progress bars show usage vs. budget
- **CSV Export**
  - Export transactions to a `.csv` file
- **Navigation**
  - Switch between Transactions, Manage, and Budgets
- **Persistence**
  - Data stored locally in SQLite

---

## 🛠 Tech Stack
- Java 21 (LTS)
- JavaFX
- SQLite (local storage)
- Gradle
- Target: Windows (cross-platform planned)

---

## 📌 Development Workflow
- **Branch per feature:** `feature/<issue>-<slug>` (e.g., `feature/2-transactions-table`)
- **Conventional Commits:** `feat:`, `fix:`, `docs:`, `refactor:`, `chore:`
- **Pull Requests:** include screenshots for UI changes + “Closes #<issue>”
- **Merges:** squash for tidy history
