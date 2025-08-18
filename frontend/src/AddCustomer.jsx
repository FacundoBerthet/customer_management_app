// Página para agregar un cliente
import CustomerForm from './CustomerForm';

function AddCustomer({ onAddCustomer }) {
    const tutorial = (
        <ol className="customer-form-tutorial">
            <li>Fill in First Name, Last Name and Email (fields marked with * are required).</li>
            <li>Use a valid email format (e.g., name@example.com) to continue.</li>
            <li>Optionally, add Phone and Address for better contact information.</li>
            <li>Click “Add Customer” to save the new customer in the list.</li>
            <li>If any field is invalid, you’ll see an error message under the input.</li>
        </ol>
    );

    return (
        <div className="add-customer-page">
            <CustomerForm onAddCustomer={onAddCustomer} tutorial={tutorial} />
        </div>
    );
}

export default AddCustomer;
