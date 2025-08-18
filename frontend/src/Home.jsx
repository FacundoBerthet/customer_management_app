// Página de bienvenida con 4 cartas
import { Link } from 'react-router-dom';

function Home() {
    return (
        <div className="home">
            <div className="home-cards">
                {/* Carta: Bienvenida */}
                <div className="home-card">
                    <h2>Welcome to Customer Management App</h2>
                    <p>Manage your customers easily. Use the navigation menu to add, list, or edit customers. Track essential details securely and keep data organized across simple, focused pages for everyday workflows.</p>
                </div>

                {/* Carta: Add Customer */}
                <div className="home-card">
                    <h2>Add Customer</h2>
                    <p>Create a new customer by filling out the form with basic information. Validate required fields, see clear errors, and get feedback when the record is saved successfully.</p>
                    <Link className="home-card-link" to="/add">Go to Add</Link>
                </div>

                {/* Carta: Customer List */}
                <div className="home-card">
                    <h2>Customer List</h2>
                    <p>Browse all customers, search, and see details in one place. View concise cards, paginate large datasets, and quickly find people using filters and keywords.</p>
                    <Link className="home-card-link" to="/list">Go to List</Link>
                </div>

                {/* Carta: Edit Customer */}
                <div className="home-card">
                    <h2>Edit Customer</h2>
                    <p>Update an existing customer's info. Select a customer from the list to edit. Modify names, contact details, and addresses, then save changes safely with instant confirmations.</p>
                    <Link className="home-card-link" to="/edit/1">Go to Edit </Link>
                </div>
            </div>
        </div>
    );
}

export default Home;
