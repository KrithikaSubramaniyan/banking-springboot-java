# Bank Management System

## Overview

**Bank Management System** is a **Spring Boot** application designed to manage banking operations for **individual and corporate accounts**. It includes **account management, transaction processing, and guardian linking** for minors.

## Key Features

### 1. **Account Creation and Management**
- **Corporate and Individual Accounts**
- **Account Holder Details**
- **Joint Accounts**

### 2. **Account Status Control**
- **Freeze/Unfreeze/Close Accounts**

### 3. **Loan Calculation**
- **Loan Eligibility**

### 4. **Guardian and Minor Account Management**
- **Link Guardians to Minors**
- **Guardian-Minor Relationship**

### 5. **User Profile Management**
- **User Creation and Updates**:
- **Validation for Minors**

### 6. **Transaction Management**
- **Transaction History**
- **Transactions based on types(WITHDRAWAL/DEPOSIT)**:

## Installation
**Build and Run the Application**:
- Make sure you have **Java 17** (or newer) installed.
- Use **Maven** to build the project.
  ```bash
  mvn clean install
  mvn spring-boot:run
  ```

## APIs

The system exposes several REST APIs for user and account management, as well as loan calculation and guardian linking.

### **User Management**
- **POST** `/api/v1/user` - Create a new user.
- **PUT** `/api/v1/user/{id}` - Update an existing user.

### **Account Management**
- **POST** `/api/v1/account/individual` - Open a new individual account.
- **POST** `/api/v1/account/corporate` - Open a new corporate account.
- **POST** `/api/v1/account/add-joint-account/{accountId}` - Add a joint account to an existing individual account.
- **POST** `/api/v1/account/{accountId}/add-authorized-user` -  Add an authorized user to a corporate account.
- **PUT** `/api/v1/account/freeze-account/{accountId}` - Freeze an account (either individual or corporate).
- **PUT** `/api/v1/account/close-account/{accountId}` - Close an account (either individual or corporate).
- **PUT** `/api/v1/account/unfreeze-account/{accountId}` - Unfreeze an account (either individual or corporate).

### **Loan Calculation**
- **POST** `/api/v1/loan/{userId}` - Calculate loan eligibility for a user.

### **Guardian-Minor Relationships**
- **POST** `/api/v1/parent-guardian/link` - Link a guardian to a minor.
- **GET** `/api/v1/parent-guardian/minor/{minorId}` - Get the list of guardians for a minor.
- **GET** `/api/v1/parent-guardian/guardian/{guardianId}` - Get the list of minors for a guardian.

### **Transactions**
- **POST** `/api/v1/transaction/deposit-amount/{accountId}` - Deposit amount into an account (individual or corporate).
- **POST** `/api/v1/transaction/withdraw-amount/{accountId}` - Withdraw amount from an account (individual or corporate).
- **GET** `/api/v1/transaction/{accountId}` - Get all transactions for an account (individual or corporate).

## Technologies Used

- **Spring Boot**: Backend framework for building RESTful services.
- **Hibernate/JPA**: ORM to handle database interactions.
- **H2 Database**: In-memory database used for development and testing. Data is stored temporarily during the application runtime.
- **Lombok**: Simplifies code with annotations for getters, setters, constructors, etc.
- **JUnit**: For unit testing the service layers.