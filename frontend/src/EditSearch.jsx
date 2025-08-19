// Página de búsqueda para seleccionar un cliente a editar
// - Permite buscar por cualquier atributo (firstName, lastName, email, phone, address)
// - Muestra coincidencias con un botón "Edit" que navega a /edit/:id
// - Debounce para evitar llamar al backend en cada tecla
// - Paginación simple (Prev/Next)
import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { searchCustomersPaged } from './api/client';

// Hook simple para debouncer: espera "delay" ms antes de publicar el último valor
function useDebouncedValue(value, delay = 400) {
  const [debounced, setDebounced] = useState(value);
  useEffect(() => {
    const id = setTimeout(() => setDebounced(value), delay);
    return () => clearTimeout(id);
  }, [value, delay]);
  return debounced;
}

function EditSearch() {
  const [q, setQ] = useState('');
  const debouncedQ = useDebouncedValue(q, 450);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  // Estado de paginación con valores por defecto seguros
  const [pageData, setPageData] = useState({ content: [], number: 0, totalPages: 0, totalElements: 0, size: 10 });

  const canSearch = (debouncedQ ?? '').trim().length > 0;

  // Normaliza la respuesta del backend para evitar "NaN" o undefined
  function normalizePageData(res) {
    const content = Array.isArray(res?.content) ? res.content : [];
    const size = Number.isFinite(res?.size) ? res.size : 10;
    const number = Number.isFinite(res?.number) ? res.number : 0;
    const totalElements = Number.isFinite(res?.totalElements) ? res.totalElements : content.length;
    // Si el backend no envía totalPages, lo calculo con totalElements y size
    const totalPages = Number.isFinite(res?.totalPages)
      ? res.totalPages
      : (size > 0 ? Math.ceil(totalElements / size) : 0);
    return { content, size, number, totalElements, totalPages };
  }

  async function doSearch(page = 0) {
    if (!canSearch) {
      // Query vacía: limpio resultados y desactivo paginación
      setPageData({ content: [], number: 0, totalPages: 0, totalElements: 0, size: pageData.size || 10 });
      return;
    }
    try {
      setLoading(true);
      setError(null);
      const res = await searchCustomersPaged(debouncedQ, page, pageData.size || 10, 'id,DESC');
      setPageData(normalizePageData(res));
    } catch (err) {
      setError('Failed to search customers.');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    doSearch(0);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [debouncedQ]);

  const hasResults = pageData.content && pageData.content.length > 0;
  // Fallbacks seguros para evitar "Page NaN"
  const currentIndex = Number.isFinite(pageData.number) ? pageData.number : 0;
  const totalPagesSafe = Number.isFinite(pageData.totalPages) && pageData.totalPages > 0 ? pageData.totalPages : 1;

  return (
    <div className="edit-customer-page">
      <h1>Find a Customer to Edit</h1>
      <input
        className="customer-form-input"
        placeholder="Search by name, email, phone, or address"
        value={q}
        onChange={(e) => setQ(e.target.value)}
        style={{ maxWidth: 480 }}
      />
      <div style={{ marginTop: '0.8rem', minHeight: '1.2rem' }}>
        {loading && <span style={{ color: '#b0b0b0' }}>Searching…</span>}
        {error && <span style={{ color: '#ff5252' }}>{error}</span>}
      </div>

      <div className="customer-list-content" style={{ marginTop: '1rem' }}>
        {!canSearch && <p className="customer-list-empty">Type something to search.</p>}
        {canSearch && !loading && !hasResults && <p className="customer-list-empty">No matches found.</p>}
        {hasResults && pageData.content.map((c) => (
          <div key={c.id} className="customer-card">
            <h3 className="customer-card-title">{c.firstName} {c.lastName}</h3>
            <p className="customer-card-info"><span>Email:</span> {c.email}</p>
            <p className="customer-card-info"><span>Phone:</span> {c.phone}</p>
            <p className="customer-card-info"><span>Address:</span> {c.address}</p>
            <div style={{ marginTop: '0.8rem', textAlign: 'right' }}>
              <Link to={`/edit/${c.id}`} className="home-card-link">Edit</Link>
            </div>
          </div>
        ))}
      </div>

      {/* Paginación simple */}
  {hasResults && (
        <div style={{ display: 'flex', gap: '0.6rem', justifyContent: 'center', marginTop: '1rem' }}>
          <button
            className="customer-form-btn"
    disabled={loading || currentIndex <= 0}
    onClick={() => doSearch(currentIndex - 1)}
          >
            Prev
          </button>
          <span style={{ alignSelf: 'center', color: '#b0b0b0' }}>
    Page {currentIndex + 1} of {totalPagesSafe}
          </span>
          <button
            className="customer-form-btn"
    disabled={loading || currentIndex >= totalPagesSafe - 1}
    onClick={() => doSearch(currentIndex + 1)}
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
}

export default EditSearch;
