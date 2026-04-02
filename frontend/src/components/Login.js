import React, { useState } from 'react';
import axios from 'axios';

const API = 'http://localhost:8080/api/auth';

export default function Login({ onLogin, showToast }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  

  const submit = async (e) => {
    e.preventDefault();
    try {
      const res = await axios.post(`${API}/login`, { username, password });
      onLogin(res.data.accessToken, res.data.roles);
    } catch (err) {
      showToast(err.response?.data?.message || 'Login failed', 'error');
    }
  };

  return (
    <div className="form-container">
      <div className="card-header">
        <h2 className="card-title">Sign In to Your Account</h2>
      </div>
      <form onSubmit={submit}>
        <div className="form-group">
          <label className="form-label">Username</label>
          <input
            className="form-input"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            placeholder="Enter your username"
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
            placeholder="Enter your password"
            required
          />
        </div>
          <button type="submit" className="btn btn-primary btn-block">Login</button>
      </form>
    </div>
  );
}
