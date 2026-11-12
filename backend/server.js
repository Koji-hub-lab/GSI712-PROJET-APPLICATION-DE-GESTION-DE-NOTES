const express = require('express');
const cors = require('cors');
const app = express();
const PORT = process.env.PORT || 5000;

app.use(cors());
app.use(express.json());

// Route de test
app.get('/api/status', (req, res) => {
    res.json({ message: "API de gestion de notes opérationnelle !" });
});

app.listen(PORT, () => {
    console.log(`Serveur démarré sur le port ${PORT}`);
});
