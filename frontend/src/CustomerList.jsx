// Componente que muestra una lista de clientes
import { useState, useEffect } from 'react';
import Customer from "./Customer";
import { getCustomers } from './api/client';

// Componente para mostrar una lista de clientes con estilo moderno
function CustomerList({ customers = [] }) {
    // Estado local que arranca con lo que llega por props (mock o ya real)
    const [data, setData] = useState(customers);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // Acción manual: cargo desde el backend y reemplazo el estado local
    async function loadFromBackend() {
        try {
            setLoading(true);
            setError(null);
            const list = await getCustomers(); // GET /api/customers
            setData(list);
        } catch (err) {
            // Si el backend respondió con error, muestro un mensaje simple
            setError('Failed to load customers. Please try again.');
            // Para debug en consola dejo el detalle completo
            // console.error('loadFromBackend error', err);
        } finally {
            setLoading(false);
        }
    }

    // Auto-carga inicial al montar el componente
    // ¿Por qué? Así el listado muestra datos reales sin requerir acción del usuario.
    // Mantengo el botón para refrescar manualmente cuando se desee.
    useEffect(() => {
        loadFromBackend();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    return (
        <div className="customer-list-container">
            <div className="customer-list">
                <h2 className="customer-list-title">Customer List</h2>

                {/* Botonera superior: refresco manual desde backend cuando yo quiera */}
                <div style={{ display: 'flex', justifyContent: 'flex-end', padding: '0 1rem 1rem' }}>
                    <button className="customer-form-btn" onClick={loadFromBackend} disabled={loading}>
                        {loading ? 'Loading…' : 'Refresh'}
                    </button>
                </div>

                {/* Mensaje de error simple (si falló la carga) */}
                {error && (
                    <div className="customer-list-content">
                        <p className="customer-list-empty">{error}</p>
                    </div>
                )}

                {/* Contenido principal: uso el estado local "data" */}
                <div className="customer-list-content">
                    {loading ? (
                        // Mensaje simple durante la carga inicial o refresco
                        <p className="customer-list-empty">Loading customers...</p>
                    ) : (
                        data.length === 0 ? (
                            <p className="customer-list-empty">No customers found.</p>
                        ) : (
                            data.map((c, i) => (
                                // Mantengo la key por índice mientras no tengamos IDs en props
                                <Customer key={c.id ?? i} {...c} />
                            ))
                        )
                    )}
                </div>
            </div>
        </div>
    );
}

export default CustomerList;