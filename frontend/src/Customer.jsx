//Componente para mostrar la información de un cliente

function Customer({firstName, lastName, email, phone, address}) {
    return (
        <div className="customer-card">
            <h3 className="customer-card-title">{firstName} {lastName}</h3>
            <p className="customer-card-info"><span>Email:</span> {email}</p>
            <p className="customer-card-info"><span>Phone:</span> {phone}</p>
            <p className="customer-card-info"><span>Address:</span> {address}</p>
        </div>
    );
}

export default Customer;
