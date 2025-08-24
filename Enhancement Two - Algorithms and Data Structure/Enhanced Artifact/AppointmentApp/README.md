# AppointmentApp

## 📌 Overview
**AppointmentApp** is a full-stack web application for creating, viewing, deleting, and exporting appointments.  
The backend is built with **Spring Boot** and exposes a REST API, while the frontend is built with **React** for a clean, responsive user interface.  
It features **real-time separation of appointments** into *upcoming* and *previous* categories, client/server-side validation, and export options in **CSV** or **JSON** formats.

---

## ✨ Features

### 🔹 Frontend (React)
- Create new appointments by entering an ID, date, and description.
- View appointments split into **Upcoming** and **Previous** categories.
- Delete appointments directly from the interface.
- Export all appointments in CSV or JSON format.
- Client-side validation for:
  - Appointment ID length (1–10 characters)
  - Date format (`yyyy-MM-dd`)
  - Description length (1–50 characters)
  - Date cannot be before `2000-01-01`

### 🔹 Backend (Spring Boot REST API)
- Handles appointment creation, retrieval, deletion, and export.
- Stores appointments **in memory** through both HashMap and TreeMap (plan to switch to a database in the future).
- Returns descriptive validation errors for invalid data.
- Provides export endpoint for CSV or JSON output.

---

## 🛠 Backend API Endpoints

**Get all appointments**  
```http
GET /appointments
```

**Get upcoming appointments** *(dates after today)*  
```http
GET /appointments/upcoming
```

**Get previous appointments** *(dates before today)* 
```http
GET /appointments/previous
```

**Create an appointment**  
```http
POST /appointments
```
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

**Export appointments**  
```http
GET /appointments/export?format={csv|json}&scope={all|upcoming|previous}
```

---

## 📋 Prerequisites
Before running, make sure you have:
- **Java 17 or later**
- **Maven**
- **Node.js (v16+)**
- **npm** or **yarn**

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
2. Enter:
   - Appointment ID (max 10 characters)
   - Date (`yyyy-MM-dd`)
   - Description (max 50 characters)
3. Click **"Add"** to save it.
4. See appointments in:
   - **Previous Appointments** (before today)
   - **Upcoming Appointments** (today or later)
5. Click **Delete** to remove an appointment.
6. Use **Export** buttons to download CSV or JSON files for all appointments.

---

## 📚 Notes
- Data is in-memory only for now; restarting the backend clears all appointments.
- Designed for easy database integration.
- Validation is enforced both client-side and server-side.
- Export feature allows saving schedules for backups or sharing.
