const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');

const Groupe = sequelize.define('Groupe', {
    id: { type: DataTypes.INTEGER, autoIncrement: true, primaryKey: true },
    nom: { type: DataTypes.STRING, allowNull: false } // ex: Groupe A, Groupe B
}, { tableName: 'groupes', timestamps: true });

module.exports = Groupe;