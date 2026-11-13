const Diplome = require('../models/Diplome');
const Niveau = require('../models/Niveau');
const Semestre = require('../models/Semestre');
const UE = require('../models/UE');
const EC = require('../models/EC');

// --- GESTION DES DIPLOMES ---
exports.creerDiplome = async (req, res) => {
    try {
        const { code, libelle } = req.body;
        const diplome = await Diplome.create({ code, libelle });
        res.status(201).json({ message: "Diplôme créé avec succès", diplome });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};

exports.listerDiplomes = async (req, res) => {
    try {
        const diplomes = await Diplome.findAll();
        res.json(diplomes);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};

// --- GESTION DES NIVEAUX ---
exports.creerNiveau = async (req, res) => {
    try {
        const { code, libelle, diplomeId } = req.body;
        const niveau = await Niveau.create({ code, libelle, diplomeId });
        res.status(201).json({ message: "Niveau créé avec succès", niveau });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};

exports.listerNiveaux = async (req, res) => {
    try {
        const niveaux = await Niveau.findAll({ include: Diplome });
        res.json(niveaux);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};

// --- GESTION DES SEMESTRES ---
exports.creerSemestre = async (req, res) => {
    try {
        const { code, libelle, niveauId } = req.body;
        const semestre = await Semestre.create({ code, libelle, niveauId });
        res.status(201).json({ message: "Semestre créé avec succès", semestre });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};

exports.listerSemestres = async (req, res) => {
    try {
        const semestres = await Semestre.findAll({ include: Niveau });
        res.json(semestres);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};

// --- GESTION DES UE (Unités d'Enseignement) ---
exports.creerUE = async (req, res) => {
    try {
        const { code, libelle, credits, semestreId } = req.body;
        const ue = await UE.create({ code, libelle, credits, semestreId });
        res.status(201).json({ message: "UE créée avec succès", ue });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};

exports.listerUEs = async (req, res) => {
    try {
        const ues = await UE.findAll({ include: Semestre });
        res.json(ues);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};

// --- GESTION DES EC (Éléments Constitutifs) ---
exports.creerEC = async (req, res) => {
    try {
        const { code_ec, libelle, coefficient, poids_cc, poids_tp, poids_examen, ueId } = req.body;
        const ec = await EC.create({ code_ec, libelle, coefficient, poids_cc, poids_tp, poids_examen, ueId });
        res.status(201).json({ message: "EC créé avec succès", ec });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};

exports.listerECs = async (req, res) => {
    try {
        const ecs = await EC.findAll({ include: UE });
        res.json(ecs);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};