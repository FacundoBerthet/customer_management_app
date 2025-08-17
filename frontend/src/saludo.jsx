function Saludo({nombre}) {
    // nombre es una prop que recibimos del componente padre
  return (
    <div>
      <h1>Customer Management App, Nice to Meet You {nombre}!</h1>
      <p>Welcome to the Customer Management App</p>
    </div>
  );
}

export default Saludo;