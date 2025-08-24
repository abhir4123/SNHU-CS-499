import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

function App() {
  // State arrays for separating upcoming and previous appointments
  const [upcoming, setUpcoming] = useState([]);
  const [previous, setPrevious] = useState([]);

  // State variables for form inputs when adding an appointment
  const [id, setId] = useState('');
  const [date, setDate] = useState('');
  const [description, setDescription] = useState('');

  // Load appointments from the backend once when the component first renders
  useEffect(() => {
    fetchAppointments();
  }, []);

  // Get both upcoming and previous appointments from the backend API
  const fetchAppointments = async () => {
    try {
      // Fetch both lists in parallel for efficiency
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

  // Add a new appointment after validating the inputs
  const addAppointment = async () => {
    // Ensure all fields are filled out
    if (!id || !date || !description) {
      alert('Please fill in ID, date, and description.');
      return;
    }
    // ID length validation
    if (id.trim().length < 1 || id.trim().length > 10) {
      alert('Appointment ID must be 1–10 characters.');
      return;
    }
    // Description length validation
    if (description.trim().length < 1 || description.trim().length > 50) {
      alert('Description must be 1–50 characters.');
      return;
    }
    // Validate date format and range
    const earliest = '2000-01-01';
    if (!/^\d{4}-\d{2}-\d{2}$/.test(date)) {
      alert('Please use yyyy-MM-dd for the date.');
      return;
    }
    if (date < earliest) {
      alert('Appointment date cannot be before 2000-01-01.');
      return;
    }

    try {
      // Send the new appointment to the backend
      await axios.post('http://localhost:8080/appointments', {
        appointmentId: id.trim(),
        appointmentDate: date,
        description: description.trim(),
      });
      // Reset form fields and refresh appointment lists
      setId('');
      setDate('');
      setDescription('');
      fetchAppointments();
    } catch (err) {
      alert(err.response?.data?.error || 'Error adding appointment.');
    }
  };

  // Remove an appointment by its ID
  const deleteAppointment = async (appointmentId) => {
    try {
      await axios.delete(`http://localhost:8080/appointments/${appointmentId}`);
      fetchAppointments();
    } catch (err) {
      alert(err.response?.data?.error || 'Error deleting appointment.');
    }
  };

  // Export the appointment schedule in either CSV or JSON format
  const exportSchedule = async (format = 'csv', scope = 'all') => {
    try {
      // Build the request URL with chosen format and scope
      const url = `http://localhost:8080/appointments/export?format=${encodeURIComponent(format)}&scope=${encodeURIComponent(scope)}`;
      const res = await axios.get(url, { responseType: 'blob' });

      // Create a downloadable file in the browser
      const blob = new Blob(
        [res.data],
        { type: format === 'csv' ? 'text/csv' : 'application/json' }
      );
      const a = document.createElement('a');
      a.href = URL.createObjectURL(blob);
      a.download = `appointments-${scope}.${format}`;
      a.click();
      URL.revokeObjectURL(a.href);
    } catch (err) {
      alert(err.response?.data?.error || 'Export failed.');
    }
  };

  return (
    <div className="App">
      <h1>Appointments</h1>

      {/* Form for adding a new appointment */}
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
          className="date-input"
        />
        <input
          placeholder="Description"
          value={description}
          maxLength={50}
          onChange={(e) => setDescription(e.target.value)}
          className="description-input"
        />
        <button className="add-btn" onClick={addAppointment}>Add</button>
      </div>

      {/* Buttons for exporting the appointment schedule */}
      <div className="export-bar">
        <button className="export-btn" onClick={() => exportSchedule('csv', 'all')}>Export CSV (All)</button>
        <button className="export-btn" onClick={() => exportSchedule('json', 'all')}>Export JSON (All)</button>
      </div>

      {/* Display lists of previous and upcoming appointments */}
      <div className="columns">
        <div className="column">
          <h2>Previous Appointments</h2>
          {previous.map((a) => (
            <div key={a.appointmentId} className="appointment-card">
              <span>{a.appointmentDate} - {a.description}</span>
              <button className="delete-btn" onClick={() => deleteAppointment(a.appointmentId)}>Delete</button>
            </div>
          ))}
        </div>

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
