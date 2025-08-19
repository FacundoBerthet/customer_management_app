import { useEffect, useState } from 'react';
import { BrowserRouter, Routes, Route, Link, NavLink } from 'react-router-dom';
import './App.css';

import Home from './Home.jsx';
import AddCustomer from './AddCustomer.jsx';
import EditCustomer from './EditCustomer.jsx';
import CustomerList from './CustomerList.jsx';
import EditSearch from './EditSearch.jsx';
import logoIcon from './assets/icons/icon.png';
import menuToggleIcon from './assets/icons/menu_toggle.png';
import { createCustomer } from './api/client';


function App() {
  // Estado para la lista de clientes
  const [customers, setCustomers] = useState([]);

  // Estado para la alerta superior
  const [alert, setAlert] = useState(null); // { type: 'success'|'error', message: string }

  // Función para crear cliente en el backend (paso 1: integración mínima)
  // - Llama a la API
  // - Muestra un toast de éxito o error
  // Nota: El listado se carga desde el backend, así que no necesitamos
  // mutar el estado local "customers" aquí.
  async function addCustomer(newCustomer) {
    try {
      await createCustomer(newCustomer); // POST /api/customers
      setAlert({ type: 'success', message: 'Customer created successfully!' });
    } catch (err) {
      // Mensaje simple; luego podemos mapear códigos (400/409) con más detalle
      const msg = err?.message || 'Failed to create customer. Please try again.';
      setAlert({ type: 'error', message: msg });
      // Re-lanzo para que el formulario pueda reaccionar si lo necesita
      throw err;
    }
  }

  // Función para cerrar la alerta
  function closeAlert() {
    setAlert(null);
  }

  // Exponer un pequeño helper para mostrar toasts desde páginas hijas
  function notify(type, message) {
    setAlert({ type, message });
  }

  // Auto-ocultar el toast a los 8 segundos si el usuario no lo cierra
  useEffect(() => {
    if (!alert) return;
    const id = setTimeout(() => setAlert(null), 8000);
    return () => clearTimeout(id);
  }, [alert]);

  const [menuOpen, setMenuOpen] = useState(false);

  function handleNavClick() {
    setMenuOpen(false);
  }

  return (
    <BrowserRouter>
      {/* Alerta inferior fija (toast) */}
      {alert && (
        <div
          className={`toast-alert ${alert.type}`}
          role="status"
          aria-live="polite"
        > 
          <span>{alert.message}</span>
          <button
            className="top-alert-close"
            onClick={closeAlert}
            aria-label="Close notification"
          >
            ×
          </button>
          {/* Barra de progreso de 8s (solo visual) */}
          <div className="toast-progress" aria-hidden="true"></div>
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
          <NavLink to="/edit" onClick={handleNavClick}>Edit Customer</NavLink>
        </div>
      </nav>
      {/* Rutas */}
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/add" element={<AddCustomer onAddCustomer={addCustomer} />} />
        <Route path="/list" element={<CustomerList customers={customers} />} />
  <Route path="/edit" element={<EditSearch />} />
  <Route path="/edit/:id" element={<EditCustomer onNotify={notify} />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App
