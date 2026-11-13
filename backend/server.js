const express = require('express');
const cors = require('cors');
const sequelize = require('./config/database');
require('dotenv').config();

// --- IMPORTATION DES MODELES ---
const User = require('./models/User');
const Diplome = require('./models/Diplome');
const Niveau = require('./models/Niveau');
const Semestre = require('./models/Semestre');
const UE = require('./models/UE');
const EC = require('./models/EC');
const Note = require('./models/Note');
const Groupe = require('./models/Groupe');

// --- RELATIONS DU REFERENTIEL ---
Diplome.hasMany(Niveau, { foreignKey: 'diplomeId', onDelete: 'CASCADE' });
Niveau.belongsTo(Diplome, { foreignKey: 'diplomeId', onDelete: 'CASCADE' });

Niveau.hasMany(Semestre, { foreignKey: 'niveauId', onDelete: 'CASCADE' });
Semestre.belongsTo(Niveau, { foreignKey: 'niveauId', onDelete: 'CASCADE' });

Semestre.hasMany(UE, { foreignKey: 'semestreId', onDelete: 'CASCADE' });
UE.belongsTo(Semestre, { foreignKey: 'semestreId', onDelete: 'CASCADE' });

UE.hasMany(EC, { foreignKey: 'ueId', onDelete: 'CASCADE' });
EC.belongsTo(UE, { foreignKey: 'ueId', onDelete: 'CASCADE' });

Niveau.hasMany(Groupe, { foreignKey: 'niveauId', onDelete: 'CASCADE' });
Groupe.belongsTo(Niveau, { foreignKey: 'niveauId', onDelete: 'CASCADE' });

// --- RELATIONS DES ENSEIGNANTS ET DES NOTES ---
User.hasMany(EC, { foreignKey: 'enseignantId', as: 'Enseignements' });
EC.belongsTo(User, { foreignKey: 'enseignantId', as: 'Enseignant' });

User.hasMany(Note, { foreignKey: 'etudiantId', onDelete: 'CASCADE' });
Note.belongsTo(User, { foreignKey: 'etudiantId', as: 'Etudiant' });

EC.hasMany(Note, { foreignKey: 'ecId', onDelete: 'CASCADE' });
Note.belongsTo(EC, { foreignKey: 'ecId', onDelete: 'CASCADE' });

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
const noteRoutes = require('./routes/noteRoutes');
app.use('/api/auth', authRoutes);
app.use('/api/admin', adminRoutes);
app.use('/api/notes', noteRoutes);

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