const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');

const Niveau = sequelize.define('Niveau', {
    id: { type: DataTypes.INTEGER, autoIncrement: true, primaryKey: true },
    code: { type: DataTypes.STRING, allowNull: false }, // ex: ING1, ING2, ING3
    libelle: { type: DataTypes.STRING, allowNull: false }
}, { tableName: 'niveaux', timestamps: true });

module.exports = Niveau;