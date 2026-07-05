# QuickMart — Web-Based Supermarket Ordering System

##  Overview

QuickMart is a web-based supermarket ordering system developed as part of the **Software Engineering (SE2030)** module for the **B.Sc. (Hons) in Information Technology** degree at the **Sri Lanka Institute of Information Technology (SLIIT)**.

The system digitizes supermarket operations by enabling customers to shop online while providing separate management dashboards for administrators, inventory staff, marketers, payment handlers, and delivery coordinators.

---

## What the Project Does

QuickMart is a full-stack web application built using Spring Boot that simulates a real-world e-commerce supermarket system.

### Customer Features
- User registration and login
- Product browsing and search
- Add products to cart
- Manage quantities and view totals
- Place orders and view order history
- Track order status

### Admin Features
- Manage users and roles
- Role-based access control
- View system dashboard

### Inventory Management
- Add, update, and delete products
- Monitor stock levels
- Handle product availability
- Manage inventory data

### Payment System
- Supports Card, PayPal, and Cash on Delivery (COD)
- Secure payment processing using strategy-based design
- Transaction tracking and logging

### Delivery Management
- Assign drivers to orders
- Track delivery status
- Update dispatch and delivery progress

### Marketing Management
- Create and manage promotions
- Schedule discount campaigns
- Display promotional content dynamically

---

## Why the Project is Useful

QuickMart demonstrates how a complete e-commerce system can be built using modern software engineering principles.

### Benefits for Users
- Convenient online shopping experience
- Real-time product availability
- Secure payment options
- Easy order tracking

### Benefits for Businesses
- Centralized supermarket management system
- Improved inventory control
- Reduced manual workload and errors
- Better customer engagement through promotions
- Efficient delivery coordination
- Secure role-based system access

### Academic Value
- Demonstrates real-world software engineering practices
- Implements layered architecture (Controller, Service, Repository)
- Uses design patterns (e.g., Strategy Pattern for payments)
- Applies Spring Boot and Spring Security concepts

---

## Tech Stack

### Frontend
- HTML
- CSS
- JavaScript
- Thymeleaf (server-side rendering)

### Backend
- Java
- Spring Boot
- Spring Security

### Database
- MySQL

### Architecture
- MVC (Model-View-Controller)
- Layered Architecture

---

## Getting Started

### Prerequisites
- Java JDK 17+
- Maven
- MySQL Server
- Git

---

### Installation Steps

Clone the repository:
```bash
git clone https://github.com/your-username/QuickMart.git


### Project Structure

  
QuickMart/
├── src/
│   ├── main/
│   │   ├── java/g115/quickmart/
│   │   │   ├── Controller/   # Handles HTTP requests
│   │   │   ├── Model/        # Entity classes
│   │   │   ├── Repo/         # Database repositories
│   │   │   ├── Service/      # Business logic
│   │   │   ├── Strategy/     # Payment strategy pattern
│   │   │   └── QuickMartApplication.java
│   │   │
│   │   └── resources/
│   │       ├── static/       # Images and static files
│   │       ├── templates/    # HTML views (Thymeleaf)
│   │       └── application.properties
│   │
│   └── test/
│       └── java/
│
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md

### 👥 The Team

* **J.G.H. Jayalath** (Product Owner) — User Management Portal & Role-Based Authentication.
* **S.A.W. Fernando** (Scrum Master) — Customer Portal, Profile Logs & Shopping Cart Features.
* **B.M.B.S. Alahakoon** — Inventory Management, Concurrency Control & Stock Synchronization.
* **N. Thahani** — Marketing Management, Promotional Scheduling & Dynamic Renderings.
* **M.G.M. Sanvidu** — Payment Portal Gateway Integration & Secure Transaction Control.
* **P.A.S. Dinod** — Delivery Assignment Logic, Driver Modules & Notification Flows.

## 📝 Project Context
Developed for the **Software Engineering (SE2030)** module under the B.Sc. (Hons) in Information Technology degree at the Sri Lanka Institute of Information Technology (SLIIT).
