# ChargBook ⚡ — Gestion de Bornes de Recharge pour Véhicules Électriques

Application full stack de gestion des utilisateurs, véhicules, bornes de recharge et réservations, développée lors d'un stage chez Wafa Assurance.

## 🎯 Fonctionnalités

- Gestion des utilisateurs et de leurs véhicules électriques
- Consultation de la disponibilité des bornes de recharge en temps réel
- Réservation, modification et annulation de créneaux de recharge
- Gestion des réclamations utilisateurs
- Espace d'administration pour le suivi et la supervision du parc de bornes

## 🏗️ Architecture

Le projet suit une architecture **microservices** :

- **Back-end** — Java / Spring Boot, exposant une API REST documentée avec OpenAPI
- **Front-end** (`chargebook-frontend`) — React
- **Base de données** — PostgreSQL
- **Conteneurisation** — Docker

## 🛠️ Stack technique

| Couche         | Technologies                     |
|----------------|-----------------------------------|
| Back-end       | Java, Spring Boot, Spring Data JPA |
| Front-end      | React, JavaScript, HTML/CSS       |
| Base de données| PostgreSQL                        |
| API            | REST, OpenAPI / Swagger           |
| Infrastructure | Docker                            |

## 🚀 Lancer le projet en local

### Prérequis
- Java 17+ et Maven
- Node.js et npm
- PostgreSQL (ou Docker)

### Back-end
```bash
./mvnw spring-boot:run
```
Le serveur démarre par défaut sur `http://localhost:8080`.

### Front-end
```bash
cd chargebook-frontend
npm install
npm start
```
L'application est accessible sur `http://localhost:3000`.

## 📄 Documentation API

La documentation de l'API REST est générée via OpenAPI et accessible une fois le back-end lancé (`/swagger-ui.html` ou équivalent selon la configuration).

## 👩‍💻 Auteure

Développé par **Rabab EL GHRIB** dans le cadre d'un stage PFA chez Wafa Assurance (2026).
