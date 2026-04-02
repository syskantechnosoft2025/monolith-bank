import React, { useState } from 'react';
import Login from './components/Login';
import Register from './components/Register';
import CustomerDashboard from './components/CustomerDashboard';
import AdminDashboard from './components/AdminDashboard';
import ManagerDashboard from './components/ManagerDashboard';

function App() {
  const [token, setToken] = useState(null);
  const [roles, setRoles] = useState([]);
  const [view, setView] = useState('login'); // login, register, dashboard
  const [toastMessage, setToastMessage] = useState('');
  const [toastType, setToastType] = useState('error');

  const showToast = (message, type = 'error') => {
    setToastMessage(message);
    setToastType(type);
    setTimeout(() => setToastMessage(''), 3500);
  };

  const handleLogin = (accessToken, userRoles) => {
    setToken(accessToken);
    setRoles(userRoles);
    setView('dashboard');
  };

  const handleLogout = () => {
    setToken(null);
    setRoles([]);
    setView('login');
    showToast('You have been logged out.', 'success');
  };

  const renderDashboard = () => {
    if (roles.includes('ROLE_ADMIN')) {
      return <AdminDashboard token={token} onLogout={handleLogout} showToast={showToast} />;
    } else if (roles.includes('ROLE_MANAGER')) {
      return <ManagerDashboard token={token} onLogout={handleLogout} showToast={showToast} />;
    } else {
      return <CustomerDashboard token={token} onLogout={handleLogout} showToast={showToast} />;
    }
  };

  return (
    <div className="app-container">
      <header className="app-header">
        <h1 className="app-title">Monolith Bank</h1>
      </header>

      {toastMessage && (
        <div className={`toast ${toastType}`} role="alert">
          {toastMessage}
        </div>
      )}
      {view === 'login' && (
        <div className="card">
          <Login onLogin={handleLogin} showToast={showToast} />
          <button className="btn btn-secondary" onClick={() => setView('register')} style={{ marginTop: '15px' }}>Create Account</button>
        </div>
      )}
      {view === 'register' && (
        <div className="card">
          <Register onRegister={() => setView('login')} showToast={showToast} />
          <button className="btn btn-secondary" onClick={() => setView('login')} style={{ marginTop: '15px' }}>Back to Login</button>
        </div>
      )}
      {view === 'dashboard' && renderDashboard()}
    </div>
  );
}

export default App;
