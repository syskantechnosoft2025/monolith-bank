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

  const handleLogin = (accessToken, userRoles) => {
    setToken(accessToken);
    setRoles(userRoles);
    setView('dashboard');
  };

  const handleLogout = () => {
    setToken(null);
    setRoles([]);
    setView('login');
  };

  const renderDashboard = () => {
    if (roles.includes('ROLE_ADMIN')) {
      return <AdminDashboard token={token} onLogout={handleLogout} />;
    } else if (roles.includes('ROLE_MANAGER')) {
      return <ManagerDashboard token={token} onLogout={handleLogout} />;
    } else {
      return <CustomerDashboard token={token} onLogout={handleLogout} />;
    }
  };

  return (
    <div style={{ padding: '20px', fontFamily: 'Arial, sans-serif' }}>
      <h1>Monolith Bank</h1>
      {view === 'login' && (
        <div>
          <Login onLogin={handleLogin} />
          <button onClick={() => setView('register')}>Register</button>
        </div>
      )}
      {view === 'register' && (
        <div>
          <Register onRegister={() => setView('login')} />
          <button onClick={() => setView('login')}>Back to Login</button>
        </div>
      )}
      {view === 'dashboard' && renderDashboard()}
    </div>
  );
}

export default App;
