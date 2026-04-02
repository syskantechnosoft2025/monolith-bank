import React, { useState } from 'react';
import axios from 'axios';

const API = 'http://localhost:8080/api/auth';

export default function Register({ onRegister, showToast }) {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error] = useState('');
  const [success] = useState('');

  const submit = async (e) => {
    e.preventDefault();
    
    try {
      const res = await axios.post(`${API}/register`, { username, email, password });
      showToast(res.data.message || 'Registration successful', 'success');
      onRegister();
    } catch (err) {
      showToast(err.response?.data?.message || 'Registration failed', 'error');
    }
  };

  return (
    <div className="form-container">
      <div className="card-header">
        <h2 className="card-title">Create Your Account</h2>
      </div>
      <form onSubmit={submit}>
        <div className="form-group">
          <label className="form-label">Username</label>
          <input
            className="form-input"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            placeholder="Choose a username"
            required
          />
        </div>
        <div className="form-group">
          <label className="form-label">Email</label>
          <input
            className="form-input"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="Enter your email"
            required
          />
        </div>
        <div className="form-group">
          <label className="form-label">Password</label>
          <input
            className="form-input"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Create a password"
            required
          />
        </div>
        <button type="submit" className="btn btn-success btn-block">Create Account</button>
      </form>
    </div>
  );
}