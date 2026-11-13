const express = require('express');
const router = express.Router();
const noteController = require('../controllers/noteController');
const { verifyToken, verifyRole } = require('../middlewares/authMiddleware');

// Route de saisie des notes (Accessible par Admin et Enseignant)
router.post('/saisir', verifyToken, verifyRole(['Administrateur', 'Enseignant']), noteController.saisirNote);

// Route pour consulter les notes d'un étudiant
router.get('/etudiant/:etudiantId', verifyToken, noteController.getNotesByEtudiant);

module.exports = router;