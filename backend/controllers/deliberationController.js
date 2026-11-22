const UE = require('../models/UE');
const EC = require('../models/EC');
const Note = require('../models/Note');
const Semestre = require('../models/Semestre');
const User = require('../models/User');

function calculerMoyenneUE(ue, ecsList, notesEtudiant) {
  const ecs = ecsList || [];
  let sommePonderee = 0;
  let sommeCoefficients = 0;
  let notesTrouvees = 0;

  const detailsEC = ecs.map((ec) => {
    const notesEc = notesEtudiant.filter((n) => n.ecId === ec.id);
    const noteChoisie =
      notesEc.find((n) => n.session === 'Normale') ||
      notesEc.find((n) => n.session === 'Rattrapage') ||
      notesEc[0] ||
      null;

    const noteFinale =
      noteChoisie && noteChoisie.note_finale !== null && noteChoisie.note_finale !== undefined
        ? Number(noteChoisie.note_finale)
        : null;

    const coeff = Number(ec.coefficient) || 1;

    if (noteFinale !== null) {
      sommePonderee += noteFinale * coeff;
      sommeCoefficients += coeff;
      notesTrouvees += 1;
    }

    return {
      ecId: ec.id,
      code_ec: ec.code_ec,
      libelle: ec.libelle,
      coefficient: coeff,
      note_cc: noteChoisie ? noteChoisie.note_cc : null,
      note_tp: noteChoisie ? noteChoisie.note_tp : null,
      note_examen: noteChoisie ? noteChoisie.note_examen : null,
      note_finale: noteFinale,
      session: noteChoisie ? noteChoisie.session : null
    };
  });

  if (notesTrouvees === 0 || sommeCoefficients === 0) {
    return {
      ueId: ue.id,
      code: ue.code,
      libelle: ue.libelle,
      credits: ue.credits,
      credits_valides: 0,
      moyenne_ue: null,
      validee: false,
      statut: 'En attente',
      ecs: detailsEC
    };
  }

  const moyenneUE = Number((sommePonderee / sommeCoefficients).toFixed(2));
  const validee = moyenneUE >= 10;

  return {
    ueId: ue.id,
    code: ue.code,
    libelle: ue.libelle,
    credits: ue.credits,
    credits_valides: validee ? ue.credits : 0,
    moyenne_ue: moyenneUE,
    validee,
    statut: validee ? 'Validée' : 'Non validée',
    ecs: detailsEC
  };
}

exports.getMoyenneUE = async (req, res) => {
  try {
    const { etudiantId, ueId } = req.params;

    const etudiant = await User.findByPk(etudiantId, {
      attributes: ['id', 'identifiant', 'nom', 'prenom', 'role']
    });
    if (!etudiant) {
      return res.status(404).json({ error: 'Étudiant introuvable.' });
    }

    const ue = await UE.findByPk(ueId, {
      include: [{ model: Semestre, attributes: ['id', 'code', 'libelle'] }]
    });
    if (!ue) {
      return res.status(404).json({ error: 'UE introuvable.' });
    }

    const ecsList = await EC.findAll({ where: { ueId: ue.id } });
    const ecIds = ecsList.map((e) => e.id);

    const notesEtudiant = ecIds.length > 0
      ? await Note.findAll({ where: { etudiantId, ecId: ecIds } })
      : [];

    const resultatUE = calculerMoyenneUE(ue, ecsList, notesEtudiant);

    return res.json({
      etudiant,
      semestre: ue.Semestre || null,
      ue: resultatUE
    });
  } catch (error) {
    return res.status(500).json({ error: error.message });
  }
};

module.exports.calculerMoyenneUE = calculerMoyenneUE;

