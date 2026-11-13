const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');

const User = sequelize.define('User', {
    id: {
        type: DataTypes.INTEGER,
        autoIncrement: true,
        primaryKey: true
    },
    identifiant: {
        type: DataTypes.STRING,
        unique: true,
        allowNull: false // Matricule étudiant (ex: 26ESTLC001), code enseignant ou login admin
    },
    password: {
        type: DataTypes.STRING,
        allowNull: false // Stocké sous forme de hash bcrypt
    },
    role: {
        type: DataTypes.ENUM('Administrateur', 'Enseignant', 'Étudiant'),
        allowNull: false
    },
    nom: {
        type: DataTypes.STRING,
        allowNull: false
    },
    prenom: {
        type: DataTypes.STRING,
        allowNull: true
    },
    email: {
        type: DataTypes.STRING,
        unique: true,
        allowNull: true
    }
}, {
    tableName: 'users',
    timestamps: true
});

module.exports = User;