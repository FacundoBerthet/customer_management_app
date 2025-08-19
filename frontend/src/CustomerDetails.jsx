// Página de detalle para ver todos los datos del cliente.
// Responsabilidades:
// - Cargar un cliente por id desde la API.
// - Mostrar todos los campos disponibles (incluyendo timestamps).
// - Formatear fechas para que sean legibles localmente.
import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getCustomer } from './api/client';

function CustomerDetails() {
  // Tomo el :id de la URL
  const { id } = useParams();
  // Estados de ciclo de vida de la carga
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [data, setData] = useState(null);

  // Helper: formatea una fecha/hora ISO a una cadena local legible.
  // - Si el valor no existe, devuelve '—'.
  // - Si no se puede parsear como Date, devuelve el valor original.
  const formatDateTime = (value) => {
    if (!value) return '—';
    const d = new Date(value);
    if (Number.isNaN(d.getTime())) return String(value);
    return d.toLocaleString(undefined, {
      year: 'numeric', month: '2-digit', day: '2-digit',
      hour: '2-digit', minute: '2-digit'
    });
  };

  // Efecto: carga el customer por id al montar o cambiar :id
  useEffect(() => {
    let ignore = false;
    async function load() {
      try {
        setLoading(true);
        setError(null);
        const res = await getCustomer(id);
        if (!ignore) setData(res);
      } catch (err) {
        if (!ignore) setError('Failed to load customer details.');
      } finally {
        if (!ignore) setLoading(false);
      }
    }
    load();
    return () => { ignore = true; };
  }, [id]);

  return (
    <div className="details-page">
      <h1>Customer Details</h1>
      {loading && <p>Loading…</p>}
      {error && <p className="edit-error-text">{error}</p>}

      {data && (
        // Grilla de pares etiqueta/valor con todos los campos del customer
        <div className="details-grid">
          <div className="details-row"><span className="details-label">ID</span><span className="details-value">{data.id}</span></div>
          <div className="details-row"><span className="details-label">First Name</span><span className="details-value">{data.firstName}</span></div>
          <div className="details-row"><span className="details-label">Last Name</span><span className="details-value">{data.lastName}</span></div>
          <div className="details-row"><span className="details-label">Email</span><span className="details-value">{data.email}</span></div>
          <div className="details-row"><span className="details-label">Phone</span><span className="details-value">{data.phone || '—'}</span></div>
          <div className="details-row"><span className="details-label">Address</span><span className="details-value">{data.address || '—'}</span></div>
          <div className="details-row"><span className="details-label">Created At</span><span className="details-value">{formatDateTime(data.createdAt)}</span></div>
          <div className="details-row"><span className="details-label">Updated At</span><span className="details-value">{formatDateTime(data.updatedAt)}</span></div>
        </div>
      )}

      <div className="details-actions">
        <Link to="/list" className="home-card-link">Back to List</Link>
        {id && <Link to={`/edit/${id}`} className="home-card-link">Edit</Link>}
      </div>
    </div>
  );
}

export default CustomerDetails;
