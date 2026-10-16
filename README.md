# NETVEX - Internet Billing Management System (MySQL & NetBeans Integration Guide)

**NETVEX** is a desktop-based Internet Billing Management System built using **Java Swing**, developed in **Apache NetBeans**, and connected to a local **MySQL Server** via **JDBC (MySQL Connector/J 8.3.0)**. Designed for Internet Service Providers (ISPs) to manage subscribers, broadband plans, monthly data usage, automated billing calculations, and simulated online customer payments.

---

## Architecture & Technology Stack

```
   ┌────────────────────────────────────────────────────────┐
   │         Apache NetBeans Java Swing Application         │
   │  (Admin Console, Subscriber Portal, Billing & Payment) │
   └───────────────────────────┬────────────────────────────┘
                               │
                               ▼
   ┌────────────────────────────────────────────────────────┐
   │             JDBC (MySQL Connector/J 8.3.0)             │
   │           Centralized: DBConnection.java               │
   └───────────────────────────┬────────────────────────────┘
                               │
                               ▼
   ┌────────────────────────────────────────────────────────┐
   │         Local MySQL Server (Port 3307 / 3306)          │
   └───────────────────────────┬────────────────────────────┘
                               │
                               ▼
   ┌────────────────────────────────────────────────────────┐
   │              Database: InternetBillingDB               │
   │                                                        │
   │  ┌───────────┐  ┌─────────────┐  ┌──────────────────┐  │
   │  │   admin   │  │    plans    │  │    customers     │  │
   │  └───────────┘  └─────────────┘  └──────────────────┘  │
   │  ┌───────────┐  ┌─────────────┐  ┌──────────────────┐  │
   │  │   usage   │  │    bills    │  │     payments     │  │
   │  └───────────┘  └─────────────┘  └──────────────────┘  │
   └────────────────────────────────────────────────────────┘
```

- **Programming Language:** Java (JDK 11, 17, or 21+)
- **GUI Framework:** Java Swing (Professional Dark Navy `#0F2747` & Blue `#1976D2` ISP theme)
- **IDE:** Apache NetBeans (Ant-based project configuration)
- **Database Driver:** MySQL Connector/J 8.3.0 (`mysql-connector-j-8.3.0.jar`)
- **Database Engine:** MySQL Server 8.0 (Service: `MySQL80`, Port: `3307` / `3306`)
- **Database Tool:** MySQL Workbench

---

## Complete Step-by-Step Setup & Integration Guide

### 1. Starting MySQL Server

Before launching the Java application, verify that your local MySQL Server service is running.

#### Option A: Via Windows Services
1. Press `Win + R`, type `services.msc`, and press **Enter**.
2. Scroll down to locate **MySQL80** (or your named MySQL service).
3. Check the **Status** column:
   - If it is not running, right-click **MySQL80** and click **Start**.
   - Ensure the **Startup Type** is set to **Automatic**.

#### Option B: Via Windows PowerShell (Administrator)
Run the following command to check and start the service:
```powershell
Get-Service *mysql*
Start-Service MySQL80
```

#### Option C: Via Command Prompt (Administrator)
```cmd
net start MySQL80
```

---

### 2. Opening MySQL Workbench

1. Click the Windows **Start Menu**, search for **MySQL Workbench**, and launch the application.
2. In the **MySQL Connections** home screen, click on your local connection:
   - Connection name: **Local instance MySQL80**
   - Host: `localhost`
   - Port: `3307` (or `3306` if configured on default port)
   - Username: `root`
3. Enter your MySQL root password when prompted and click **OK**.
4. You will be taken to the SQL Query Editor interface.

---

### 3. Creating and Importing `InternetBillingDB`

To create the database, tables, foreign key constraints, and initial ISP sample records:

1. In MySQL Workbench, go to the top menu: **File** > **Open SQL Script...** (or press `Ctrl + Shift + O`).
2. Navigate to your project directory and select:
   ```text
   database.sql
   ```
3. Inspect the SQL script. It creates:
   - `CREATE DATABASE IF NOT EXISTS InternetBillingDB;`
   - **`Admin`**: Administrator login accounts.
   - **`Plans`**: Internet broadband packages (speeds, limits, tariffs, rentals, fines).
   - **`Customers`**: Subscriber account profiles linked to their selected plan.
   - **`Usage`**: Monthly subscriber data consumption records (`UQ_Usage_Customer_Month`).
   - **`Bills`**: Computed invoices with itemized charges and payment statuses.
   - **`Payments`**: Payment transactions linked to invoices and customer accounts.
4. Execute the entire script by clicking the **Execute (⚡ Lightning Bolt)** icon on the toolbar, or press `Ctrl + Shift + Enter`.
5. In the left **Navigator** pane, switch to the **Schemas** tab and click the **Refresh** icon.
6. Verify that **`InternetBillingDB`** appears with all 6 tables:
   - `admin`
   - `customers`
   - `plans`
   - `usage`
   - `bills`
   - `payments`

---

### 4. Configuring the MySQL Root Password

The project uses a centralized properties file (`db.properties`) to store database connection settings. This decouples credentials from Java source files.

1. Open `db.properties` in any text editor.
2. Set your MySQL root password:
   ```properties
   # Database Configuration for Internet Billing Management System
   db.engine=MYSQL
   db.host=localhost
   db.port=3307
   db.name=InternetBillingDB
   db.user=root
   db.password=Guzxc123
   db.trustServerCertificate=true
   db.encrypt=false
   db.integratedSecurity=false
   ```
   *(Note: If your MySQL service is on port 3306, simply change `db.port=3306`)*
