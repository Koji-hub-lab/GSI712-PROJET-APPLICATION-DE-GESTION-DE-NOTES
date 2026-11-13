const { Sequelize } = require('sequelize');
require('dotenv').config();

// Connexion à MySQL avec les variables d'environnement
const sequelize = new Sequelize(
    process.env.DB_NAME || 'estlc_db',
    process.env.DB_USER || 'root',
    process.env.DB_PASS || '',
    {
        host: process.env.DB_HOST || 'localhost',
        dialect: 'mysql',
        logging: false, // Désactive les logs SQL dans la console
    }
);

module.exports = sequelize;