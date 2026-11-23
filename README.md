# Portail académique ESTLC

Application de gestion de notes pour l'École Supérieure de Transport, de Logistique et de Commerce (UEb – Ambam).
Trois espaces : étudiant, enseignant, administration.

## Structure
- `backend/` : API Spring Boot 4 (Java 17), base MariaDB/MySQL, authentification JWT
- `frontend/` : interface React (Vite) aux couleurs de l'ESTLC

## Lancer le projet
Prérequis : Java 17, Node.js 18+, MariaDB ou MySQL avec une base `student_db`.

Backend (http://localhost:8080) :
    cd backend
    DB_USER=sms DB_PASSWORD=votre_mot_de_passe ./mvnw spring-boot:run

Frontend (http://localhost:5173) :
    cd frontend
    npm install
    npm run dev

## Premier compte
Au premier lancement, si aucun super admin n'existe, la première connexion sur l'onglet Administration crée le compte avec les identifiants saisis (comportement à supprimer avant tout déploiement).

## Crédits
Le backend est basé sur le dépôt https://github.com/TheIbrahim-System/Student-Management-System (aucune licence déclarée par l'auteur). Le frontend a été réalisé pour ce projet.

## Équipe
Projet GSI712 – [noms des membres]
