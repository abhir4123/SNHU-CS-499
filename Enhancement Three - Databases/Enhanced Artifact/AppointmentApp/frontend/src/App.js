// Top-level SPA routing and main pages (Home, Login, Register).
// Provides a lightweight auth context (JWT token) and fetches data from the backend.

import React, { useEffect, useState, useContext, createContext } from "react";
import { BrowserRouter, Routes, Route, Link, useNavigate } from "react-router-dom";
import axios from "axios";
import "./App.css";

// Simple auth context to share JWT and helpers across pages
const AuthContext = createContext();
const API = "http://localhost:8080";

export default function App() {
  // Keep token in localStorage so auth persists across refreshes
  const [token, setTokenState] = useState(() => localStorage.getItem("token"));
  const setToken = (t) => {
    if (t) {
      localStorage.setItem("token", t);
      setTokenState(t);
    } else {
      localStorage.removeItem("token");
      setTokenState(null);
    }
  };
  const value = { token, authed: Boolean(token), setToken, clearToken: () => setToken(null) };

  // SPA routes: home, login, register
  return (
    <AuthContext.Provider value={value}>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
        </Routes>
      </BrowserRouter>
    </AuthContext.Provider>
  );
}

function Home() {
  // authed = true when we have a token (controls visibility of write actions)
  const { authed, token } = useContext(AuthContext);
  const [upcoming, setUpcoming] = useState([]);
  const [previous, setPrevious] = useState([]);

  // add form
  const [id, setId] = useState("");
  const [date, setDate] = useState("");
  const [description, setDescription] = useState("");

  // export controls (single button + format/scope dropdowns)
  const [expFormat, setExpFormat] = useState("csv"); // csv | json
  const [expScope, setExpScope] = useState("all");   // all | upcoming | previous | range
  const [rangeStart, setRangeStart] = useState("");
  const [rangeEnd, setRangeEnd] = useState("");

  // Load appointments for both columns on first render
  const fetchAppointments = async () => {
    try {
      const [u, p] = await Promise.all([
        axios.get(`${API}/appointments/upcoming`),
        axios.get(`${API}/appointments/previous`),
      ]);
      setUpcoming(u.data);
      setPrevious(p.data);
    } catch {
      alert("Failed to load appointments. Is the backend running?");
    }
  };

  useEffect(() => { fetchAppointments(); }, []);

  // Client-side validation mirrors backend rules for nicer UX
  const addAppointment = async () => {
    if (!authed) { alert("Please log in to add appointments."); return; }
    if (!id || !date || !description) { alert("Please fill in ID, date, and description."); return; }
    if (id.trim().length < 1 || id.trim().length > 10) { alert("Appointment ID must be 1–10 characters."); return; }
    if (description.trim().length < 1 || description.trim().length > 50) { alert("Description must be 1–50 characters."); return; }
    const earliest = "2000-01-01";
    if (!/^\d{4}-\d{2}-\d{2}$/.test(date) || date < earliest) { alert("Please use yyyy-MM-dd and a date ≥ 2000-01-01."); return; }

    try {
      await axios.post(`${API}/appointments`, {
        appointmentId: id.trim(), appointmentDate: date, description: description.trim(),
      }, { headers: { Authorization: `Bearer ${token}` } });
      setId(""); setDate(""); setDescription("");
      fetchAppointments();
    } catch (err) {
      alert(err.response?.data?.error || "Error adding appointment.");
    }
  };

  // Delete flow (auth required); refresh lists on success
  const deleteAppointment = async (appointmentId) => {
    if (!authed) { alert("Please log in to delete appointments."); return; }
    try {
      await axios.delete(`${API}/appointments/${appointmentId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      fetchAppointments();
    } catch (err) {
      alert(err.response?.data?.error || "Error deleting appointment.");
    }
  };

  // Unified export: supports all/upcoming/previous/range (exclusive)
  const exportData = async () => {
    try {
      let url = `${API}/appointments/export?format=${encodeURIComponent(expFormat)}&scope=${encodeURIComponent(expScope)}`;

      if (expScope === "range") {
        if (!rangeStart || !rangeEnd) {
          alert("Please provide both start and end dates for range export.");
          return;
        }

        // Dates are yyyy-MM-dd, so string compare works. Exclusive bounds: start must be < end.
        if (rangeStart >= rangeEnd) {
          alert(
            "Invalid range: the Start date must be strictly before the End date for an exclusive range."
          );
          return;
        }

        url += `&start=${encodeURIComponent(rangeStart)}&end=${encodeURIComponent(rangeEnd)}`;
      }

      const res = await axios.get(url, { responseType: "blob" });
      const blob = new Blob([res.data], { type: expFormat === "csv" ? "text/csv" : "application/json" });
      const a = document.createElement("a");
      a.href = URL.createObjectURL(blob);
      a.download = `appointments-${expScope}.${expFormat}`;
      a.click();
      URL.revokeObjectURL(a.href);
    } catch (err) {
      // Surface backend message when available, otherwise show a generic fallback.
      alert(err.response?.data?.error || "Export failed.");
    }
  };

  return (
    <div className="App">
      <Header />
      <h1>Appointments</h1>

      {/* Compact export controls: one button + two dropdowns + (optional) date inputs */}
      <div className="export-controls">
        <button className="export-main-btn" onClick={exportData}>Export Data</button>

        <label className="export-label">
          Format
          <select className="export-select" value={expFormat} onChange={(e) => setExpFormat(e.target.value)}>
            <option value="csv">CSV</option>
            <option value="json">JSON</option>
          </select>
        </label>

        <label className="export-label">
          Scope
          <select
            className="export-select"
            value={expScope}
            onChange={(e) => setExpScope(e.target.value)}
          >
            <option value="all">All</option>
            <option value="upcoming">Upcoming</option>
            <option value="previous">Previous</option>
            <option value="range">Range (exclusive)</option>
          </select>
        </label>

        {expScope === "range" && (
          <>
            <label className="export-label">
              Start
              <input
                type="date"
                className="range-input"
                value={rangeStart}
                onChange={(e) => setRangeStart(e.target.value)}
                aria-label="Range start (exclusive)"
              />
            </label>
            <label className="export-label">
              End
              <input
                type="date"
                className="range-input"
                value={rangeEnd}
                onChange={(e) => setRangeEnd(e.target.value)}
                aria-label="Range end (exclusive)"
              />
            </label>
          </>
        )}
      </div>

      {/* Add-only when authenticated; lists are always visible */}
      {authed ? (
        <div className="form-container">
          <input placeholder="ID" value={id} maxLength={10} onChange={(e) => setId(e.target.value)} className="id-input" />
          <input type="date" value={date} onChange={(e) => setDate(e.target.value)} className="date-input" />
          <input placeholder="Description" value={description} maxLength={50} onChange={(e) => setDescription(e.target.value)} className="description-input" />
          <button className="add-btn" onClick={addAppointment}>Add</button>
        </div>
      ) : (
        <p><em>Log in to add or delete appointments.</em></p>
      )}

      {/* Two columns: previous (left) and upcoming (right) */}
      <div className="columns">
        <div className="column">
          <h2>Previous Appointments</h2>
          {previous.map((a) => (
            <div key={a.appointmentId} className="appointment-card">
              <span>{a.appointmentDate} - {a.description}</span>
              {authed && <button className="delete-btn" onClick={() => deleteAppointment(a.appointmentId)}>Delete</button>}
            </div>
          ))}
        </div>
        <div className="column">
          <h2>Upcoming Appointments</h2>
          {upcoming.map((a) => (
            <div key={a.appointmentId} className="appointment-card">
              <span>{a.appointmentDate} - {a.description}</span>
              {authed && <button className="delete-btn" onClick={() => deleteAppointment(a.appointmentId)}>Delete</button>}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

function LoginPage() {
  // Minimal form to exchange credentials for a JWT, then store it
  const { setToken } = useContext(AuthContext);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const nav = useNavigate();

  const onLogin = async () => {
    if (!email) { alert("Email is required."); return; }
    if (!/^\S+@\S+\.\S+$/.test(email)) { alert("Please enter a valid email address."); return; }
    if (!password) { alert("Password is required."); return; }
    if (password.length < 8) { alert("Password must be at least 8 characters."); return; }

    try {
      const res = await axios.post(`${API}/auth/login`, { email, password });
      setToken(res.data.token);
      nav("/");
    } catch (err) {
      alert(err.response?.data?.error || "Invalid email or password.");
    }
  };

  const onCancel = () => nav("/");

  return (
    <div className="App">
      <Header />
      <h1>Log In</h1>
      <div className="form-container">
        <input placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
        <input placeholder="Password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
        <button className="add-btn" onClick={onLogin}>Log In</button>
        <button className="delete-btn" onClick={onCancel}>Cancel</button>
      </div>
    </div>
  );
}

function RegisterPage() {
  // Creates a user, then logs them in to keep UX smooth
  const { setToken } = useContext(AuthContext);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const nav = useNavigate();

  const onRegister = async () => {
    if (!email) { alert("Email is required."); return; }
    if (!/^\S+@\S+\.\S+$/.test(email)) { alert("Please enter a valid email address."); return; }
    if (!password) { alert("Password is required."); return; }
    if (password.length < 8) { alert("Password must be at least 8 characters."); return; }

    try {
      await axios.post(`${API}/auth/register`, { email, password });
      const login = await axios.post(`${API}/auth/login`, { email, password });
      setToken(login.data.token);
      nav("/");
    } catch (err) {
      alert(err.response?.data?.error || "Registration failed.");
    }
  };

  const onCancel = () => nav("/");

  return (
    <div className="App">
      <Header />
      <h1>Register</h1>
      <div className="form-container">
        <input placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
        <input placeholder="Password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
        <button className="add-btn" onClick={onRegister}>Register & Login</button>
        <button className="delete-btn" onClick={onCancel}>Cancel</button>
      </div>
    </div>
  );
}

function Header() {
  // Header shows Login/Register when logged out, and Log Out when logged in
  const { authed, clearToken } = useContext(AuthContext);
  const nav = useNavigate();
  const logout = () => { clearToken(); nav("/"); };
  return (
    <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 16 }}>
      <div style={{ display: "flex", gap: 12 }}>
        {!authed && <Link to="/login">Login</Link>}
        {!authed && <Link to="/register">Register</Link>}
      </div>
      {authed && <button className="delete-btn" onClick={logout}>Log Out</button>}
    </div>
  );
}
