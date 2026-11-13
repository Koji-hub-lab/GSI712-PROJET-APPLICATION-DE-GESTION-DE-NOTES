const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');

const Note = sequelize.define('Note', {
    id: {
        type: DataTypes.INTEGER,
        autoIncrement: true,
        primaryKey: true
    },
    note_cc: {
        type: DataTypes.FLOAT,
        allowNull: true,
        defaultValue: 0.0
    },
    note_tp: {
        type: DataTypes.FLOAT,
        allowNull: true,
        defaultValue: 0.0
    },
    note_examen: {
        type: DataTypes.FLOAT,
        allowNull: true,
        defaultValue: 0.0
    },
    note_finale: {
        type: DataTypes.FLOAT,
        allowNull: true,
        defaultValue: 0.0
    },
    session: {
        type: DataTypes.ENUM('Normale', 'Rattrapage'),
        defaultValue: 'Normale',
        allowNull: false
    }
}, {
    tableName: 'notes',
    timestamps: true
});

module.exports = Note;