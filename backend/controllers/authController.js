const User = require('../models/User');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');

// Inscription (Utile pour initialiser les utilisateurs)
exports.register = async (req, res) => {
    try {
        const { identifiant, password, role, nom, prenom, email } = req.body;

        // Vérifier si l'utilisateur existe déjà
        const existingUser = await User.findOne({ where: { identifiant } });
        if (existingUser) {
            return res.status(400).json({ error: "Cet identifiant est déjà utilisé." });
        }

        // Hasher le mot de passe
        const salt = await bcrypt.genSalt(10);
        const hashedPassword = await bcrypt.hash(password, salt);

        // Créer l'utilisateur
        const newUser = await User.create({
            identifiant,
            password: hashedPassword,
            role,
            nom,
            prenom,
            email
        });

        res.status(201).json({
            message: "Utilisateur créé avec succès",
            user: {
                id: newUser.id,
                identifiant: newUser.identifiant,
                role: newUser.role,
                nom: newUser.nom
            }
        });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};

// Connexion (Login)
exports.login = async (req, res) => {
    try {
        const { identifiant, password } = req.body;

        // Trouver l'utilisateur par son identifiant
        const user = await User.findOne({ where: { identifiant } });
        if (!user) {
            return res.status(404).json({ error: "Utilisateur non trouvé." });
        }

        // Vérifier le mot de passe
        const isMatch = await bcrypt.compare(password, user.password);
        if (!isMatch) {
            return res.status(401).json({ error: "Mot de passe incorrect." });
        }

        // Générer un token JWT (valable 24h)
        const token = jwt.sign(
            { id: user.id, identifiant: user.identifiant, role: user.role },
            process.env.JWT_SECRET || 'estlc_secret_key_2026',
            { expiresIn: '24h' }
        );

        res.json({
            message: "Connexion réussie",
            token,
            user: {
                id: user.id,
                identifiant: user.identifiant,
                role: user.role,
                nom: user.nom,
                prenom: user.prenom
            }
        });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};