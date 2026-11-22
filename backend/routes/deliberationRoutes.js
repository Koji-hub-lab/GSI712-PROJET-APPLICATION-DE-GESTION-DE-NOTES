const express = require('express');
const router = express.Router();
const deliberationController = require('../controllers/deliberationController');
const { verifyToken } = require('../middlewares/authMiddleware');

router.get('/etudiant/:etudiantId/ue/:ueId', verifyToken, deliberationController.getMoyenneUE);
router.get('/etudiant/:etudiantId/semestre/:semestreId', verifyToken, deliberationController.getMoyenneSemestre);
router.get('/etudiant/:etudiantId/releve', verifyToken, deliberationController.getReleveEtudiant);

module.exports = router;
