const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');
const Diplome = require('./Diplome');

const Niveau = sequelize.define('Niveau', {
    id: { type: DataTypes.INTEGER, autoIncrement: true, primaryKey: true },
    code: { type: DataTypes.STRING, allowNull: false }, // ex: ING1, ING2, ING3
    libelle: { type: DataTypes.STRING, allowNull: false }
}, { tableName: 'niveaux', timestamps: true });

// Association : Un niveau appartient à un diplôme
Niveau.belongsTo(Diplome, { foreignKey: 'diplomeId', onDelete: 'CASCADE' });
Diplome.hasMany(Niveau, { foreignKey: 'diplomeId' });

module.exports = Niveau;