import React, { useEffect, useState } from 'react';
import axios from 'axios';

const API = 'http://localhost:8080/api';

export default function CustomerDashboard({ token, onLogout }) {
  const [accounts, setAccounts] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [payees, setPayees] = useState([]);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState('accounts');

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
      setError(err.response?.data?.message || 'Unable to load accounts');
    }
  };

  const fetchTransactions = async () => {
    try {
      const res = await axios.get(`${API}/transactions/search`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setTransactions(res.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Unable to load transactions');
    }
  };

  const handleDeposit = async (accountNumber, amount) => {
    try {
      await axios.post(`${API}/transactions/deposit`, null, {
        params: { accountNumber, amount },
        headers: { Authorization: `Bearer ${token}` },
      });
      fetchAccounts();
      alert('Deposit successful');
    } catch (err) {
      setError(err.response?.data?.message || 'Deposit failed');
    }
  };

  const handleWithdraw = async (accountNumber, amount) => {
    try {
      await axios.post(`${API}/transactions/withdraw`, null, {
        params: { accountNumber, amount },
        headers: { Authorization: `Bearer ${token}` },
      });
      fetchAccounts();
      alert('Withdraw successful');
    } catch (err) {
      setError(err.response?.data?.message || 'Withdraw failed');
    }
  };

  const handleTransfer = async (fromAccount, toAccount, amount) => {
    try {
      await axios.post(`${API}/transactions/transfer`, null, {
        params: { fromAccount, toAccount, amount },
        headers: { Authorization: `Bearer ${token}` },
      });
      fetchAccounts();
      alert('Transfer successful');
    } catch (err) {
      setError(err.response?.data?.message || 'Transfer failed');
    }
  };

  const addPayee = (payee) => {
    setPayees([...payees, payee]);
  };

  return (
    <div>
      <button onClick={onLogout}>Logout</button>
      <h2>Customer Dashboard</h2>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <div>
        <button onClick={() => setActiveTab('accounts')}>Accounts</button>
        <button onClick={() => { setActiveTab('transactions'); fetchTransactions(); }}>Transactions</button>
        <button onClick={() => setActiveTab('payees')}>Payees</button>
      </div>
      {activeTab === 'accounts' && (
        <div>
          <h3>Your Accounts</h3>
          <ul>
            {accounts.map((acct) => (
              <li key={acct.id}>
                {acct.number} - {acct.type} - {acct.balance.toFixed(2)} {acct.currency}
                <br />
                <button onClick={() => {
                  const amount = prompt('Enter deposit amount');
                  if (amount) handleDeposit(acct.number, parseFloat(amount));
                }}>Deposit</button>
                <button onClick={() => {
                  const amount = prompt('Enter withdraw amount');
                  if (amount) handleWithdraw(acct.number, parseFloat(amount));
                }}>Withdraw</button>
              </li>
            ))}
          </ul>
          <h4>Fund Transfer</h4>
          <form onSubmit={(e) => {
            e.preventDefault();
            const from = e.target.from.value;
            const to = e.target.to.value;
            const amount = parseFloat(e.target.amount.value);
            handleTransfer(from, to, amount);
          }}>
            <input name="from" placeholder="From Account" required />
            <input name="to" placeholder="To Account" required />
            <input name="amount" type="number" placeholder="Amount" required />
            <button type="submit">Transfer</button>
          </form>
        </div>
      )}
      {activeTab === 'transactions' && (
        <div>
          <h3>Transaction History</h3>
          <ul>
            {transactions.map((txn) => (
              <li key={txn.id}>{txn.type} - {txn.amount} - {txn.timestamp}</li>
            ))}
          </ul>
        </div>
      )}
      {activeTab === 'payees' && (
        <div>
          <h3>Payee Management</h3>
          <ul>
            {payees.map((payee, idx) => (
              <li key={idx}>{payee.name} - {payee.account}</li>
            ))}
          </ul>
          <form onSubmit={(e) => {
            e.preventDefault();
            addPayee({ name: e.target.name.value, account: e.target.account.value });
            e.target.reset();
          }}>
            <input name="name" placeholder="Payee Name" required />
            <input name="account" placeholder="Account Number" required />
            <button type="submit">Add Payee</button>
          </form>
        </div>
      )}
    </div>
  );
}