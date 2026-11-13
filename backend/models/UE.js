const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');
const Semestre = require('./Semestre');

const UE = sequelize.define('UE', {
    id: { type: DataTypes.INTEGER, autoIncrement: true, primaryKey: true },
    code: { type: DataTypes.STRING, unique: true, allowNull: false }, // ex: INF101
    libelle: { type: DataTypes.STRING, allowNull: false },
    credits: { type: DataTypes.INTEGER, allowNull: false } // Crédits ECTS
}, { tableName: 'ues', timestamps: true });

UE.belongsTo(Semestre, { foreignKey: 'semestreId', onDelete: 'CASCADE' });
Semestre.hasMany(UE, { foreignKey: 'semestreId' });

module.exports = UE;