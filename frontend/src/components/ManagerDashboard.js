import React, { useEffect, useState } from 'react';
import axios from 'axios';

const API = 'http://localhost:8080/api';

export default function ManagerDashboard({ token, onLogout, showToast }) {
  const [accounts, setAccounts] = useState([]);

  useEffect(() => {
    fetchAccounts();
  }, []);

  const fetchAccounts = async () => {
    try {
      const res = await axios.get(`${API}/accounts/me`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setAccounts(res.data);
    } catch (err) {
      showToast(err.response?.data?.message || 'Unable to load accounts', 'error');
    }
  };

  return (
    <div className="app-container">
      <div className="dashboard-header">
        <h1 className="dashboard-title">Manager Dashboard</h1>
        <button className="btn btn-danger" onClick={onLogout}>Logout</button>
      </div>
      <div className="card">
        <div className="card-header">
          <h2 className="card-title">Management Tools</h2>
        </div>
        <p>Manager controls and oversight features will be implemented here.</p>
      </div>
      <div className="card">
        <div className="card-header">
          <h2 className="card-title">Managed Accounts</h2>
        </div>
        <ul className="transaction-list">
          {accounts.map((acct) => (
            <li key={acct.id} className="transaction-item">
              <div>
                <div className="transaction-desc">{acct.number} - {acct.type}</div>
                <div className="account-number">Balance: ${acct.balance?.toFixed(2) || '0.00'}</div>
              </div>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}