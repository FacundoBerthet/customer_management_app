// Página para editar un cliente
import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getCustomer, updateCustomer, deleteCustomer } from './api/client';

function EditCustomer({ onNotify }) {
    const { id } = useParams();
    const navigate = useNavigate();
    const [form, setForm] = useState(null);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState(null); // error general de carga
    const [deleting, setDeleting] = useState(false);
    // Errores de campos al editar (400 de backend) y error general de API al guardar
    const [fieldErrors, setFieldErrors] = useState({});
    const [apiError, setApiError] = useState(null);

    useEffect(() => {
        let ignore = false;
        async function load() {
            try {
                setLoading(true);
                const data = await getCustomer(id);
                if (!ignore) setForm({
                    firstName: data.firstName || '',
                    lastName: data.lastName || '',
                    email: data.email || '',
                    phone: data.phone || '',
                    address: data.address || ''
                });
            } catch (err) {
                if (!ignore) {
                    if (err?.status === 404) {
                        setError('Customer not found. Redirecting to search…');
                        onNotify?.('error', 'Customer not found.');
                        // Pequeño delay y volvemos a /edit
                        setTimeout(() => navigate('/edit'), 1500);
                    } else {
                        setError('Failed to load customer.');
                        onNotify?.('error', 'Failed to load customer.');
                    }
                }
            } finally {
                if (!ignore) setLoading(false);
            }
        }
        load();
        return () => { ignore = true; };
    }, [id]);

    // Limpia error del campo editado y actualiza el formulario
    function handleChange(e) {
        const { name, value } = e.target;
        setForm({ ...form, [name]: value });
        setFieldErrors((prev) => ({ ...prev, [name]: undefined }));
    }

    // Validación simple (igual que en Add): mínimos requeridos y formato de email
    function validate(data) {
        const errs = {};
        if (!data.firstName?.trim()) errs.firstName = 'First name is required.';
        if (!data.lastName?.trim()) errs.lastName = 'Last name is required.';
        if (!data.email?.trim()) {
            errs.email = 'Email is required.';
        } else if (!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(data.email)) {
            errs.email = 'Email is not valid.';
        }
        return errs;
    }

    async function handleSave(e) {
        e.preventDefault();
        // 1) Validación en el cliente para feedback inmediato
        const v = validate(form);
        if (Object.keys(v).length) {
            setFieldErrors(v);
            setApiError('Some fields are invalid. Please review and try again.');
            // Tomo el primer mensaje corto para el toast
            const order = ['firstName', 'lastName', 'email', 'phone', 'address'];
            const firstKey = order.find((k) => v[k]);
            const shortMsg = firstKey ? v[firstKey] : 'Invalid form.';
            onNotify?.('error', shortMsg);
            return;
        }

        try {
            setSaving(true);
            setApiError(null);
            setFieldErrors({});
            await updateCustomer(id, form);
            onNotify?.('success', 'Customer updated successfully!');
            // Navegar de vuelta al listado es opcional; por ahora nos quedamos aquí
        } catch (err) {
            const status = err?.status;
            if (status === 400) {
                // Intento mapear errores de validación por campo
                const fe = {};
                // Variantes posibles desde backend: fieldErrors [{field, message}] o errors
                const list = Array.isArray(err?.fieldErrors) ? err.fieldErrors : (Array.isArray(err?.errors) ? err.errors : []);
                list.forEach((e) => {
                    if (e?.field && e?.message) fe[e.field] = e.message;
                });
                setFieldErrors(fe);
                const firstMsg = list?.[0]?.message || (Object.keys(fe).length ? fe[Object.keys(fe)[0]] : null);
                const msg = firstMsg || 'Some fields are invalid.';
                setApiError(Object.keys(fe).length ? null : msg);
                onNotify?.('error', msg);
            } else if (status === 409) {
                // Conflicto típico: email duplicado
                const msg = 'Email already exists. Please use another one.';
                setFieldErrors({ ...fieldErrors, email: msg });
                onNotify?.('error', msg);
            } else if (status === 404) {
                const msg = 'Customer not found.';
                setApiError(msg);
                onNotify?.('error', msg);
            } else {
                const msg = 'Unexpected error. Please try again later.';
                setApiError(msg);
                onNotify?.('error', msg);
            }
        } finally {
            setSaving(false);
        }
    }

    async function handleDelete() {
        const confirmed = window.confirm('Are you sure you want to delete this customer? This action cannot be undone.');
        if (!confirmed) return;
        try {
            setDeleting(true);
            await deleteCustomer(id);
            onNotify?.('success', 'Customer deleted successfully!');
            // Volvemos al buscador de edición para seguir trabajando allí
            navigate('/edit');
        } catch (err) {
            onNotify?.('error', 'Failed to delete customer.');
        } finally {
            setDeleting(false);
        }
    }

    return (
        <div className="edit-customer-page">
            <h1>Edit Customer</h1>
            {loading && <p>Loading…</p>}
            {error && <p className="edit-error-text">{error}</p>}
            {form && (
                <form onSubmit={handleSave} className="edit-form">
                    {/* Error general del guardado (si no hay por campo) */}
                    {apiError && (
                        <div className="customer-form-error" role="alert" aria-live="polite">{apiError}</div>
                    )}
                    <input className="customer-form-input" name="firstName" placeholder="First Name" value={form.firstName} onChange={handleChange} />
                    {fieldErrors.firstName && <div className="customer-form-error">{fieldErrors.firstName}</div>}
                    <input className="customer-form-input" name="lastName" placeholder="Last Name" value={form.lastName} onChange={handleChange} />
                    {fieldErrors.lastName && <div className="customer-form-error">{fieldErrors.lastName}</div>}
                    {/* Email editable; si backend devuelve 409, mostramos inline */}
                    <input className="customer-form-input" name="email" placeholder="Email" value={form.email} onChange={handleChange} />
                    {fieldErrors.email && <div className="customer-form-error">{fieldErrors.email}</div>}
                    <input className="customer-form-input" name="phone" placeholder="Phone" value={form.phone} onChange={handleChange} />
                    {fieldErrors.phone && <div className="customer-form-error">{fieldErrors.phone}</div>}
                    <input className="customer-form-input" name="address" placeholder="Address" value={form.address} onChange={handleChange} />
                    {fieldErrors.address && <div className="customer-form-error">{fieldErrors.address}</div>}
            <div className="edit-actions">
                        <button className="customer-form-btn" type="submit" disabled={saving || deleting}>
                            {saving ? 'Saving…' : 'Save Changes'}
                        </button>
                        <button
                className="customer-form-btn edit-btn-danger"
                            type="button"
                            onClick={handleDelete}
                            disabled={saving || deleting}
                        >
                            {deleting ? 'Deleting…' : 'Delete'}
                        </button>
                    </div>
                </form>
            )}
        </div>
    );
}

export default EditCustomer;
