# AppointmentApp

## 📌 Overview
**AppointmentApp** is a full-stack web application designed to manage personal or professional appointments in a simple and reliable way.

The **backend** is powered by **Spring Boot** and integrates with **MongoDB** for persistent data storage, exposing a clean REST API for appointment management and authentication.  
The **frontend** is built with **React**, providing a responsive interface for creating, viewing, deleting, and exporting appointments.  

This project demonstrates **real-time categorization** of appointments (into *upcoming* and *previous*), strong **validation** on both client and server, secure **JWT-based authentication**, and flexible **export functionality** in CSV or JSON format.

Whether you are a student learning full-stack development, or a developer evaluating architectural patterns, this application serves as a clear example of how backend and frontend layers interact seamlessly.

---

## ✨ Features

### 🔹 Frontend (React)
- **Add appointments** by entering an ID, date, and description.
- **View appointments** automatically separated into:
  - **Upcoming** (today and future dates)
  - **Previous** (past dates)
- **Delete appointments** directly from the interface.
- **Export appointments** in **CSV** or **JSON** format, with scope filtering:
  - All appointments
  - Upcoming only
  - Previous only
  - Date range (exclusive start/end)
- **Client-side validation** ensures:
  - Appointment ID length between **1–10 characters**
  - Description length between **1–50 characters**
  - Dates must follow **yyyy-MM-dd** format
  - Dates must be **≥ 2000-01-01**

### 🔹 Backend (Spring Boot + MongoDB)
- REST API for managing appointments and authentication.
- Endpoints for:
  - Creating
  - Retrieving (all, upcoming, previous, by range)
  - Deleting
  - Exporting
- **Validation enforced server-side** (mirroring frontend checks).
- **MongoDB integration** for reliable data persistence.
- **JWT authentication** for secure access to protected endpoints.
- Clear error handling with descriptive JSON messages.

---

## 🛠 Backend API Endpoints

**Get all appointments**  
```http
GET /appointments
```
➡️ Public — no authentication required.

**Get upcoming appointments** *(dates today and after)*  
```http
GET /appointments/upcoming
```
➡️ Public — no authentication required.

**Get previous appointments** *(dates before today)*  
```http
GET /appointments/previous
```
➡️ Public — no authentication required.

**Get appointments in a date range (exclusive)**  
```http
GET /appointments/range?start=YYYY-MM-DD&end=YYYY-MM-DD
```
➡️ Public — no authentication required.

**Create an appointment**  
```http
POST /appointments
```
➡️ 🔒 Requires authentication (JWT token).  

Request Body (JSON):  
```json
{
  "appointmentId": "12345",
  "appointmentDate": "2025-08-10",
  "description": "Doctor Visit"
}
```

**Delete an appointment by ID**  
```http
DELETE /appointments/{id}
```
➡️ 🔒 Requires authentication (JWT token).

**Export appointments**  
```http
GET /appointments/export?format={csv|json}&scope={all|upcoming|previous|range}&start=YYYY-MM-DD&end=YYYY-MM-DD
```
➡️ Public — no authentication required.  
(*Exports can be downloaded by anyone, but data is read-only.*)

---

**Authentication Endpoints**  
- Register:  
  ```http
  POST /auth/register
  ```
  ➡️ Public — no authentication required.  

- Login:  
  ```http
  POST /auth/login
  ```
  ➡️ Public — no authentication required.  
  Returns a **JWT token** used in `Authorization: Bearer <token>` headers for protected routes.

---

## 📋 Prerequisites
Before running, make sure you have:
- **Java 17 or later**
- **Maven**
- **Node.js (v16+)**
- **npm** or **yarn**
- **MongoDB** (running locally at `mongodb://localhost:27017/appointmentapp` by default)

---

## 🚀 How to Run the Application

### 1) Clone and enter the project (Preferably with PowerShell)
```bash
cd AppointmentApp
```

### 2) Run the Backend
```bash
cd backend
./mvnw spring-boot:run
```

Backend will run at:  
👉 http://localhost:8080  

#### ⚠️ If Port 8080 Is Already in Use
Check which process is occupying port `8080`:  
```bash
netstat -ano | findstr 8080
```

You’ll see output like this:  
```plaintext
TCP    0.0.0.0:8080    0.0.0.0:0    LISTENING    12345
```
Here, **12345** is the Process ID (PID).

Kill the process using PowerShell:  
```bash
taskkill /PID 12345 /F
```
- Replace **12345** with the PID you found.  
- The `/F` flag forces termination.  

