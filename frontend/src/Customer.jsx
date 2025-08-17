//Componente para mostrar la información de un cliente

// Componente para mostrar la información de un cliente con estilo moderno
function Customer({firstName, lastName, email, phone, address}) {
    return (
        <div className="card">
            <h3 className="customer-title">{firstName} {lastName}</h3>
            <p className="card-info"><span>Email:</span> {email}</p>
            <p className="card-info"><span>Phone:</span> {phone}</p>
            <p className="card-info"><span>Address:</span> {address}</p>
        </div>
    );
}

export default Customer;
