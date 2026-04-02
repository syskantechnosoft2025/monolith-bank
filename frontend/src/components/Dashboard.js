import React, { useEffect, useState } from 'react';
import axios from 'axios';

const API = 'http://localhost:8080/api';

export default function Dashboard({ token, onLogout, showToast }) {
  const [accounts, setAccounts] = useState([]);

  useEffect(() => {
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
    fetchAccounts();
  }, [token]);

  return (
    <div className="app-container">
      <div className="dashboard-header">
        <h1 className="dashboard-title">Customer Dashboard</h1>
        <button className="btn btn-danger" onClick={onLogout}>Logout</button>
      </div>
      <div className="card">
        <div className="card-header">
          <h2 className="card-title">Your Accounts</h2>
        </div>
        {accounts.length === 0 ? (
          <p>No accounts found. Please contact your bank manager.</p>
        ) : (
          <div>
            {accounts.map((acct) => (
              <div key={acct.id} className="account-card">
                <div className="account-type">{acct.type}</div>
                <div className="account-balance">${acct.balance?.toFixed(2) || '0.00'}</div>
                <div className="account-number">Account: {acct.number}</div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