#### 🔄 Alternative: Change the Port
Instead of killing the process, you can configure the backend to use a different port.  
Edit `application.properties` and add:  
```properties
server.port=8081
```
Then the backend will run on 👉 http://localhost:8081

### 3) Run the Frontend
```bash
cd frontend
npm install
npm start
```
Frontend will run at:  
👉 http://localhost:3000  

If port `3000` is in use, React will prompt to use another.

---

## 💻 Using the Application

1. Open the frontend in your browser.  
2. **Register or Log In** using the navigation links at the top:  
   - Without logging in, you can **only view** existing appointments.  
   - Once logged in, you gain the ability to **add** and **delete** appointments.  
3. Enter appointment details:
   - **ID** (1–10 characters)  
   - **Date** (`yyyy-MM-dd`, must be ≥ 2000-01-01)  
   - **Description** (1–50 characters)  
4. Click **Add** to save the appointment.  
5. Appointments are automatically categorized:  
   - **Previous** (before today)  
   - **Upcoming** (today or later)  
6. Use **Delete** buttons to remove an appointment (available only when logged in).  
7. Use the **Export controls** to download data as CSV or JSON.  
   - You can choose *all, upcoming, previous, or range* (exclusive dates).  
8. **Log Out** any time using the button in the header. Once logged out:  
   - You return to **view-only mode**, and cannot add or delete appointments until you log in again.

---

## 🔒 Authentication
- Registration and login are handled via the backend (`/auth/register`, `/auth/login`) and surfaced in the frontend UI.  
- A **JWT token** is stored in `localStorage` after login, enabling secure API requests.  
- **Authenticated users** can:  
  - Add appointments  
  - Delete appointments  
- **Non-authenticated users** can:  
  - Only view appointments  
  - Export data (read-only feature)  
- Logging out clears the token, immediately restricting the user to **view-only mode**.

---

## 🧪 Validation
Both **client** and **server** perform checks to ensure data quality:
- Appointment IDs and descriptions respect length constraints.
- Dates must be valid and in proper format.
- Backend returns JSON error messages when validation fails.
- Frontend surfaces these errors through alerts.

---

## 📂 Data Persistence
Appointments are stored in **MongoDB** (`appointmentapp` database).  
➡️ This ensures data **persists** even if the backend is restarted.  

This design makes the application realistic for production, while still being lightweight for local development and demos.

---

## 📤 Export Feature
Appointments can be exported to:
- **CSV** (for spreadsheets)  
- **JSON** (for programmatic use)  

Scope options:
- `all` — everything  
- `upcoming` — only future appointments  
- `previous` — only past appointments  
- `range` — exclusive date range (`start < end`)

Exports are always **read-only**, available to both logged-in and logged-out users.

---

## ⚡ Technical Highlights
- **React Router** for SPA navigation (`/`, `/login`, `/register`)  
- **Axios** for API communication  
- **Context API** for lightweight global state (auth token)  
- **Blob + download anchor trick** for client-side file export  
- **Spring Boot REST API** with MongoDB persistence  
- **JWT authentication** for secured endpoints (add/delete)  
- **Clear separation of concerns**: frontend handles UI/UX, backend handles persistence and validation  

---

## 📚 Notes
- Data is stored in **MongoDB**, ensuring persistence across backend restarts.  
- Adding or deleting appointments requires being **logged in**; otherwise, the app is view-only.  
- Logging out clears your token and reverts you to **read-only mode**.  
- Validation is enforced both client-side and server-side.  
- Export feature demonstrates real-world file generation in a React + Spring Boot + MongoDB stack.  

---

## 🎓 Why This Project?
This project was built to **demonstrate full-stack skills**:
- Designing a REST API in Spring Boot
- Persisting data with MongoDB
- Consuming that API in a React frontend
- Managing authentication and validation
- Implementing a practical feature (exporting appointments)

It’s structured and documented to help others **learn and extend** the codebase.

---

## 👨‍💻 Author
Developed by **Abhilash Krishna Raj**  

This project was created for **CS-499: Computer Science Capstone** at Southern New Hampshire University.
The artifact originally came from **CS-320: Software Testing and Quality Assurance**, where I built an in-memory Appointment Service in Java.

For the capstone, I enhanced the artifact into a **full-stack web application** using **Spring Boot, React, and MongoDB**. These enhancements demonstrate modern software engineering practices such as persistent storage, RESTful APIs, modular design, and scalable architecture.  
```