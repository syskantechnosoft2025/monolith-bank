import React, { useEffect, useState } from 'react';
import axios from 'axios';

const API = 'http://localhost:8080/api';

export default function AdminDashboard({ token, onLogout }) {
  const [accounts, setAccounts] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchAccounts();
  }, []);

  const fetchAccounts = async () => {
    try {
      // Assuming admin can see all accounts, but endpoint is /me, so perhaps need a different endpoint.
      // For now, use /me
      const res = await axios.get(`${API}/accounts/me`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setAccounts(res.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Unable to load accounts');
    }
  };

  return (
    <div>
      <button onClick={onLogout}>Logout</button>
      <h2>Admin Dashboard</h2>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <h3>All Accounts</h3>
      <ul>
        {accounts.map((acct) => (
          <li key={acct.id}>{acct.number} - {acct.type} - {acct.balance.toFixed(2)} {acct.currency}</li>
        ))}
      </ul>
      {/* Add admin specific features here */}
    </div>
  );
}