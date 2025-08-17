import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'

import CustomerList from './CustomerList.jsx'
import CustomerForm from './CustomerForm.jsx'

function App() {
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

  function addCustomer(newCustomer) {
    setCustomers([...customers, newCustomer])
  }

  return (
    <>
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
