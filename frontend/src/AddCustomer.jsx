// Página para agregar un cliente
import CustomerForm from './CustomerForm';

function AddCustomer({ onAddCustomer }) {
    return (
        <div className="add-customer-page">
            <h1>Add Customer</h1>
            <CustomerForm onAddCustomer={onAddCustomer} />
        </div>
    );
}

export default AddCustomer;
