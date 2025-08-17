// Componente que muestra una lista de clientes
import Customer from "./Customer";

// Componente para mostrar una lista de clientes con estilo moderno
function CustomerList({ customers }) {
    return (
    <div className="list">
            <h2 className="list-title">Customer List</h2>
            {customers.length === 0 ?
        (<p className="list-empty">No customers found.</p>)
                :
                (customers.map((c, i) => (<Customer key={i} {...c} />)))}
        </div>
    );
}

export default CustomerList;