//Componente para mostrar la información de un cliente
import { Link } from 'react-router-dom';

function Customer({ id, firstName, lastName, email, phone, address }) {
    return (
        <div className="customer-card">
            <h3 className="customer-card-title">{firstName} {lastName}</h3>
            <p className="customer-card-info"><span>Email:</span> {email}</p>
            <p className="customer-card-info"><span>Phone:</span> {phone}</p>
            <p className="customer-card-info"><span>Address:</span> {address}</p>
            {id != null && (
                <div className="customer-card-actions">
                    <Link to={`/edit/${id}`} className="home-card-link">Edit</Link>
                </div>
            )}
        </div>
    );
}

export default Customer;
