# 💰 Expense Tracker Web Application

## 📌 Introduction
The **Expense Tracker Web Application** is a full-stack financial management system that allows users to track income, expenses, budgets, and savings goals. It provides a secure and modern user experience with both traditional authentication and social login integration.

---

## 🎯 Objective
- Help users manage personal finances effectively
- Provide clear visibility into spending habits
- Support budgeting and financial goal tracking
- Deliver a secure and scalable full-stack application

---

## 💡 Inspiration
Managing daily expenses manually is inefficient and error-prone. This project was inspired by the need for a simple, centralized system where users can:
- Track transactions
- Monitor budgets
- Visualize financial data

---

## ❗ Problem Statement
Many people struggle with:
- Tracking where their money goes
- Staying within budget
- Managing multiple accounts and transactions
- Lack of simple tools for financial awareness

---

## 🚀 Features

### 🔐 Authentication & Security
- Email & Password Login/Register
- Google Login (OAuth2)
- Facebook Login (OAuth2)
- JWT-based authentication with HttpOnly cookies
- Role-based access (User/Admin)

---

### 👤 User Management
- View and update profile
- Upload profile image
- Account activation/deactivation (Admin)

---

### 💳 Accounts
- Create multiple accounts (Cash, Bank, Card, etc.)
- Track balances
- Multi-currency support

---

### 🏷️ Categories
- Default categories shared across users
- Custom categories (via drawer/sheet UI)
- Lightweight and simple creation

---

### 💸 Transactions
- Add income & expense transactions
- Upload multiple images (Cloudinary)
- Filter and view transaction history
- Associate transactions with accounts and categories

---

### 📊 Budgets
- Set budgets per category
- Track spending against budget
- Monthly budget overview

---

### 🎯 Goals
- Create financial goals
- Track progress
- Upload goal-related images

---

### 📈 Reports & Dashboard
- Overview of income and expenses
- Financial summaries
- Filtered reports for better insights

---

### 🛠️ Admin Panel
- Manage users
- Update roles (USER / ADMIN)
- Activate / deactivate accounts

---

## 🧰 Technologies Used

### 🖥️ Frontend
- **Next.js (App Router)**
- **RTK Query (Redux Toolkit)**
- **React Hook Form + Zod**
- **shadcn/ui**
- **Tailwind CSS**

---

### ⚙️ Backend
- **Spring Boot**
- **Spring Security**
- **JWT Authentication**
- **OAuth2 (Google & Facebook)**
- **JPA / Hibernate**
- **PostgreSQL**

---

### ☁️ External Services
- **Cloudinary** (Image Upload)
- **Google OAuth**
- **Facebook OAuth**
- **Railway (for backend deployment)**
- **Vercel (for frontend deployment)**

---

## 🔐 Authentication Flow

### Local Login
Frontend → Spring Boot → JWT → Cookie → Authenticated Requests
### Social Login (Google / Facebook)
Frontend → OAuth Provider → Spring Boot → JWT → Cookie → Dashboard
All authentication methods share the same JWT system for consistency.

---

## 🧠 Architecture Overview
Frontend (Next.js)  
↓  
REST API (Spring Boot)  
↓  
Database (PostgreSQL)

- Stateless backend using JWT
- Secure cookie-based authentication
- Clean separation of concerns

---

## 🔮 Future Improvements
- Refresh token system
- Email verification
- Password reset
- Advanced analytics & charts
- Mobile responsiveness enhancements
- Account linking UI (connect/disconnect providers)

---

## ✅ Conclusion
This project demonstrates a **complete full-stack application** with:

- Secure authentication (JWT + OAuth2)
- Scalable backend architecture
- Modern frontend practices
- Real-world financial tracking features

It reflects practical implementation of:
- Authentication systems
- API design
- State management
- UI component architecture

---

## 👨‍💻 Author
Developed as a full-stack project focusing on:
- Software Engineering
- Web Development
- Secure Authentication Systems
