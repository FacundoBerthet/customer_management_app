import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'


import CustomerList from './CustomerList.jsx'
import CustomerForm from './CustomerForm.jsx'

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
  ])

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
    <>
      {/* Cartel superior de alerta */}
      {alert && (
        <div className={`top-alert ${alert.type}`}> 
          <span>{alert.message}</span>
          <button className="top-alert-close" onClick={closeAlert}>×</button>
        </div>
      )}
      <div className="add-form-container">
        <CustomerForm onAddCustomer={addCustomer} />
      </div>
      <div className="customer-list-container">
        <CustomerList customers={customers} />
      </div>
    </>
  )
}

export default App
