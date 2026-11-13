const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');
const Niveau = require('./Niveau');

const Semestre = sequelize.define('Semestre', {
    id: { type: DataTypes.INTEGER, autoIncrement: true, primaryKey: true },
    code: { type: DataTypes.STRING, unique: true, allowNull: false }, // ex: S1, S2 ... S10
    libelle: { type: DataTypes.STRING, allowNull: false }
}, { tableName: 'semestres', timestamps: true });

Semestre.belongsTo(Niveau, { foreignKey: 'niveauId', onDelete: 'CASCADE' });
Niveau.hasMany(Semestre, { foreignKey: 'niveauId' });

module.exports = Semestre;