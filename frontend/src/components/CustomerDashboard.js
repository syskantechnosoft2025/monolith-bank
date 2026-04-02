import React, { useState, useEffect } from 'react';
import axios from 'axios';

const CustomerDashboard = ({ token, onLogout, showToast }) => {
  const [accounts, setAccounts] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [payees, setPayees] = useState([]);
  const [activeTab, setActiveTab] = useState('history');
  const [selectedAccount, setSelectedAccount] = useState('');
  const [selectedPayee, setSelectedPayee] = useState('');
  const [amount, setAmount] = useState('');
  const [description, setDescription] = useState('');
  const [newAccountType, setNewAccountType] = useState('SAVINGS');
  const [newAccountBalance, setNewAccountBalance] = useState('');

  useEffect(() => {
    fetchAccounts();
    fetchTransactions();
    fetchPayees();
  }, []);

  const fetchAccounts = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/accounts/me', {
        headers: { Authorization: `Bearer ${token}` }
      });
      setAccounts(response.data);
    } catch (err) {
      showToast('Failed to fetch accounts', 'error');
    }
  };

  const fetchTransactions = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/transactions', {
        headers: { Authorization: `Bearer ${token}` }
      });
      setTransactions(response.data);
    } catch (err) {
      showToast('Failed to fetch transactions', 'error');
    }
  };

  const fetchPayees = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/payees', {
        headers: { Authorization: `Bearer ${token}` }
      });
      setPayees(response.data);
    } catch (err) {
      showToast('Failed to fetch payees', 'error');
    }
  };

  const createAccount = async () => {
    try {
      await axios.post('http://localhost:8080/api/accounts', {
        accountType: newAccountType,
        balance: parseFloat(newAccountBalance)
      }, {
        headers: { Authorization: `Bearer ${token}` }
      });
      fetchAccounts();
      setNewAccountType('SAVINGS');
      setNewAccountBalance('');
    } catch (err) {
      showToast('Failed to create account', 'error');
    }
  };

  const handleDeposit = async () => {
    try {
      await axios.post('http://localhost:8080/api/transactions/deposit', {
        accountId: selectedAccount,
        amount: parseFloat(amount),
        description
      }, {
        headers: { Authorization: `Bearer ${token}` }
      });
      fetchAccounts();
      fetchTransactions();
      setAmount('');
      setDescription('');
    } catch (err) {
      showToast('Failed to deposit', 'error');
    }
  };

  const handleWithdraw = async () => {
    try {
      await axios.post('http://localhost:8080/api/transactions/withdraw', {
        accountId: selectedAccount,
        amount: parseFloat(amount),
        description
      }, {
        headers: { Authorization: `Bearer ${token}` }
      });
      fetchAccounts();
      fetchTransactions();
      setAmount('');
      setDescription('');
    } catch (err) {
      showToast('Failed to withdraw', 'error');
    }
  };

  const handleTransfer = async () => {
    try {
      await axios.post('http://localhost:8080/api/transactions/transfer', {
        fromAccountId: selectedAccount,
        toPayeeId: selectedPayee,
        amount: parseFloat(amount),
        description
      }, {
        headers: { Authorization: `Bearer ${token}` }
      });
      fetchAccounts();
      fetchTransactions();
      setAmount('');
      setDescription('');
    } catch (err) {
      showToast('Failed to transfer', 'error');
    }
  };

  const renderTransactionContent = () => {
    switch (activeTab) {
      case 'history':
        return (
          <div className="card">
            <div className="card-header">
              <h3 className="card-title">Transaction History</h3>
            </div>
            <ul className="transaction-list">
              {transactions.map(tx => (
                <li key={tx.id} className="transaction-item">
                  <span className="transaction-desc">{tx.description}</span>
                  <span className={`transaction-amount ${tx.amount < 0 ? 'negative' : ''}`}>${tx.amount}</span>
                </li>
              ))}
            </ul>
          </div>
        );
      case 'deposit':
        return (
          <div className="card">
            <div className="card-header">
              <h3 className="card-title">Deposit Funds</h3>
            </div>
            <div className="form-group">
              <label className="form-label">Select Account</label>
              <select value={selectedAccount} onChange={e => setSelectedAccount(e.target.value)} className="form-select">
                <option value="">Choose an account</option>
                {accounts.map(acc => (
                  <option key={acc.id} value={acc.id}>{acc.accountType} - ${acc.balance}</option>
                ))}
              </select>
            </div>
            {selectedAccount && (
              <div className="balance-display">
                <div className="balance-label">Current Balance</div>
                <div className="balance-amount">${accounts.find(acc => acc.id === selectedAccount)?.balance}</div>
              </div>
            )}
            <div className="form-group">
              <label className="form-label">Amount</label>
              <input type="number" placeholder="Enter amount" value={amount} onChange={e => setAmount(e.target.value)} className="form-input" />
            </div>
            <div className="form-group">
              <label className="form-label">Description</label>
              <input type="text" placeholder="Transaction description" value={description} onChange={e => setDescription(e.target.value)} className="form-input" />
            </div>
            <button onClick={handleDeposit} className="btn btn-success">Deposit</button>
          </div>
        );
      case 'withdraw':
        return (
          <div className="card">
            <div className="card-header">
              <h3 className="card-title">Withdraw Funds</h3>
            </div>
            <div className="form-group">
              <label className="form-label">Select Account</label>
              <select value={selectedAccount} onChange={e => setSelectedAccount(e.target.value)} className="form-select">
                <option value="">Choose an account</option>
                {accounts.map(acc => (
                  <option key={acc.id} value={acc.id}>{acc.accountType} - ${acc.balance}</option>
                ))}
              </select>
            </div>
            {selectedAccount && (
              <div className="balance-display">
                <div className="balance-label">Current Balance</div>
                <div className="balance-amount">${accounts.find(acc => acc.id === selectedAccount)?.balance}</div>
              </div>
            )}
            <div className="form-group">
              <label className="form-label">Amount</label>
              <input type="number" placeholder="Enter amount" value={amount} onChange={e => setAmount(e.target.value)} className="form-input" />
            </div>
            <div className="form-group">
              <label className="form-label">Description</label>
              <input type="text" placeholder="Transaction description" value={description} onChange={e => setDescription(e.target.value)} className="form-input" />
            </div>
            <button onClick={handleWithdraw} className="btn btn-primary">Withdraw</button>
          </div>
        );
      case 'transfer':
        return (
          <div className="card">
            <div className="card-header">
              <h3 className="card-title">Transfer Funds</h3>
            </div>
            <div className="form-group">
              <label className="form-label">From Account</label>
              <select value={selectedAccount} onChange={e => setSelectedAccount(e.target.value)} className="form-select">
                <option value="">Choose source account</option>
                {accounts.map(acc => (
                  <option key={acc.id} value={acc.id}>{acc.accountType} - ${acc.balance}</option>
                ))}
              </select>
            </div>
            {selectedAccount && (
              <div className="balance-display">
                <div className="balance-label">Current Balance</div>
                <div className="balance-amount">${accounts.find(acc => acc.id === selectedAccount)?.balance}</div>
              </div>
            )}
            <div className="form-group">
              <label className="form-label">To Payee</label>
              <select value={selectedPayee} onChange={e => setSelectedPayee(e.target.value)} className="form-select">
                <option value="">Choose payee</option>
                {payees.map(payee => (
                  <option key={payee.id} value={payee.id}>{payee.name}</option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label className="form-label">Amount</label>
              <input type="number" placeholder="Enter amount" value={amount} onChange={e => setAmount(e.target.value)} className="form-input" />
            </div>
            <div className="form-group">
              <label className="form-label">Description</label>
              <input type="text" placeholder="Transaction description" value={description} onChange={e => setDescription(e.target.value)} className="form-input" />
            </div>
            <button onClick={handleTransfer} className="btn btn-primary">Transfer</button>
          </div>
        );
      default:
        return null;
    }
  };

  return (
    <div className="app-container">
      <div className="dashboard-header">
        <h1 className="dashboard-title">Customer Dashboard</h1>
        <button className="btn btn-danger" onClick={onLogout}>Logout</button>
      </div>
      <div className="card">
        <div className="card-header">
          <h2 className="card-title">Create New Account</h2>
        </div>
        <div className="form-group">
          <label className="form-label">Account Type</label>
          <select value={newAccountType} onChange={e => setNewAccountType(e.target.value)} className="form-select">
            <option value="SAVINGS">Savings Account</option>
            <option value="CHECKING">Checking Account</option>
          </select>
        </div>
        <div className="form-group">
          <label className="form-label">Initial Balance</label>
          <input type="number" placeholder="Enter initial balance" value={newAccountBalance} onChange={e => setNewAccountBalance(e.target.value)} className="form-input" />
        </div>
        <button onClick={createAccount} className="btn btn-primary">Create Account</button>
      </div>
      <div className="card">
        <div className="card-header">
          <h2 className="card-title">Your Accounts</h2>
        </div>
        {accounts.length === 0 ? (
          <p>No accounts found. Create your first account above.</p>
        ) : (
          <div>
            {accounts.map(acc => (
              <div key={acc.id} className="account-card">
                <div className="account-type">{acc.accountType} Account</div>
                <div className="account-balance">${acc.balance}</div>
                <div className="account-number">Account ID: {acc.id}</div>
              </div>
            ))}
          </div>
        )}
      </div>
      <div className="card">
        <div className="card-header">
          <h2 className="card-title">Transactions</h2>
        </div>
        <div className="tab-container">
          <button onClick={() => setActiveTab('history')} className={`tab-button ${activeTab === 'history' ? 'active' : ''}`}>History</button>
          <button onClick={() => setActiveTab('deposit')} className={`tab-button ${activeTab === 'deposit' ? 'active' : ''}`}>Deposit</button>
          <button onClick={() => setActiveTab('withdraw')} className={`tab-button ${activeTab === 'withdraw' ? 'active' : ''}`}>Withdraw</button>
          <button onClick={() => setActiveTab('transfer')} className={`tab-button ${activeTab === 'transfer' ? 'active' : ''}`}>Transfer</button>
        </div>
        {renderTransactionContent()}
      </div>
    </div>
  );
};

export default CustomerDashboard;