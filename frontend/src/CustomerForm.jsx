import { useState } from 'react';

//Componente que permite agregar un nuevo cliente
function CustomerForm({ onAddCustomer, tutorial }) {
    // Estado para los campos del formulario
    const [form, setForm] = useState({
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        address: ''
    });
    // Estado para los mensajes de error de validación
    const [errors, setErrors] = useState({});
    // Estado de envío para evitar clicks múltiples y dar feedback
    const [submitting, setSubmitting] = useState(false);
    // Error genérico de API (por ejemplo 409 email duplicado, 400 validación backend, etc.)
    const [apiError, setApiError] = useState(null);

    // Función que valida los campos del formulario
    function validate(form) {
        const newErrors = {};
        // Validar nombre
        if (!form.firstName.trim()) newErrors.firstName = 'First name is required.';
        // Validar apellido
        if (!form.lastName.trim()) newErrors.lastName = 'Last name is required.';
        // Validar email
        if (!form.email.trim()) {
            newErrors.email = 'Email is required.';
        } else if (!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(form.email)) {
            newErrors.email = 'Email is not valid.';
        }
        return newErrors;
    }

    // Manejador de cambios en los campos del formulario
    function handleChange(e) {
        setForm({ ...form, [e.target.name]: e.target.value });
        // Limpia el error del campo modificado
        setErrors({ ...errors, [e.target.name]: undefined });
    }

    // Manejador de envío del formulario
    async function handleSubmit(e) {
        e.preventDefault(); // Previene el comportamiento por defecto
        const validationErrors = validate(form); // Valida los campos
        // Si hay errores, los muestra y no envía el formulario
        if (Object.keys(validationErrors).length > 0) {
            setErrors(validationErrors);
            return;
        }
        // Si no hay errores, intenta crear el cliente en el backend
        try {
            setSubmitting(true);
            setApiError(null);
            await onAddCustomer(form);
            // Si todo salió bien, limpio el formulario
            setForm({ firstName: '', lastName: '', email: '', phone: '', address: '' });
            setErrors({});
        } catch (err) {
            // Mensaje simple para el usuario; luego afinamos según status
            const status = err?.status;
            if (status === 409) {
                setApiError('Email already exists. Please use another one.');
            } else if (status === 400) {
                // Si el backend envía detalles de validación, podemos mostrarlos aquí
                setApiError('Some fields are invalid. Please review and try again.');
            } else {
                setApiError('Unexpected error. Please try again later.');
            }
        } finally {
            setSubmitting(false);
        }
    }

    // Renderiza el formulario y los mensajes de error debajo de cada campo
    return (
        <form className="customer-form" onSubmit={handleSubmit}>
            <h2 className="customer-form-title">Add Customer</h2>
            {/* Tutorial debajo del título (opcional) */}
            {tutorial && (
                <div className="customer-form-help">
                    {tutorial}
                </div>
            )}
            {/* Error de API en bloque superior (si existe) */}
            {apiError && (
                <div className="customer-form-error customer-form-error-top" role="alert" aria-live="polite">
                    {apiError}
                </div>
            )}
            {/* Info about required fields */}
            <div className="customer-form-required-info">Fields marked with <span className="customer-form-required">*</span> are required.</div>
            {/* First Name (required) */}
            <input className="customer-form-input" name="firstName" placeholder="First Name*" value={form.firstName} onChange={handleChange} />
            {errors.firstName && <div className="customer-form-error">{errors.firstName}</div>}
            {/* Last Name (required) */}
            <input className="customer-form-input" name="lastName" placeholder="Last Name*" value={form.lastName} onChange={handleChange} />
            {errors.lastName && <div className="customer-form-error">{errors.lastName}</div>}
            {/* Email (required) */}
            <input className="customer-form-input" name="email" placeholder="Email*" value={form.email} onChange={handleChange} />
            {errors.email && <div className="customer-form-error">{errors.email}</div>}
            {/* Phone */}
            <input className="customer-form-input" name="phone" placeholder="Phone" value={form.phone} onChange={handleChange} />
            {/* Address */}
            <input className="customer-form-input" name="address" placeholder="Address" value={form.address} onChange={handleChange} />
            {/* Submit button */}
            <button className="customer-form-btn" type="submit" disabled={submitting}>
                {submitting ? 'Adding…' : 'Add Customer'}
            </button>
        </form>
    );
}

export default CustomerForm;