// Données d'exemple (fictives) pour les écrans statiques. À remplacer par les appels à l'API.
export const ETUDIANTS = [
  {
    "id": 1,
    "matricule": "23TLC101C",
    "nom": "Abena Marie",
    "email": "marie.a@estlc.test",
    "telephone": "690000001",
    "filiere": "GSI",
    "niveau": "GSI4",
    "semestre": "GSI-S7",
    "groupe": "GSI4-A",
    "statut": "Actif",
    "anneeEntree": 2023
  },
  {
    "id": 2,
    "matricule": "23TLC108L",
    "nom": "Biyong Samuel",
    "email": "samuel.b@estlc.test",
    "telephone": "690000002",
    "filiere": "GSI",
    "niveau": "GSI4",
    "semestre": "GSI-S7",
    "groupe": "GSI4-A",
    "statut": "Actif",
    "anneeEntree": 2023
  },
  {
    "id": 3,
    "matricule": "23TLC115S",
    "nom": "Chindjang Aïcha",
    "email": "aicha.c@estlc.test",
    "telephone": "690000003",
    "filiere": "GSI",
    "niveau": "GSI4",
    "semestre": "GSI-S7",
    "groupe": "GSI4-A",
    "statut": "En dette",
    "anneeEntree": 2023
  },
  {
    "id": 4,
    "matricule": "23TLC122Z",
    "nom": "Dongmo Patrick",
    "email": "patrick.d@estlc.test",
    "telephone": "690000004",
    "filiere": "GSI",
    "niveau": "GSI4",
    "semestre": "GSI-S7",
    "groupe": "GSI4-A",
    "statut": "Actif",
    "anneeEntree": 2023
  },
  {
    "id": 5,
    "matricule": "23TLC129E",
    "nom": "Eyenga Ruth",
    "email": "ruth.e@estlc.test",
    "telephone": "690000005",
    "filiere": "GSI",
    "niveau": "GSI4",
    "semestre": "GSI-S7",
    "groupe": "GSI4-A",
    "statut": "Suspendu",
    "anneeEntree": 2023
  },
  {
    "id": 6,
    "matricule": "23TLC136N",
    "nom": "Fouda Kevin",
    "email": "kevin.f@estlc.test",
    "telephone": "690000006",
    "filiere": "GSI",
    "niveau": "GSI4",
    "semestre": "GSI-S7",
    "groupe": "GSI4-A",
    "statut": "Actif",
    "anneeEntree": 2023
  },
  {
    "id": 7,
    "matricule": "24TLC143U",
    "nom": "Gwet Léa",
    "email": "lea.g@estlc.test",
    "telephone": "690000007",
    "filiere": "GSI",
    "niveau": "GSI3",
    "semestre": "GSI-S5",
    "groupe": "GSI3-A",
    "statut": "Actif",
    "anneeEntree": 2024
  },
  {
    "id": 8,
    "matricule": "24TLC150B",
    "nom": "Hiol Brice",
    "email": "brice.h@estlc.test",
    "telephone": "690000008",
    "filiere": "GSI",
    "niveau": "GSI3",
    "semestre": "GSI-S5",
    "groupe": "GSI3-A",
    "statut": "En dette",
    "anneeEntree": 2024
  },
  {
    "id": 9,
    "matricule": "24TLC157K",
    "nom": "Ikome Sandrine",
    "email": "sandrine.i@estlc.test",
    "telephone": "690000009",
    "filiere": "GSI",
    "niveau": "GSI3",
    "semestre": "GSI-S5",
    "groupe": "GSI3-A",
    "statut": "Actif",
    "anneeEntree": 2024
  },
  {
    "id": 10,
    "matricule": "24TLC164R",
    "nom": "Jiogue Arnaud",
    "email": "arnaud.j@estlc.test",
    "telephone": "690000010",
    "filiere": "GSI",
    "niveau": "GSI3",
    "semestre": "GSI-S5",
    "groupe": "GSI3-A",
    "statut": "Actif",
    "anneeEntree": 2024
  }
]

export const ENSEIGNANTS = [
  {
    "id": 1,
    "code": "ENS001",
    "nom": "Nkomo Paul",
    "email": "p.nkomo@estlc.test",
    "telephone": "671000001",
    "ec": [
      "GSI721",
      "GSI722",
      "GSI723",
      "GSI724"
    ]
  },
  {
    "id": 2,
    "code": "ENS002",
    "nom": "Mballa Claire",
    "email": "c.mballa@estlc.test",
    "telephone": "671000002",
    "ec": [
      "GSI731",
      "GSI732",
      "GSI733",
      "GSI734"
    ]
  },
  {
    "id": 3,
    "code": "ENS003",
    "nom": "Essomba Jean",
    "email": "j.essomba@estlc.test",
    "telephone": "671000003",
    "ec": [
      "GSI711",
      "GSI712",
      "GSI713",
      "GSI714",
      "GSI522",
      "GSI524"
    ]
  }
]

export const EC_INTITULES = {
  "GSI721": "Complexité et structure de données avancée",
  "GSI722": "Calculabilité",
  "GSI723": "Vérification et validation des logiciels",
  "GSI724": "UML Design Patterns, web sémantique",
  "GSI731": "POO et programmation web",
  "GSI732": "Terminaux mobiles et hébergement",
  "GSI733": "Microprocesseur et multi-coeurs",
  "GSI734": "Architectures FPGA, GPU, Arduino",
  "GSI711": "Business plan",
  "GSI712": "Constitution d'une équipe projet",
  "GSI713": "Validation technique d'un projet",
  "GSI714": "Prototypage du produit ou service",
  "GSI522": "Conception et analyse des algorithmes",
  "GSI524": "Langage formel et compilation"
}
