import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

function App() {
  // State variables to store upcoming and previous appointments
  const [upcoming, setUpcoming] = useState([]);
  const [previous, setPrevious] = useState([]);

  // State variables for the form inputs
  const [id, setId] = useState('');
  const [date, setDate] = useState('');
  const [description, setDescription] = useState('');

  // Fetch appointments when the component first loads
  useEffect(() => {
    fetchAppointments();
  }, []);

  // Fetch upcoming and previous appointments from the backend
  const fetchAppointments = async () => {
    try {
      // Using Promise.all to fetch both lists in parallel
      const [upcomingRes, previousRes] = await Promise.all([
        axios.get('http://localhost:8080/appointments/upcoming'),
        axios.get('http://localhost:8080/appointments/previous'),
      ]);
      setUpcoming(upcomingRes.data);
      setPrevious(previousRes.data);
    } catch (err) {
      alert('Failed to load appointments. Please check if backend is running.');
    }
  };

  // Add a new appointment
  const addAppointment = async () => {
    // --- Small client-side validation ---
    if (!id || !date || !description) {
      alert('Please fill in ID, date, and description.');
      return;
    }
    if (id.trim().length < 1 || id.trim().length > 10) {
      alert('Appointment ID must be 1–10 characters.');
      return;
    }
    if (description.trim().length < 1 || description.trim().length > 50) {
      alert('Description must be 1–50 characters.');
      return;
    }
    // Validate date format (yyyy-MM-dd) and range
    const earliest = '2000-01-01';
    if (!/^\d{4}-\d{2}-\d{2}$/.test(date)) {
      alert('Please use yyyy-MM-dd for the date.');
      return;
    }
    if (date < earliest) {
      alert('Appointment date cannot be before 2000-01-01.');
      return;
    }
    // ------------------------------------

    try {
      // Send POST request to backend
      await axios.post('http://localhost:8080/appointments', {
        appointmentId: id.trim(),
        appointmentDate: date,
        description: description.trim(),
      });
      // Reset form fields after adding
      setId('');
      setDate('');
      setDescription('');
      // Refresh appointment lists
      fetchAppointments();
    } catch (err) {
      // Show backend error message if available
      alert(err.response?.data?.error || 'Error adding appointment.');
    }
  };

  // Delete an appointment by ID
  const deleteAppointment = async (appointmentId) => {
    try {
      await axios.delete(`http://localhost:8080/appointments/${appointmentId}`);
      // Refresh list after deletion
      fetchAppointments();
    } catch (err) {
      alert(err.response?.data?.error || 'Error deleting appointment.');
    }
  };

  return (
    <div className="App">
      <h1>Appointments</h1>

      {/* Form for adding new appointments */}
      <div className="form-container">
        <input
          placeholder="ID"
          value={id}
          maxLength={10}
          onChange={(e) => setId(e.target.value)}
          className="id-input"
        />
        <input
          type="date"
          value={date}
          onChange={(e) => setDate(e.target.value)}
        />
        <input
          placeholder="Description"
          value={description}
          maxLength={50}
          onChange={(e) => setDescription(e.target.value)}
          className="desc-input"
        />
        <button className="add-btn" onClick={addAppointment}>Add</button>
      </div>

      {/* Display previous and upcoming appointments side by side */}
      <div className="columns">
        {/* Previous appointments */}
        <div className="column">
          <h2>Previous Appointments</h2>
          {previous.map((a) => (
            <div key={a.appointmentId} className="appointment-card">
              <span>{a.appointmentDate} - {a.description}</span>
              <button className="delete-btn" onClick={() => deleteAppointment(a.appointmentId)}>Delete</button>
            </div>
          ))}
        </div>

        {/* Upcoming appointments */}
        <div className="column">
          <h2>Upcoming Appointments</h2>
          {upcoming.map((a) => (
            <div key={a.appointmentId} className="appointment-card">
              <span>{a.appointmentDate} - {a.description}</span>
              <button className="delete-btn" onClick={() => deleteAppointment(a.appointmentId)}>Delete</button>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default App;
