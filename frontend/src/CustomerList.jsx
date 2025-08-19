// Componente que muestra una lista de clientes (paginada server-side)
import { useEffect, useMemo, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import Customer from "./Customer";
import { getCustomersPaged, searchCustomersPaged } from './api/client';

// Defaults alineados al backend (PageResponse)
const DEFAULT_PAGE = 0;
const DEFAULT_SIZE = 10; // el backend capa a 50
const DEFAULT_SORT = 'id,DESC';

function CustomerList() {
    // Sin props: siempre usamos backend paginado
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [pageData, setPageData] = useState({ content: [], page: 0, size: DEFAULT_SIZE, totalElements: 0, totalPages: 0, first: true, last: true });

    const [searchParams, setSearchParams] = useSearchParams();
    // Leo page/size/sort/q desde la URL; si no están, uso defaults
    const page = useMemo(() => {
        const p = Number(searchParams.get('page'));
        return Number.isFinite(p) && p >= 0 ? p : DEFAULT_PAGE;
    }, [searchParams]);
    const size = useMemo(() => {
        const s = Number(searchParams.get('size'));
        return Number.isFinite(s) && s > 0 ? s : DEFAULT_SIZE;
    }, [searchParams]);
    const sort = searchParams.get('sort') || DEFAULT_SORT;
    // Helper para parsear el sort "field,DIRECTION"
    const parseSort = (s) => {
        const [field, dir] = String(s || DEFAULT_SORT).split(',');
        const direction = (dir || 'DESC').toUpperCase() === 'ASC' ? 'ASC' : 'DESC';
        const safeField = field || 'id';
        return { field: safeField, direction };
    };
    const { field: sortField, direction: sortDir } = useMemo(() => parseSort(sort), [sort]);
    const qParam = searchParams.get('q') || '';

    // Mantengo un estado controlado para el input de búsqueda
    const [qInput, setQInput] = useState(qParam);
    useEffect(() => {
        // Si cambia la URL externamente, sincronizo el input
        if (qParam !== qInput) setQInput(qParam);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [qParam]);

    // Debounce simple para evitar golpear el backend en cada tecla
    function useDebouncedValue(value, delay = 450) {
        const [debounced, setDebounced] = useState(value);
        useEffect(() => {
            const id = setTimeout(() => setDebounced(value), delay);
            return () => clearTimeout(id);
        }, [value, delay]);
        return debounced;
    }
    const debouncedQ = useDebouncedValue(qInput, 450);

    // Normaliza la respuesta del backend para evitar NaN/undefined
    function normalize(res) {
        const content = Array.isArray(res?.content) ? res.content : [];
        const page = Number.isFinite(res?.page) ? res.page : 0;
        const size = Number.isFinite(res?.size) ? res.size : DEFAULT_SIZE;
        const totalElements = Number.isFinite(res?.totalElements) ? res.totalElements : content.length;
        const totalPages = Number.isFinite(res?.totalPages) ? res.totalPages : (size > 0 ? Math.ceil(totalElements / size) : 0);
        const first = typeof res?.first === 'boolean' ? res.first : page === 0;
        const last = typeof res?.last === 'boolean' ? res.last : (totalPages <= 1 || page >= totalPages - 1);
        return { content, page, size, totalElements, totalPages, first, last };
    }

    async function load() {
        try {
            setLoading(true);
            setError(null);
            const term = (debouncedQ || '').trim();
            const data = term
                ? await searchCustomersPaged(term, page, size, sort)
                : await getCustomersPaged(page, size, sort);
            setPageData(normalize(data));
        } catch (err) {
            if (err?.status === 400) {
                // Parámetros inválidos: reseteo a defaults de forma amable
                setError('Invalid paging parameters. Resetting to defaults.');
                const next = new URLSearchParams(searchParams);
                next.set('page', String(DEFAULT_PAGE));
                next.set('size', String(DEFAULT_SIZE));
                next.set('sort', DEFAULT_SORT);
                setSearchParams(next);
            } else {
                setError('Failed to load customers. Please try again.');
            }
        } finally {
            setLoading(false);
        }
    }

    // Aseguro que haya params por defecto en la URL al montar
    useEffect(() => {
        const hasPage = searchParams.has('page');
        const hasSize = searchParams.has('size');
        const hasSort = searchParams.has('sort');
        if (!hasPage || !hasSize || !hasSort) {
            const next = new URLSearchParams(searchParams);
            if (!hasPage) next.set('page', String(DEFAULT_PAGE));
            if (!hasSize) next.set('size', String(DEFAULT_SIZE));
            if (!hasSort) next.set('sort', DEFAULT_SORT);
            setSearchParams(next);
            return; // esperar a que la URL se actualice para cargar
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    // Cargar cada vez que cambian los parámetros o la búsqueda (debounced)
    useEffect(() => {
        load();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [page, size, sort, debouncedQ]);

    function goToPage(p) {
        const next = new URLSearchParams(searchParams);
        next.set('page', String(Math.max(0, p)));
        setSearchParams(next);
    }

    function handleSearchChange(e) {
        const value = e.target.value;
        setQInput(value);
        const next = new URLSearchParams(searchParams);
        if (value.trim()) {
            next.set('q', value);
        } else {
            next.delete('q');
        }
        // Al cambiar la búsqueda, reseteo a la primera página
        next.set('page', String(DEFAULT_PAGE));
        setSearchParams(next);
    }

    // Cambios de ordenamiento: actualizo 'sort' en la URL y reseteo a page 0
    function handleSortFieldChange(e) {
        const next = new URLSearchParams(searchParams);
        next.set('sort', `${e.target.value},${sortDir}`);
        next.set('page', String(DEFAULT_PAGE));
        setSearchParams(next);
    }
    function handleSortDirChange(e) {
        const next = new URLSearchParams(searchParams);
        next.set('sort', `${sortField},${e.target.value}`);
        next.set('page', String(DEFAULT_PAGE));
        setSearchParams(next);
    }

    return (
        <div className="customer-list-container">
            <div className="customer-list">
                <h2 className="customer-list-title">Customer List</h2>

                {/* Controles superiores: búsqueda + estado de paginado/navegación */}
                <div className="customer-list-toolbar">
                    <div className="customer-list-filterbox">
                        <input
                            className="customer-form-input customer-list-search-input"
                            placeholder="Search by name, email, phone, or address"
                            value={qInput}
                            onChange={handleSearchChange}
                        />
                        <div className="customer-list-sort">
                            <label className="customer-list-sort-label" htmlFor="sort-field">Sort</label>
                            <select
                                id="sort-field"
                                className="customer-list-sort-select"
                                value={sortField}
                                onChange={handleSortFieldChange}
                            >
                                <option value="createdAt">Created</option>
                                <option value="updatedAt">Updated</option>
                                <option value="lastName">Last name</option>
                                <option value="firstName">First name</option>
                                <option value="email">Email</option>
                                <option value="id">ID</option>
                            </select>
                            <select
                                className="customer-list-sort-select"
                                value={sortDir}
                                onChange={handleSortDirChange}
                            >
                                <option value="DESC">Desc</option>
                                <option value="ASC">Asc</option>
                            </select>
                        </div>
                    </div>
                    <div className="customer-list-pageinfo">
                        Page {pageData.totalPages > 0 ? pageData.page + 1 : 1} of {pageData.totalPages || 1}
                        {typeof pageData.totalElements === 'number' && (
                            <span> — {pageData.totalElements} results</span>
                        )}
                    </div>
                    <div className="customer-list-pager">
                        <button className="customer-form-btn" disabled={loading || pageData.first} onClick={() => goToPage(pageData.page - 1)}>Prev</button>
                        <button className="customer-form-btn" disabled={loading || pageData.last} onClick={() => goToPage(pageData.page + 1)}>Next</button>
                    </div>
                </div>

                {/* Error global */}
                {error && (
                    <div className="customer-list-content">
                        <p className="customer-list-empty">{error}</p>
                    </div>
                )}

                {/* Contenido principal */}
                <div className="customer-list-content">
                    {loading ? (
                        <p className="customer-list-empty">Loading customers...</p>
                    ) : (
                        (!pageData.content || pageData.content.length === 0) ? (
                            <p className="customer-list-empty">No customers found.</p>
                        ) : (
                            pageData.content.map((c) => (
                                <Customer key={c.id} {...c} />
                            ))
                        )
                    )}
                </div>
            </div>
        </div>
    );
}

export default CustomerList;