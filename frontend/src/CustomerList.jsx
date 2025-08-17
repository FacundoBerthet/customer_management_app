// Componente que muestra una lista de clientes
import Customer from "./Customer";

// Componente para mostrar una lista de clientes con estilo moderno
function CustomerList({ customers }) {
    return (
    <div className="customer-list">
        <h2 className="customer-list-title">Customer List</h2>
            <div className="customer-list-content">
                {customers.length === 0 ?
                (<p className="customer-list-empty">No customers found.</p>)
                :
                (customers.map((c, i) => (<Customer key={i} {...c} />)))}
            </div>
        </div>
    );
}

export default CustomerList;