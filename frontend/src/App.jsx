
import { useState } from 'react';
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import './App.css';

import Home from './Home.jsx';
import AddCustomer from './AddCustomer.jsx';
import EditCustomer from './EditCustomer.jsx';
import CustomerList from './CustomerList.jsx';


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
        <Link to="/">Home</Link>
        <Link to="/add">Add Customer</Link>
        <Link to="/list">Customer List</Link>
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
