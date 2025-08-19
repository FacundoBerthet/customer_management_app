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
    const [error, setError] = useState(null);
    const [deleting, setDeleting] = useState(false);

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
                if (!ignore) setError('Failed to load customer.');
            } finally {
                if (!ignore) setLoading(false);
            }
        }
        load();
        return () => { ignore = true; };
    }, [id]);

    function handleChange(e) {
        setForm({ ...form, [e.target.name]: e.target.value });
    }

    async function handleSave(e) {
        e.preventDefault();
        try {
            setSaving(true);
            await updateCustomer(id, form);
            onNotify?.('success', 'Customer updated successfully!');
            // Navegar de vuelta al listado es opcional; por ahora nos quedamos aquí
        } catch (err) {
            onNotify?.('error', 'Failed to update customer.');
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
            {error && <p style={{ color: '#ff5252' }}>{error}</p>}
            {form && (
                <form onSubmit={handleSave} style={{ display: 'grid', gap: '0.7rem', marginTop: '1rem' }}>
                    <input className="customer-form-input" name="firstName" placeholder="First Name" value={form.firstName} onChange={handleChange} />
                    <input className="customer-form-input" name="lastName" placeholder="Last Name" value={form.lastName} onChange={handleChange} />
                    <input className="customer-form-input" name="email" placeholder="Email" value={form.email} onChange={handleChange} />
                    <input className="customer-form-input" name="phone" placeholder="Phone" value={form.phone} onChange={handleChange} />
                    <input className="customer-form-input" name="address" placeholder="Address" value={form.address} onChange={handleChange} />
                    <div style={{ display: 'flex', gap: '0.6rem' }}>
                        <button className="customer-form-btn" type="submit" disabled={saving || deleting}>
                            {saving ? 'Saving…' : 'Save Changes'}
                        </button>
                        <button
                            className="customer-form-btn"
                            type="button"
                            onClick={handleDelete}
                            disabled={saving || deleting}
                            style={{ background: '#e53935', color: '#fff' }}
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
