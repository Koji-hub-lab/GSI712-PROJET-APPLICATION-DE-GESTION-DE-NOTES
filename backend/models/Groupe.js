const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');
const Niveau = require('./Niveau');

const Groupe = sequelize.define('Groupe', {
    id: { type: DataTypes.INTEGER, autoIncrement: true, primaryKey: true },
    nom: { type: DataTypes.STRING, allowNull: false } // ex: Groupe A, Groupe B
}, { tableName: 'groupes', timestamps: true });

Groupe.belongsTo(Niveau, { foreignKey: 'niveauId', onDelete: 'CASCADE' });
Niveau.hasMany(Groupe, { foreignKey: 'niveauId' });

module.exports = Groupe;