exports.getMoyenneSemestre = async (req, res) => {
  try {
    const { etudiantId, semestreId } = req.params;

    const etudiant = await User.findByPk(etudiantId, {
      attributes: ['id', 'identifiant', 'nom', 'prenom', 'role']
    });
    if (!etudiant) {
      return res.status(404).json({ error: 'Étudiant introuvable.' });
    }

    const semestre = await Semestre.findByPk(semestreId);
    if (!semestre) {
      return res.status(404).json({ error: 'Semestre introuvable.' });
    }

    const ues = await UE.findAll({ where: { semestreId } });
    const ueIds = ues.map((u) => u.id);

    const ecsList = ueIds.length > 0
      ? await EC.findAll({ where: { ueId: ueIds } })
      : [];
    const ecIds = ecsList.map((e) => e.id);

    const notesEtudiant = ecIds.length > 0
      ? await Note.findAll({ where: { etudiantId, ecId: ecIds } })
      : [];

    let sommePondereeUE = 0;
    let sommeCreditsUE = 0;
    let creditsTotalSemestre = 0;
    let creditsCapitalises = 0;

    const detailsUE = ues.map((ue) => {
      const ecsDeLUE = ecsList.filter((e) => e.ueId === ue.id);
      const resUE = calculerMoyenneUE(ue, ecsDeLUE, notesEtudiant);

      const creditsUE = Number(ue.credits) || 0;
      creditsTotalSemestre += creditsUE;
      creditsCapitalises += Number(resUE.credits_valides) || 0;

      if (resUE.moyenne_ue !== null && creditsUE > 0) {
        sommePondereeUE += resUE.moyenne_ue * creditsUE;
        sommeCreditsUE += creditsUE;
      }

      return resUE;
    });

    const moyenneSemestre =
      sommeCreditsUE > 0 ? Number((sommePondereeUE / sommeCreditsUE).toFixed(2)) : null;

    let decision = 'En attente';
    let valide = false;
    if (moyenneSemestre !== null) {
      if (moyenneSemestre >= 10) {
        decision = 'Admis';
        valide = true;
      } else if (moyenneSemestre >= 8) {
        decision = 'Ajourné (Compensation possible)';
      } else {
        decision = 'Ajourné';
      }
    }

    return res.json({
      etudiant,
      semestre: {
        id: semestre.id,
        code: semestre.code,
        libelle: semestre.libelle
      },
      bilan: {
        moyenne_semestre: moyenneSemestre,
        credits_total: creditsTotalSemestre,
        credits_capitalises: creditsCapitalises,
        valide,
        decision
      },
      ues: detailsUE
    });
  } catch (error) {
    return res.status(500).json({ error: error.message });
  }
};

exports.getReleveEtudiant = async (req, res) => {
  try {
    const { etudiantId } = req.params;

    const etudiant = await User.findByPk(etudiantId, {
      attributes: ['id', 'identifiant', 'nom', 'prenom', 'role']
    });
    if (!etudiant) {
      return res.status(404).json({ error: 'Étudiant introuvable.' });
    }

    const semestres = await Semestre.findAll({ order: [['id', 'ASC']] });
    const ues = await UE.findAll({ order: [['id', 'ASC']] });
    const ecsList = await EC.findAll({ order: [['id', 'ASC']] });
    const notesEtudiant = await Note.findAll({ where: { etudiantId } });

    let totalCreditsGeneral = 0;
    let totalCreditsCapitalisesGeneral = 0;
    let sommePondereeGenerale = 0;
    let sommeCreditsEvaluesGeneral = 0;

    const releveSemestres = semestres.map((sem) => {
      const uesDuSemestre = ues.filter((u) => u.semestreId === sem.id);

      let sommePondereeUE = 0;
      let sommeCreditsUE = 0;
      let creditsTotalSemestre = 0;
      let creditsCapitalises = 0;

      const detailsUE = uesDuSemestre.map((ue) => {
        const ecsDeLUE = ecsList.filter((e) => e.ueId === ue.id);
        const resUE = calculerMoyenneUE(ue, ecsDeLUE, notesEtudiant);

        const creditsUE = Number(ue.credits) || 0;
        creditsTotalSemestre += creditsUE;
        creditsCapitalises += Number(resUE.credits_valides) || 0;

        if (resUE.moyenne_ue !== null && creditsUE > 0) {
          sommePondereeUE += resUE.moyenne_ue * creditsUE;
          sommeCreditsUE += creditsUE;
        }

        return resUE;
      });

      const moyenneSemestre =
        sommeCreditsUE > 0 ? Number((sommePondereeUE / sommeCreditsUE).toFixed(2)) : null;

      totalCreditsGeneral += creditsTotalSemestre;
      totalCreditsCapitalisesGeneral += creditsCapitalises;

      if (moyenneSemestre !== null && creditsTotalSemestre > 0) {
        sommePondereeGenerale += moyenneSemestre * creditsTotalSemestre;
        sommeCreditsEvaluesGeneral += creditsTotalSemestre;
      }

      let decision = 'En attente';
      let valide = false;
      if (moyenneSemestre !== null) {
        if (moyenneSemestre >= 10) {
          decision = 'Admis';
          valide = true;
        } else if (moyenneSemestre >= 8) {
          decision = 'Ajourné (Compensation possible)';
        } else {
          decision = 'Ajourné';
        }
      }

      return {
        semestre: {
          id: sem.id,
          code: sem.code,
          libelle: sem.libelle
        },
        bilan: {
          moyenne_semestre: moyenneSemestre,
          credits_total: creditsTotalSemestre,
          credits_capitalises: creditsCapitalises,
          valide,
          decision
        },
        ues: detailsUE
      };
    });

    const moyenneGenerale =
      sommeCreditsEvaluesGeneral > 0
        ? Number((sommePondereeGenerale / sommeCreditsEvaluesGeneral).toFixed(2))
        : null;

    return res.json({
      etudiant,
      synthese_globale: {
        moyenne_generale: moyenneGenerale,
        credits_total: totalCreditsGeneral,
        credits_capitalises: totalCreditsCapitalisesGeneral
      },
      semestres: releveSemestres
    });
  } catch (error) {
    return res.status(500).json({ error: error.message });
  }
};
