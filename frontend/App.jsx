import { useState, useEffect } from 'react';

function App() {
  const [status, setStatus] = useState("Chargement...");

  useEffect(() => {
    fetch('http://localhost:5000/api/status')
      .then(res => res.json())
      .then(data => setStatus(data.message))
      .catch(err => setStatus("Erreur de connexion au backend"));
  }, []);

  return (
    <div style={{ padding: '20px', fontFamily: 'Arial' }}>
      <h1>Gestion de Notes - Sprint 1</h1>
      <p>Statut du serveur : <strong>{status}</strong></p>
    </div>
  );
}

export default App;