3. Save the file.
4. *(Alternative)* You can also configure the password from the running application's GUI:
   - Launch the application to the **Welcome Screen**.
   - Click the **"DB Config"** button in the top right.
   - Choose **MySQL / MySQL Workbench**.
   - Enter your password and port, then click **OK**. The application will test the connection and save `db.properties` automatically.

---

### 5. Adding the MySQL JDBC Driver to the NetBeans Project

The MySQL JDBC Driver (`mysql-connector-j-8.3.0.jar`) is bundled in the project's `lib/` directory and pre-configured in `nbproject/project.properties`.

To verify or add it manually in **Apache NetBeans**:
1. Open **Apache NetBeans**.
2. Open the project: **File** > **Open Project...** > select `InternetBillingManagementSystem`.
3. In the **Projects** panel on the left, expand **InternetBillingManagementSystem**.
4. Right-click on **Libraries** and check if `mysql-connector-j-8.3.0.jar` is listed.
5. If missing:
   - Right-click **Libraries** -> select **Add JAR/Folder...**
   - Browse to `InternetBillingManagementSystem/lib/mysql-connector-j-8.3.0.jar`
   - Click **Open**.

---

### 6. Centralized `DBConnection.java` Configuration

Database connectivity is managed exclusively by [`DBConnection.java`](file:///c:/Users/mathe/CrossDevice/ZOROK/PHY%20S1/java/InternetBillingManagementSystem/src/internetbilling/database/DBConnection.java):
- **Package:** `internetbilling.database.DBConnection`
- **Driver:** `com.mysql.cj.jdbc.Driver`
- **Connection URL:**
  ```java
  jdbc:mysql://localhost:3307/InternetBillingDB?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&connectTimeout=3000
  ```
- **Configuration Loading:** Dynamically reads `db.properties` at runtime.
- **Fail-Safe Diagnostics:** When configured for MySQL, it validates connectivity directly against MySQL Server. If the service is stopped, password is wrong (Error 1045), or database is missing (Error 1049), it reports the exact diagnostic reason instead of silently falling back to fake in-memory data.
- **Security:** All database queries across all DAO classes use parameterized `PreparedStatement` to guarantee SQL injection immunity.

---

### 7. Running the Java Swing Application

#### In Apache NetBeans IDE:
1. Right-click **InternetBillingManagementSystem** in the **Projects** tab.
2. Select **Clean and Build** (`Shift + F11`).
3. Once the build completes, right-click the project and select **Run** (`F6`).
4. The **NETVEX - Welcome Screen** will display.

#### From the Command Line / Windows Terminal:
You can also build and test directly using the provided Windows scripts:
```cmd
# Compile all Java sources
.\compile.bat

# Run the full automated verification test suite
.\test.bat

# Launch the interactive Swing GUI
.\run.bat
```

#### Default Credentials:
- **Administrator Login:**
  - Username: `admin`
  - Password: `admin123`
- **Subscriber / Customer Login:**
  - Username: `johndoe`
  - Password: `cust123`
  *(Other customer accounts: `sarahc`, `michaels`, `brucew` - all with password `cust123`)*

---

### 8. Checking Application Data in MySQL Workbench

Every change made in the Java Swing application immediately persists to MySQL. You can verify this in real-time using MySQL Workbench.

Open a new SQL tab in MySQL Workbench and run:

```sql
USE InternetBillingDB;

-- 1. Check Administrator Account
SELECT * FROM Admin;

-- 2. Check Internet Broadband Plans
SELECT * FROM Plans;

-- 3. Check All Registered Customers
SELECT * FROM Customers;

-- 4. Check Monthly Data Usage Records
SELECT * FROM `Usage`;

-- 5. Check Invoices & Billing Records
SELECT * FROM Bills;

-- 6. Check Online Payment Transactions
SELECT * FROM Payments;
```

#### Real-Time Verification Actions to Test:
1. **Add Customer in Java GUI:**
   - Log in as **Admin** -> Click **Customers** -> Fill out form -> Click **Register Customer**.
   - In MySQL Workbench: `SELECT * FROM Customers;` -> The new customer row is visible.
2. **Record Monthly Usage:**
   - Log in as **Admin** -> Click **Monthly Usage** -> Select customer and month -> Enter data used -> Click **Save Usage**.
   - In MySQL Workbench: `SELECT * FROM `Usage`;` -> The new usage row is visible.
3. **Generate Bill:**
   - In **Invoices & Bills** -> Select customer and billing month -> Click **Calculate & Generate Bill**.
   - In MySQL Workbench: `SELECT * FROM Bills;` -> The generated bill row with status `UNPAID` is visible.
4. **Make Simulated Payment:**
   - Log in as Customer (`johndoe`) -> Go to **Pay Bills Online** -> Pay the outstanding bill with simulated UPI or Card.
   - In MySQL Workbench:
     - `SELECT * FROM Payments;` -> New payment row created (`PAY-XXXXX`).
     - `SELECT * FROM Bills WHERE customer_id = 'CUST101';` -> Bill status updated to `PAID`.
5. **Dashboard Statistics:**
   - Reopen or refresh the **Admin Dashboard** -> Total Customers, Invoices, Paid/Unpaid counts, and Revenue reflect exact database values.

---

## Security & Data Integrity Highlights

- **Strict Relational Constraints:** Foreign keys ensure referential integrity between `Customers`, `Plans`, `Usage`, `Bills`, and `Payments`.
- **Prepared Statements:** No user input is ever concatenated directly into SQL queries.
- **Duplicate Prevention:** Unique constraints prevent duplicate bills or duplicate monthly usage records for the same customer and billing cycle.
