const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');

const Diplome = sequelize.define('Diplome', {
    id: { type: DataTypes.INTEGER, autoIncrement: true, primaryKey: true },
    code: { type: DataTypes.STRING, unique: true, allowNull: false }, // ex: GSI, GSA
    libelle: { type: DataTypes.STRING, allowNull: false } // ex: Génie des Systèmes Informatiques
}, { tableName: 'diplomes', timestamps: true });

module.exports = Diplome;