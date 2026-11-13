const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');

const UE = sequelize.define('UE', {
    id: { type: DataTypes.INTEGER, autoIncrement: true, primaryKey: true },
    code: { type: DataTypes.STRING, unique: true, allowNull: false }, // ex: INF101
    libelle: { type: DataTypes.STRING, allowNull: false },
    credits: { type: DataTypes.INTEGER, allowNull: false } // Crédits ECTS
}, { tableName: 'ues', timestamps: true });

module.exports = UE;