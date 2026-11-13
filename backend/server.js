const express = require('express');
const cors = require('cors');
const sequelize = require('./config/database');
require('dotenv').config();

// --- IMPORTATION DES MODELES DU REFERENTIEL & AUTH ---
require('./models/User');
require('./models/Diplome');
require('./models/Niveau');
require('./models/Semestre');
require('./models/UE');
require('./models/EC');
require('./models/Groupe');

const app = express();
const PORT = process.env.PORT || 5000;

// Middlewares globaux
app.use(cors());
app.use(express.json());

// Route de test
app.get('/api/status', (req, res) => {
    res.json({ message: "API de gestion de notes opérationnelle !" });
});

// --- MONTAGE DES ROUTES AUTH ET ADMIN ---
const authRoutes = require('./routes/authRoutes');
const adminRoutes = require('./routes/adminRoutes');
app.use('/api/auth', authRoutes);
app.use('/api/admin', adminRoutes);

// Synchronisation de la BDD et démarrage du serveur
sequelize.sync({ alter: true })
    .then(() => {
        console.log('Base de données MySQL synchronisée avec succès.');
        app.listen(PORT, () => {
            console.log(`Serveur démarré sur le port ${PORT}`);
        });
    })
    .catch(err => {
        console.error('Erreur de connexion à la base de données :', err);
    });