# HSE Synergy - Système de Gestion HSE

![HSE Synergy](https://img.shields.io/badge/Version-1.0.0-blue)
![Java](https://img.shields.io/badge/Java-17-red)
![Spring%20Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-green)
![Angular](https://img.shields.io/badge/Angular-17-darkred)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)

## 📋 Table des Matières
- [Description](#description)
- [Architecture](#architecture)
- [Prérequis](#prérequis)
- [Installation](#installation)
- [Démarrage](#démarrage)
- [Utilisation](#utilisation)
- [Credentials de Test](#credentials-de-test)
- [Structure du Projet](#structure-du-projet)
- [Endpoints API](#endpoints-api)
- [Troubleshooting](#troubleshooting)

---

## 📖 Description

**HSE Synergy** est une plateforme intégrée de gestion Hygiène, Sécurité et Environnement (HSE) basée sur une architecture **microservices**. Elle permet de:

✅ **Gérer les incidents** - Signaler, suivre et résoudre les incidents de sécurité  
✅ **Planifier les audits** - Créer et planifier des audits HSE  
✅ **Plans d'action** - Générer et suivre les plans d'action  
✅ **Non-conformités** - Enregistrer et tracker les non-conformités  
✅ **Dashboard en temps réel** - Vue d'ensemble avec KPIs  

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────┐
│        Angular Frontend (localhost:4200)             │
├─────────────────────────────────────────────────────┤
│           API Gateway (localhost:8084)               │
├─────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌──────────────┐  ┌──────────────┐ │
│  │ Service     │  │ MicroService │  │ MicroService │ │
│  │ Utilisateurs│  │ Audit        │  │ PlanAction   │ │
│  │ (8080)      │  │ (8082)       │  │ (8083)       │ │
│  └─────────────┘  └──────────────┘  └──────────────┘ │
│  ┌──────────────┐  ┌──────────────┐                   │
│  │ Incidents    │  │ Eureka Server│                   │
│  │ (8081)       │  │ (8761)       │                   │
│  └──────────────┘  └──────────────┘                   │
├─────────────────────────────────────────────────────┤
│         MySQL Databases (Docker)                     │
│  • db_utilisateurs (3307)                            │
│  • db_incidents (3308)                               │
│  • db_audits (3311)                                  │
│  • db_planaction (3310)                              │
└─────────────────────────────────────────────────────┘
```

**Composants:**
- **Angular 17** - Frontend responsive avec Tailwind CSS
- **Spring Boot 3.3.4** - Services backend avec Java 17
- **Eureka Server** - Service discovery et registry
- **API Gateway** - Routeur centralisé avec load balancing
- **MySQL 8.0** - 4 bases de données isolées (Docker)
- **JWT** - Authentification sécurisée

---

## 🔧 Prérequis

- **Java 17+** - [Télécharger](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- **Node.js 18+** - [Télécharger](https://nodejs.org/)
- **Maven 3.6+** - Inclus avec le projet (mvnw)
- **Docker** - [Télécharger](https://www.docker.com/)
- **MySQL Client** (optionnel) - [MySQL Workbench](https://www.mysql.com/products/workbench/)

---

## 📥 Installation

### 1️⃣ Cloner le projet
```bash
git clone https://github.com/votre-org/hse-synergy.git
cd services_repo
```

### 2️⃣ Démarrer les bases de données MySQL (Docker)
```bash
docker-compose up -d
```

**Vérifier le statut :**
```bash
docker-compose ps
```

**Accéder à phpMyAdmin :**
- URL: http://localhost:8090
- Username: `root`
- Password: `root123`

### 3️⃣ Démarrer les microservices Java

Dans **4 terminaux séparés**, exécutez:

**Terminal 1 - Eureka Server:**
```bash
cd eureka-server
.\mvnw.cmd spring-boot:run
```

**Terminal 2 - Service Utilisateurs:**
```bash
cd service_utilisateurs
.\mvnw.cmd spring-boot:run
```

**Terminal 3 - Service Incidents:**
```bash
cd incidents
.\mvnw.cmd spring-boot:run
```

**Terminal 4 - Service Audit:**
```bash
cd MicroService_Audit
.\mvnw.cmd spring-boot:run
```

**Terminal 5 - Service Plan d'Action:**
```bash
cd microService_PlanAction
.\mvnw.cmd spring-boot:run
```

**Terminal 6 - API Gateway:**
```bash
cd api-gateway
.\mvnw.cmd spring-boot:run
```

### 4️⃣ Démarrer le frontend Angular

```bash
cd angular_project/my-login-app
npm install
ng serve
```

---

## 🚀 Démarrage Rapide

Une fois tous les services en cours d'exécution:

1. **Accéder à l'application :** http://localhost:4200
2. **Eureka Dashboard :** http://localhost:8761 (voir tous les services enregistrés)
3. **phpMyAdmin :** http://localhost:8090 (gérer les bases de données)

---

## 👥 Utilisation

### 1. Connexion
```
Email: admin@hse.com
Mot de passe: admin123
```

### 2. Navigation
- **Dashboard** - Vue d'ensemble avec KPIs
- **Incidents** - Créer/Modifier/Supprimer des incidents
- **Audits** - Gérer les audits planifiés
- **Plans d'Action** - Suivre les plans d'action
- **Non-Conformités** - Enregistrer les non-conformités
- **Profil** - Voir les informations utilisateur

### 3. Créer un Incident
1. Aller à **Incidents** → **+ Nouveau Incident**
2. Remplir le formulaire (Date, Type, Lieu, Description)
3. Cliquer **Créer**
4. L'incident apparaît immédiatement dans la liste

### 4. Filtrer les Incidents
- Filtrer par **Type** (CHUTE, FEU, FUITE_CHIMIQUE, etc.)
- Filtrer par **Statut** (OUVERT, EN_COURS, RESOLU, FERME)

---

## 🔐 Credentials de Test

### Base de Données MySQL
```
Host: localhost
Ports:
  - db_utilisateurs: 3307
  - db_incidents: 3308
  - db_audits: 3311
  - db_planaction: 3310

Username: hse_user
Password: HseSecure2026!

phpMyAdmin:
  URL: http://localhost:8090
  Username: root
  Password: root123
```

### Application HSE
```
Email: admin@hse.com
Password: admin123

Rôle: ROLE_ADMIN
```

---

## 📁 Structure du Projet

```
services_repo/
├── angular_project/
│   └── my-login-app/          # Frontend Angular 17
│       ├── src/app/
│       │   ├── dashboard/      # Tableau de bord
│       │   ├── incidents/      # Gestion incidents
│       │   ├── login/          # Page de connexion
│       │   ├── core/           # Services, modèles, guards
│       │   └── ...
│       └── package.json
│
├── service_utilisateurs/       # Service Authentification (8080)
│   ├── src/main/java/
│   │   ├── controller/         # AuthController, UserController
│   │   ├── service/            # AuthService, UserService
│   │   ├── security/           # JWT, SecurityConfig
│   │   └── model/              # Utilisateur, Role
│   └── pom.xml
│
├── incidents/                  # Service Incidents (8081)
│   ├── src/main/java/
│   │   ├── controller/         # IncidentController
│   │   ├── service/            # IncidentService
│   │   └── model/              # Incident, TypeIncident
│   └── pom.xml
│
├── MicroService_Audit/         # Service Audit (8082)
│   └── ...
│
├── microService_PlanAction/    # Service Plans d'Action (8083)
│   └── ...
│
├── api-gateway/                # API Gateway (8084)
│   └── application.properties  # Routes et CORS
│
├── eureka-server/              # Eureka Server (8761)
│   └── ...
│
├── docker-compose.yml          # Configuration Docker MySQL
└── README.md                   # Ce fichier
```

---

## 🔌 Endpoints API

### Authentification
```
POST   /api/auth/login          # Connexion (retourne JWT + user info)
POST   /api/auth/register       # Inscription
```

### Incidents
```
GET    /api/incidents           # Lister tous les incidents
POST   /api/incidents           # Créer un incident
GET    /api/incidents/{id}      # Détails d'un incident
PUT    /api/incidents/{id}      # Modifier un incident
DELETE /api/incidents/{id}      # Supprimer un incident
```

### Audits
```
GET    /api/audits              # Lister les audits
GET    /api/audits/mes-audits   # Mes audits (user)
POST   /api/audits              # Créer un audit
```

### Plans d'Action
```
GET    /api/actions             # Lister les actions
GET    /api/actions/responsable/{id}  # Actions assignées à l'user
POST   /api/actions             # Créer une action
```

**Note:** Tous les endpoints (sauf `/api/auth/**`) nécessitent une authentification JWT.

---

## 🛠️ Configuration

### JWT Configuration
**Fichier :** `application.properties` dans chaque service

```properties
application.security.jwt.secret-key=bXlzZWNyZXRrZXlmb3Jqd3RteXNlY3JldGtleWZvcmp3dA==
application.security.jwt.expiration=86400000  # 24h
```

### CORS Configuration
**Fichier :** `api-gateway/src/main/resources/application.properties`

```properties
spring.cloud.gateway.globalcors.corsConfigurations.[/**].allowedOrigins=http://localhost:4200
spring.cloud.gateway.globalcors.corsConfigurations.[/**].allowedMethods=GET,POST,PUT,DELETE,OPTIONS
spring.cloud.gateway.globalcors.corsConfigurations.[/**].allowCredentials=true
```

### Database Configuration
**Fichier :** `service_utilisateurs/src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3307/db_utilisateurs?useSSL=false
spring.datasource.username=hse_user
spring.datasource.password=HseSecure2026!
spring.jpa.hibernate.ddl-auto=update
```

---
# Vérifier que Docker est lancé
docker-compose ps

# Redémarrer les containers
docker-compose down
docker-compose up -d
```


### Eureka Dashboard
```
http://localhost:8761
```

Affiche:
- ✅ Services enregistrés (statut UP/DOWN)
- 🔗 Adresse et port de chaque service
- ❤️ Heartbeat status

### phpMyAdmin
```
http://localhost:8090
```

Accédez aux 4 bases MySQL et exécutez des requêtes.

---

## 🧪 Tests

### Test de Connexion
```bash
curl -X POST http://localhost:8084/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin@hse.com","password":"admin123"}'
```

### Test de Récupération des Incidents
```bash
curl -X GET http://localhost:8084/api/incidents \
  -H "Authorization: Bearer <YOUR_JWT_TOKEN>"
```

---