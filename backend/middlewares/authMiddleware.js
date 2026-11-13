const jwt = require('jsonwebtoken');

exports.verifyToken = (req, res, next) => {
    const authHeader = req.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1]; // Format: Bearer TOKEN

    if (!token) {
        return res.status(403).json({ error: "Accès refusé. Aucun token fourni." });
    }

    try {
        const verified = jwt.verify(token, process.env.JWT_SECRET || 'estlc_secret_key_2026');
        req.user = verified;
        next();
    } catch (error) {
        res.status(401).json({ error: "Token invalide ou expiré." });
    }
};

// Middleware optionnel pour restreindre par rôle (ex: 'Administrateur')
exports.verifyRole = (rolesAutorises) => {
    return (req, res, next) => {
        if (!req.user || !rolesAutorises.includes(req.user.role)) {
            return res.status(403).json({ error: "Accès non autorisé pour votre profil." });
        }
        next();
    };
};