const express = require('express');
const router = express.Router();
const adminController = require('../controllers/adminController');

// Routes Diplômes
router.post('/diplomes', adminController.creerDiplome);
router.get('/diplomes', adminController.listerDiplomes);

// Routes Niveaux
router.post('/niveaux', adminController.creerNiveau);
router.get('/niveaux', adminController.listerNiveaux);

// Routes Semestres
router.post('/semestres', adminController.creerSemestre);
router.get('/semestres', adminController.listerSemestres);

// Routes UE
router.post('/ues', adminController.creerUE);
router.get('/ues', adminController.listerUEs);

// Routes EC
router.post('/ecs', adminController.creerEC);
router.get('/ecs', adminController.listerECs);

module.exports = router;