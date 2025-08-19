//Cliente API para conectar el frontend con el backend Spring Boot


// BASE_URL es la URL base para la API de clientes.
// - En dev: usar VITE_API_URL en .env (por ej. http://localhost:8080/api)
// - En prod (Nginx): usamos ruta relativa '/api' para aprovechar el reverse proxy
const API_BASE = import.meta.env.VITE_API_URL || '/api';
const BASE_URL = `${API_BASE.replace(/\/$/, '')}/customers`;

// Helper para manejar errores y parsear JSON
async function fetchJson(url, options = {}) {
    try {
        const res = await fetch(url, options);
        // Si la respuesta no es exitosa, lanzo error con status
        if (!res.ok) {
            const error = await res.json().catch(() => ({}));
            throw { status: res.status, ...error };
        }
        return await res.json();
    } catch (err) {
        throw err;
    }
}

// Obtener todos los clientes
export async function getCustomers() {
    // GET /api/customers
    return fetchJson(`${BASE_URL}`);
}

// Obtener un cliente por ID
export async function getCustomer(id) {
    // GET /api/customers/{id}
    return fetchJson(`${BASE_URL}/${id}`);
}

// Crear un nuevo cliente
export async function createCustomer(data) {
    // POST /api/customers
    return fetchJson(`${BASE_URL}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });
}

// Actualizar un cliente existente
export async function updateCustomer(id, data) {
    // PUT /api/customers/{id}
    return fetchJson(`${BASE_URL}/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });
}

// Eliminar un cliente
export async function deleteCustomer(id) {
    // DELETE /api/customers/{id}
    const res = await fetch(`${BASE_URL}/${id}`, { method: 'DELETE' });
    if (!res.ok) {
        const error = await res.json().catch(() => ({}));
        throw { status: res.status, ...error };
    }
    return true; // Devuelvo true si se eliminó correctamente
}

// Obtener clientes paginados
export async function getCustomersPaged(page = 0, size = 10, sort = 'id,DESC') {
    // GET /api/customers/page?page=0&size=10&sort=id,DESC
    const params = new URLSearchParams({ page, size, sort });
    return fetchJson(`${BASE_URL}/page?${params}`);
}

// Buscar clientes paginados
export async function searchCustomersPaged(q, page = 0, size = 10, sort = 'id,DESC') {
    // GET /api/customers/search/page?q=...&page=0&size=10&sort=id,DESC
    const params = new URLSearchParams({ q, page, size, sort });
    return fetchJson(`${BASE_URL}/search/page?${params}`);
}
