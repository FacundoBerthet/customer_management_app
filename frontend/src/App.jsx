import { useState } from 'react';
import { BrowserRouter, Routes, Route, Link, NavLink } from 'react-router-dom';
import './App.css';

import Home from './Home.jsx';
import AddCustomer from './AddCustomer.jsx';
import EditCustomer from './EditCustomer.jsx';
import CustomerList from './CustomerList.jsx';
import logoIcon from './assets/icons/icon.png';
import menuToggleIcon from './assets/icons/menu_toggle.png';


function App() {
  // Estado para la lista de clientes
  const [customers, setCustomers] = useState([
    {
      firstName: 'Juan',
      lastName: 'Pérez',
      email: 'juan.perez@example.com',
      phone: '123-4567',
      address: 'Calle Falsa 123'
    },
    {
      firstName: 'Ana',
      lastName: 'García',
      email: 'ana.garcia@example.com',
      phone: '987-6543',
      address: 'Av. Siempreviva 742'
    }
  ]);

  // Estado para la alerta superior
  const [alert, setAlert] = useState(null); // { type: 'success'|'error', message: string }

  // Función para agregar cliente y mostrar alerta
  function addCustomer(newCustomer) {
    setCustomers([...customers, newCustomer]);
    setAlert({ type: 'success', message: 'Customer added successfully!' });
  }

  // Función para cerrar la alerta
  function closeAlert() {
    setAlert(null);
  }

  const [menuOpen, setMenuOpen] = useState(false);

  function handleNavClick() {
    setMenuOpen(false);
  }

  return (
    <BrowserRouter>
      {/* Cartel superior de alerta */}
      {alert && (
        <div className={`top-alert ${alert.type}`}> 
          <span>{alert.message}</span>
          <button className="top-alert-close" onClick={closeAlert}>×</button>
        </div>
      )}
      {/* Menú de navegación */}
      <nav className="main-nav">
        <div className="nav-left">
          <Link to="/" className="nav-logo" onClick={handleNavClick}>
            <img src={logoIcon} alt="Home" />
            <span>Customer App</span>
          </Link>
        </div>
        <button
          className="nav-toggle"
          aria-label="Toggle menu"
          onClick={() => setMenuOpen((v) => !v)}
        >
          <img src={menuToggleIcon} alt="Menu" />
        </button>
        <div className={`nav-links ${menuOpen ? 'open' : ''}`}>
          <NavLink to="/" onClick={handleNavClick}>Home</NavLink>
          <NavLink to="/add" onClick={handleNavClick}>Add Customer</NavLink>
          <NavLink to="/list" onClick={handleNavClick}>Customer List</NavLink>
          <NavLink to="/edit/1" onClick={handleNavClick}>Edit Customer</NavLink>
        </div>
      </nav>
      {/* Rutas */}
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/add" element={<AddCustomer onAddCustomer={addCustomer} />} />
        <Route path="/list" element={<CustomerList customers={customers} />} />
        <Route path="/edit/:id" element={<EditCustomer />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App
