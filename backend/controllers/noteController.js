const Note = require('../models/Note');
const EC = require('../models/EC');
const User = require('../models/User');

// Saisir ou Mettre à jour une note (réservé aux enseignants / admins)
exports.saisirNote = async (req, res) => {
    try {
        const { etudiantId, ecId, note_cc, note_tp, note_examen, session } = req.body;

        // 1. Vérifier si l'étudiant existe et a bien le rôle 'Étudiant'
        const etudiant = await User.findOne({ where: { id: etudiantId, role: 'Étudiant' } });
        if (!etudiant) {
            return res.status(404).json({ error: "Étudiant non trouvé ou profil invalide." });
        }

        // 2. Vérifier si l'EC existe (et récupérer ses coefficients/poids si configurés, sinon utiliser des valeurs par défaut ex: CC 30%, TP 20%, Examen 50%)
        const ec = await EC.findByPk(ecId);
        if (!ec) {
            return res.status(404).json({ error: "Élément Constitutif (EC) non trouvé." });
        }

        // 3. Calcul automatique de la note finale (Exemple de pondération standard : CC 30%, TP 20%, Examen 50%)
        // Tu pourras adapter ces coefficients selon les attributs de ton modèle EC si tu en as défini.
        const cc = note_cc !== undefined ? parseFloat(note_cc) : 0;
        const tp = note_tp !== undefined ? parseFloat(note_tp) : 0;
        const examen = note_examen !== undefined ? parseFloat(note_examen) : 0;

        // Formule de calcul (modifiable selon ton règlement pédagogique)
        const note_finale = (cc * 0.3) + (tp * 0.2) + (examen * 0.5);

        // 4. Chercher si une note existe déjà pour cet étudiant, cet EC et cette session
        let [noteRecord, created] = await Note.findOrCreate({
            where: { etudiantId, ecId, session: session || 'Normale' },
            defaults: {
                note_cc: cc,
                note_tp: tp,
                note_examen: examen,
                note_finale: Math.round(note_finale * 100) / 100 // Arrondi à 2 chiffres
            }
        });

        // Si elle existait déjà, on la met à jour
        if (!created) {
            noteRecord.note_cc = cc;
            noteRecord.note_tp = tp;
            noteRecord.note_examen = examen;
            noteRecord.note_finale = Math.round(note_finale * 100) / 100;
            await noteRecord.save();
        }

        res.status(200).json({
            message: created ? "Note enregistrée avec succès" : "Note mise à jour avec succès",
            note: noteRecord
        });

    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};

// Consulter les notes d'un étudiant
exports.getNotesByEtudiant = async (req, res) => {
    try {
        const { etudiantId } = req.params;
        const notes = await Note.findAll({
            where: { etudiantId },
            include: [{ model: EC, attributes: ['code_ec', 'libelle', 'coefficient'] }]
        });
        res.json(notes);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};