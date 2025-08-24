# AppointmentApp

## 📌 Overview
AppointmentApp is a full-stack web application for managing appointments.
The backend is built with Spring Boot and exposes a REST API, while the frontend is built with React for a modern, responsive user interface.
You can create, view, and delete appointments, with validation on both the frontend and backend to ensure correct data entry.

---

## ✨ Features

### 🔹 Frontend (React)
- Create new appointments by entering an ID, date, and description.
- View appointments in two sections: Upcoming and Previous.
- Delete appointments directly from the list.
- Client-side validation for ID length, date format, and description length.

### 🔹 Backend (Spring Boot REST API)
- Handles appointment creation, retrieval, and deletion.
- Stores appointments in memory (ready for future database integration).
- Returns descriptive validation errors when data is invalid.

---

## 🛠 Backend API Endpoints

Get all appointments:
```http
  GET /appointments
```

Get upcoming appointments (dates after today):
```http
  GET /appointments/upcoming
```

Get previous appointments (dates before today):
```http
  GET /appointments/previous
```

Create an appointment:
```http
  POST /appointments
```
```json
  Request Body (JSON):
    {
      "appointmentId": "12345",
      "appointmentDate": "2025-08-10",
      "description": "Doctor Visit"
    }
```

Delete an appointment by ID:
```http
  DELETE /appointments/{id}
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
- Open the frontend in your browser.
- Enter an Appointment ID (max 10 characters), date, and description (max 50 characters).
- Click "Add Appointment" to save it.
- View your appointments in the "Upcoming Appointments" or "Previous Appointments" lists.
- Click the red "Delete" button to remove an appointment.
- If you enter invalid data, an error popup will explain the issue.

---

## 📚 Notes
- Data is stored in memory only. Restarting the backend will reset all appointments.
- The application is designed for easy integration with a database in the future.
- Backend validation ensures that invalid data is never accepted.