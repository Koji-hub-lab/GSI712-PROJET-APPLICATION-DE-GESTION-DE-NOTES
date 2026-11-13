const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');

const Semestre = sequelize.define('Semestre', {
    id: { type: DataTypes.INTEGER, autoIncrement: true, primaryKey: true },
    code: { type: DataTypes.STRING, unique: true, allowNull: false }, // ex: S1, S2 ... S10
    libelle: { type: DataTypes.STRING, allowNull: false }
}, { tableName: 'semestres', timestamps: true });

module.exports = Semestre;