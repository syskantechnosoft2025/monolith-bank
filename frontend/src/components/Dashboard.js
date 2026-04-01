import React, { useEffect, useState } from 'react';
import axios from 'axios';

const API = 'http://localhost:8080/api';

export default function Dashboard({ token, onLogout }) {
  const [accounts, setAccounts] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchAccounts = async () => {
      try {
        const res = await axios.get(`${API}/accounts/me`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setAccounts(res.data);
      } catch (err) {
        setError(err.response?.data?.message || 'Unable to load accounts');
      }
    };
    fetchAccounts();
  }, [token]);

  return (
    <div>
      <button onClick={onLogout}>Logout</button>
      <h2>Your Accounts</h2>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <ul>
        {accounts.map((acct) => (
          <li key={acct.id}>{acct.number} - {acct.type} - {acct.balance.toFixed(2)} {acct.currency}</li>
        ))}
      </ul>
    </div>
  );
}
