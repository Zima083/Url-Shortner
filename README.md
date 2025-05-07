# 🌐 URL Shortener Service

A **secure, role-based URL shortening service** built with **Spring Boot**, **Spring Security**, and **PostgreSQL (Docker)**.

This service allows both **guest** and **authenticated users** to create, manage, and track shortened URLs with configurable expiration, privacy settings, and click analytics.

## 🚀 Features

✅ Accept long URLs and return a **unique shortened URL**  
✅ **URL validation** (optional/configurable)  
✅ **Guest users:**
- Create **public** shortened URLs
- **30-day expiration** by default

✅ **Authenticated users:**
- Create **public or private** shortened URLs
- Set **custom expiration time**
- View and delete their own shortened URLs

✅ **Role-based login** using **Spring Security**
- **Admin users**:
  - View **all URLs** (public + private)
- **Regular users**:
  - Manage only their own URLs

✅ **Redirection:**
- Redirect to original long URL when accessed
- Handle **invalid/expired URLs** gracefully

✅ **Analytics:**
- Track **number of clicks per URL**

✅ **User Management:**
- **Register and login**
- Role-based access control (`USER`, `ADMIN`)

---

## 🏗️ Technology Stack

| Layer               | Tech                               |
|--------------------|-----------------------------------|
| Backend             | Spring Boot                        |
| Security            | Spring Security                    |
| ORM / DB Access     | Spring Data JPA (Hibernate)        |
| Database            | PostgreSQL (via Docker)            |
| Authentication     | JWT (optional) / Spring Session    |
| Build Tool          | Maven / Gradle                     |
| Deployment Ready    | Docker                             |

---

## 📦 Running the Project

### 1️⃣ **Clone the Repository**

```bash
git clone https://github.com/your-username/url-shortener-springboot.git
cd url-shortener-springboot
