import { useState } from 'react';

//Componente que permite agregar un nuevo cliente
function CustomerForm({ onAddCustomer }) {
    //Estado para los campos del formulario
    const [form, setForm] = useState({
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        address: ''
    });

    //Manejador de cambios en los campos del formulario
    function handleChange(e) {
        setForm({...form, [e.target.name]: e.target.value});
    }

    //Manejador de envio del formulario
    function handleSubmit(e){
        e.preventDefault(); //Previene el comportamiento por defecto del formulario
        onAddCustomer(form); //Llama a la función onAdd con los datos del formulario
        setForm({ firstName: '', lastName: '', email: '', phone: '', address: '' }); //Limpia el formulario
    }

    return (
        <form className="customer-form" onSubmit={handleSubmit}>
            <h2 className="form-title">Add Customer</h2>
            <input className="form-input" name="firstName" placeholder="Name" value={form.firstName} onChange={handleChange} />
            <input className="form-input" name="lastName" placeholder="Last Name" value={form.lastName} onChange={handleChange} />
            <input className="form-input" name="email" placeholder="Email" value={form.email} onChange={handleChange} />
            <input className="form-input" name="phone" placeholder="Phone" value={form.phone} onChange={handleChange} />
            <input className="form-input" name="address" placeholder="Address" value={form.address} onChange={handleChange} />
            <button className="form-btn" type="submit">Add Customer</button>
        </form>
    );
}

export default CustomerForm;