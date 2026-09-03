# EnergyNest ⚡
**Your Smart Portal to Clean & Affordable Energy in Malaysia**

EnergyNest is a modern Android application designed to help Malaysian homeowners manage their renewable energy systems. From monitoring solar energy generation to selling excess power back to the grid and booking maintenance services, EnergyNest provides a centralized ecosystem for sustainable living.

---

## ✨ Key Features

### 🏠 Energy Dashboard
* **Real-time Monitoring**: Track your daily energy generation and stored battery levels.
* **Smart Sync**: Integrated **Refresh Button** for instant database updates.
* **100kWh Storage Logic**: Visualized energy scale where 1kWh = 1% for intuitive monitoring.

### 💰 Smart Sell & Financials
* **Grid Sell**: Manually or automatically discharge excess energy to the TNB grid under the 1:1 Solar ATAP program.
* **Auto-Sell Logic**: Automatically processes energy sales when battery storage exceeds 80%.
* **Secure Withdrawals**: Withdraw earnings to Bank Accounts or eWallets with strict 16-digit validation and bank selection.
* **Transaction History**: View detailed logs of all sales, assessments, and service payments.
* **PDF Receipts**: Generate and download professional PDF receipts for all successful transactions.

### 🏗️ LEGA Roof Assessment (CREAM)
* **Eligibility Check**: Submit property details (Terrace, Semi-D, Bungalow) and roof space measurements.
* **Deposit Management**: Pay a refundable deposit via TnG, Visa, or Mastercard to initiate professional yield evaluations.

### 🛠️ Services & Maintenance
* **Booking System**: Schedule professional cleaning, maintenance checks, or expert consultations.
* **Live Tracking**: Monitor the status of your service requests from "Pending" to "Confirmed."

---

## 🛠️ Technical Stack

*   **Language**: [Kotlin](https://kotlinlang.org/)
*   **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
*   **Database & Auth**: [Supabase](https://supabase.com/) (PostgreSQL + PostgREST)
*   **Networking**: Ktor Client for Supabase connectivity.
*   **Concurrency**: Kotlin Coroutines & Flow.
*   **Local Storage**: SharedPreferences for session persistence.
*   **PDF Generation**: Android `PdfDocument` API.

---

## 📊 Database Schema

The application connects to a PostgreSQL database via Supabase with the following primary tables:
- **`User`**: Core account management and address synchronization.
- **`Payment`**: Global transaction ledger with UUID reference numbers.
- **`Smart_Sell`**: Record of energy sales and accumulated credits.
- **`Floor_usage`**: Real-time energy consumption breakdown per house floor.
- **`Home`**: Historical energy stats and battery levels.
- **`Booking`**: Appointment scheduling for services.
- **`Property`**: Linked details for solar installations.

---

## 🚀 Getting Started

1.  **Clone the Repository**:
    ```bash
    git clone https://github.com/teatimesxd/EnegyNest.git
    ```
2.  **Configure Supabase**:
    Update the `SupabaseClient.kt` file with your project URL and Anon Key.
3.  **Build & Run**:
    Open the project in **Android Studio (Ladybug or newer)** and run on an emulator or physical device (API 26+).

---

## 🔧 Recent Improvements
- ✅ **Optimized Storage Scale**: Switched to a 100kWh maximum capacity logic.
- ✅ **Bank Validation**: Added strict 16-digit checking for secure withdrawals.
- ✅ **Dynamic Floor Sync**: Connected the Power Usage card to real-time database records.
- ✅ **Floating Point Fix**: Resolved precision issues when withdrawing small credit amounts (e.g., 0.27 RM).

---

## 📄 License
This project is for educational and portfolio purposes.