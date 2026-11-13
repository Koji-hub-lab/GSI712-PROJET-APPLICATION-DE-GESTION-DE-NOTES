const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');
const UE = require('./UE');

const EC = sequelize.define('EC', {
    id: { type: DataTypes.INTEGER, autoIncrement: true, primaryKey: true },
    code_ec: { type: DataTypes.STRING, unique: true, allowNull: false },
    libelle: { type: DataTypes.STRING, allowNull: false },
    coefficient: { type: DataTypes.FLOAT, allowNull: false },
    // Pondérations en pourcentage (ex: CC = 30%, Examen = 70%)
    poids_cc: { type: DataTypes.FLOAT, defaultValue: 0.3 },
    poids_tp: { type: DataTypes.FLOAT, defaultValue: 0.0 },
    poids_examen: { type: DataTypes.FLOAT, defaultValue: 0.7 }
}, { tableName: 'ecs', timestamps: true });

EC.belongsTo(UE, { foreignKey: 'ueId', onDelete: 'CASCADE' });
UE.hasMany(EC, { foreignKey: 'ueId' });

module.exports = EC;