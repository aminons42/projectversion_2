# 🗄️ Schéma de Base de Données MySQL - Projet HSE Microservices

## 📊 Vue d'Ensemble

Le projet utilise **4 bases de données MySQL distinctes** pour chaque microservice :

1. **db_utilisateurs** - Service Utilisateurs
2. **db_incidents** - Service Incidents  
3. **db_audits** - Service Audits
4. **db_planaction** - Service Plans d'Action

---

## 🔧 Configuration MySQL Requise

### Prérequis
- MySQL 8.0+ (ou MariaDB 10.6+)
- Port par défaut : 3306
- Encodage : UTF-8 (utf8mb4)

### Création des Bases de Données

```sql
-- Créer les 4 bases de données
CREATE DATABASE IF NOT EXISTS db_utilisateurs CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS db_incidents CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS db_audits CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS db_planaction CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Créer un utilisateur dédié pour chaque service (recommandé)
CREATE USER IF NOT EXISTS 'hse_user'@'localhost' IDENTIFIED BY 'HseSecure2026!';
GRANT ALL PRIVILEGES ON db_utilisateurs.* TO 'hse_user'@'localhost';
GRANT ALL PRIVILEGES ON db_incidents.* TO 'hse_user'@'localhost';
GRANT ALL PRIVILEGES ON db_audits.* TO 'hse_user'@'localhost';
GRANT ALL PRIVILEGES ON db_planaction.* TO 'hse_user'@'localhost';
FLUSH PRIVILEGES;
```

---

## 📦 BASE 1 : db_utilisateurs (Service Utilisateurs)

### Tables

#### 1. `utilisateur`
```sql
CREATE TABLE utilisateur (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,  -- BCrypt hash
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### 2. `role`
```sql
CREATE TABLE role (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50) NOT NULL UNIQUE,
    INDEX idx_nom (nom)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### 3. `user_roles` (Table de jointure Many-to-Many)
```sql
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES utilisateur(user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role(role_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### Données Initiales Recommandées

```sql
USE db_utilisateurs;

-- Insérer les rôles de base
INSERT INTO role (nom) VALUES 
    ('ROLE_ADMIN'),
    ('ROLE_MANAGER'),
    ('ROLE_AUDITEUR'),
    ('ROLE_USER');

-- Créer un administrateur par défaut (password: admin123)
INSERT INTO utilisateur (nom, prenom, username, password) VALUES 
    ('Admin', 'Système', 'admin@hse.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- Assigner le rôle ADMIN
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);
```

### Statistiques Utilisées
- **Nombre d'utilisateurs actifs** : `SELECT COUNT(*) FROM utilisateur`

---

## 📦 BASE 2 : db_incidents (Service Incidents)

### Tables

#### 1. `incidents`
```sql
CREATE TABLE incidents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATETIME NOT NULL,
    description TEXT,
    location VARCHAR(200),
    creator_name VARCHAR(100) NOT NULL,
    statut VARCHAR(50) NOT NULL,
    type_incident VARCHAR(50) NOT NULL,  -- ENUM: ACCIDENT_TRAVAIL, PRESQU_ACCIDENT, INCIDENT_ENVIRONNEMENTAL, etc.
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_statut (statut),
    INDEX idx_type (type_incident),
    INDEX idx_date (date),
    INDEX idx_creator (creator_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### Valeurs ENUM pour `type_incident`
```
- ACCIDENT_TRAVAIL
- PRESQU_ACCIDENT
- INCIDENT_ENVIRONNEMENTAL
- INCIDENT_SECURITE
- VIOLATION_PROCEDURE
- AUTRE
```

### Valeurs ENUM pour `statut`
```
- OUVERT
- EN_COURS
- RESOLU
- CLOS
```

### Statistiques Utilisées
- **Incidents critiques** : `SELECT COUNT(*) FROM incidents WHERE type_incident = 'ACCIDENT_TRAVAIL'`

### Exemple de Données
```sql
USE db_incidents;

INSERT INTO incidents (date, description, location, creator_name, statut, type_incident) VALUES 
    (NOW(), 'Chute de plain-pied dans l\'entrepôt', 'Entrepôt A - Zone 3', 'Jean Dupont', 'OUVERT', 'ACCIDENT_TRAVAIL'),
    (NOW(), 'Fuite d\'eau détectée', 'Bureau B12', 'Marie Martin', 'EN_COURS', 'INCIDENT_ENVIRONNEMENTAL'),
    (NOW(), 'Équipement de protection défectueux', 'Atelier C', 'Pierre Durand', 'RESOLU', 'PRESQU_ACCIDENT');
```

---

## 📦 BASE 3 : db_audits (Service Audits)

### Tables

#### 1. `audits`
```sql
CREATE TABLE audits (
    audit_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(200) NOT NULL,
    description TEXT,
    type_audit VARCHAR(50) NOT NULL,  -- ENUM: INTERNE, EXTERNE, REGLEMENTAIRE, etc.
    date_debut DATETIME,
    date_fin DATETIME,
    date_planifiee DATETIME,
    statut VARCHAR(50) NOT NULL DEFAULT 'PLANIFIE',  -- ENUM: PLANIFIE, EN_COURS, TERMINE
    auditeur_id BIGINT NOT NULL,
    departement VARCHAR(100),
    zone VARCHAR(100),
    score_global DOUBLE,
    taux_conformite DOUBLE,
    observations TEXT,
    recommandations TEXT,
    recommendations TEXT,  -- Colonne dupliquée dans le code
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    checklist_template_id BIGINT,
    INDEX idx_statut (statut),
    INDEX idx_type (type_audit),
    INDEX idx_auditeur (auditeur_id),
    INDEX idx_date_planifiee (date_planifiee),
    FOREIGN KEY (checklist_template_id) REFERENCES check_list(template_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### 2. `check_list`
```sql
CREATE TABLE check_list (
    template_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(200) NOT NULL,
    description TEXT,
    type_audit VARCHAR(50) NOT NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    version INT NOT NULL DEFAULT 1,
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    createur_id BIGINT,
    INDEX idx_type (type_audit),
    INDEX idx_actif (actif)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### 3. `question_audit`
```sql
CREATE TABLE question_audit (
    question_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    libelle TEXT NOT NULL,
    description TEXT,
    type_reponse VARCHAR(50) NOT NULL DEFAULT 'OUI_NON',  -- ENUM: OUI_NON, NUMERIQUE, TEXTE, CHOIX_MULTIPLE
    exigence_reglementaire TEXT,
    critere TEXT,
    ponderation INT DEFAULT 1,
    criticite VARCHAR(50) NOT NULL DEFAULT 'MOYENNE',  -- ENUM: FAIBLE, MOYENNE, ELEVEE, CRITIQUE
    ordre INT NOT NULL DEFAULT 0,
    obligatoire BOOLEAN NOT NULL DEFAULT TRUE,
    categorie_id BIGINT,  -- Référence vers check_list
    categorie VARCHAR(100),
    INDEX idx_categorie (categorie_id),
    INDEX idx_ordre (ordre),
    FOREIGN KEY (categorie_id) REFERENCES check_list(template_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### 4. `reponse_audit`
```sql
CREATE TABLE reponse_audit (
    reponse_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    valeur VARCHAR(500) NOT NULL,
    conforme BOOLEAN NOT NULL DEFAULT TRUE,
    commentaire TEXT,
    observation TEXT,
    action_requise BOOLEAN NOT NULL DEFAULT FALSE,
    date_reponse DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    utilisateur_id BIGINT NOT NULL,
    audit_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    INDEX idx_audit (audit_id),
    INDEX idx_question (question_id),
    INDEX idx_conforme (conforme),
    FOREIGN KEY (audit_id) REFERENCES audits(audit_id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES question_audit(question_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### 5. `non_comformite`
```sql
CREATE TABLE non_comformite (
    id BIGINT PRIMARY KEY,  -- Pas AUTO_INCREMENT (défini manuellement)
    description TEXT NOT NULL,
    gravite VARCHAR(50) NOT NULL DEFAULT 'MINEURE',  -- ENUM: MINEURE, MAJEURE, CRITIQUE
    cause_racine TEXT,
    action_immediate TEXT,
    plan_action_id BIGINT,  -- Référence vers microService_PlanAction
    statut VARCHAR(50) NOT NULL DEFAULT 'OUVERTE',  -- ENUM: OUVERTE, EN_TRAITEMENT, CLOTUREE
    date_detection DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_cloture DATETIME,
    responsable_id BIGINT,
    audit_id BIGINT NOT NULL,
    reponse_id BIGINT,
    INDEX idx_statut (statut),
    INDEX idx_gravite (gravite),
    INDEX idx_audit (audit_id),
    FOREIGN KEY (audit_id) REFERENCES audits(audit_id) ON DELETE CASCADE,
    FOREIGN KEY (reponse_id) REFERENCES reponse_audit(reponse_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### ENUMs Audits

**TypeAudit**:
- INTERNE
- EXTERNE
- REGLEMENTAIRE
- CERTIFICATION
- FOURNISSEUR

**StatutAudit**:
- PLANIFIE
- EN_COURS
- TERMINE
- ANNULE

**TypeReponse**:
- OUI_NON
- NUMERIQUE
- TEXTE
- CHOIX_MULTIPLE

**NiveauCriticite**:
- FAIBLE
- MOYENNE
- ELEVEE
- CRITIQUE

**Gravite**:
- MINEURE
- MAJEURE
- CRITIQUE

**StatutNC**:
- OUVERTE
- EN_TRAITEMENT
- CLOTUREE

### Statistiques Utilisées
- **Taux de conformité global** : `SELECT AVG(taux_conformite) FROM audits WHERE statut = 'TERMINE'`

---

## 📦 BASE 4 : db_planaction (Service Plans d'Action)

### Tables

#### 1. `action_prise` (PlanAction)
```sql
CREATE TABLE action_prise (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(200),
    description TEXT,
    statut VARCHAR(50),  -- ENUM: BROUILLON, EN_ATTENTE, VALIDE, EN_COURS, CLOTURE, ANNULE
    type VARCHAR(50) NOT NULL,  -- ENUM: CORRECTIF, PREVENTIF, AMELIORATION
    source VARCHAR(50) NOT NULL,  -- ENUM: INCIDENT, AUDIT, INSPECTION, SUGGESTION
    source_id BIGINT,  -- ID de la source (incident_id, audit_id, etc.)
    priorite VARCHAR(50),  -- ENUM: FAIBLE, MOYENNE, ELEVEE, CRITIQUE
    date_creation DATETIME,
    date_echeance DATETIME,
    date_cloture DATETIME,
    responsable_id BIGINT,
    budget_estime DOUBLE,
    cout_reel DOUBLE,
    valideur_id BIGINT,
    date_validation DATETIME,
    INDEX idx_statut (statut),
    INDEX idx_type (type),
    INDEX idx_source (source, source_id),
    INDEX idx_responsable (responsable_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### 2. `action`
```sql
CREATE TABLE action (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    description TEXT,
    avancement INT DEFAULT 0,
    type VARCHAR(50),  -- ENUM: CORRECTIF, PREVENTIF, AMELIORATION
    date_debut DATETIME,
    date_echeance DATETIME,
    statut VARCHAR(50) NOT NULL DEFAULT 'A_FAIRE',  -- ENUM: A_FAIRE, EN_COURS, EN_ATTENTE, TERMINEE, VERIFIEE, ANNULEE
    priorite VARCHAR(50) DEFAULT 'MOYENNE',  -- ENUM: FAIBLE, MOYENNE, ELEVEE, CRITIQUE
    duree_estimee INT,  -- En heures
    duree_reelle INT,  -- En heures
    ressources_necessaires TEXT,
    responsable_id BIGINT,
    indicateur_efficacite VARCHAR(200),
    ressources_reelle TEXT,
    plan_action_id BIGINT,
    INDEX idx_statut (statut),
    INDEX idx_priorite (priorite),
    INDEX idx_plan (plan_action_id),
    INDEX idx_responsable (responsable_id),
    FOREIGN KEY (plan_action_id) REFERENCES action_prise(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### 3. `escalade`
```sql
CREATE TABLE escalade (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    motif VARCHAR(50) NOT NULL,  -- ENUM: RETARD, BLOCAGE, RESSOURCES_INSUFFISANTES, COMPLEXITE, etc.
    niveau VARCHAR(50) NOT NULL,  -- ENUM: NIVEAU_1_SUPERVISEUR, NIVEAU_2_MANAGER, NIVEAU_3_DIRECTION
    date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    statut VARCHAR(50) NOT NULL DEFAULT 'OUVERTE',  -- ENUM: OUVERTE, EN_TRAITEMENT, RESOLUE, ANNULEE
    description TEXT,
    resolution TEXT,
    traite_par BIGINT,
    plan_action_id BIGINT,
    action_id BIGINT,
    INDEX idx_statut (statut),
    INDEX idx_niveau (niveau),
    INDEX idx_motif (motif),
    FOREIGN KEY (plan_action_id) REFERENCES action_prise(id) ON DELETE CASCADE,
    FOREIGN KEY (action_id) REFERENCES action(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### 4. `suivi_action`
```sql
CREATE TABLE suivi_action (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    commentaire TEXT NOT NULL,
    avancement INT NOT NULL,  -- Pourcentage 0-100
    difficultes TEXT,
    solutions TEXT,
    utilisateur_id BIGINT NOT NULL,
    action_id BIGINT NOT NULL,
    INDEX idx_action (action_id),
    INDEX idx_date (date),
    FOREIGN KEY (action_id) REFERENCES action(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### 5. `verification`
```sql
CREATE TABLE verification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date_verification DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    verificateur_id BIGINT NOT NULL,
    resultat VARCHAR(50) NOT NULL,  -- ENUM: CONFORME, NON_CONFORME, PARTIELLEMENT_CONFORME
    commentaire TEXT,
    efficace BOOLEAN NOT NULL DEFAULT FALSE,
    date_prochaine_surveillance DATETIME,
    action_id BIGINT NOT NULL,
    INDEX idx_action (action_id),
    INDEX idx_resultat (resultat),
    INDEX idx_date (date_verification),
    FOREIGN KEY (action_id) REFERENCES action(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### ENUMs Plans d'Action

**StatutPlan**:
- BROUILLON
- EN_ATTENTE
- VALIDE
- EN_COURS
- CLOTURE
- ANNULE

**TypeAction**:
- CORRECTIF
- PREVENTIF
- AMELIORATION

**SourcePlan**:
- INCIDENT
- AUDIT
- INSPECTION
- SUGGESTION
- NON_CONFORMITE

**Priorite**:
- FAIBLE
- MOYENNE
- ELEVEE
- CRITIQUE

**StatutAction**:
- A_FAIRE
- EN_COURS
- EN_ATTENTE
- TERMINEE
- VERIFIEE
- ANNULEE

**MotifEscalade**:
- RETARD
- BLOCAGE
- RESSOURCES_INSUFFISANTES
- COMPLEXITE
- BESOIN_VALIDATION

**NiveauEscalade**:
- NIVEAU_1_SUPERVISEUR
- NIVEAU_2_MANAGER
- NIVEAU_3_DIRECTION

**StatutEscalade**:
- OUVERTE
- EN_TRAITEMENT
- RESOLUE
- ANNULEE

**ResultatVerification**:
- CONFORME
- NON_CONFORME
- PARTIELLEMENT_CONFORME

### Statistiques Utilisées
- **Actions en retard** : `SELECT COUNT(*) FROM action WHERE statut NOT IN ('TERMINEE', 'VERIFIEE') AND date_echeance < NOW()`

---

## 🔗 Relations Inter-Services

### Références Croisées (Microservices Communication)

Les services communiquent via **IDs stockés** (pas de clés étrangères entre bases) :

1. **Audits → Utilisateurs**
   - `audits.auditeur_id` → `utilisateur.user_id`
   - `reponse_audit.utilisateur_id` → `utilisateur.user_id`

2. **Audits → Plans d'Action**
   - `non_comformite.plan_action_id` → `action_prise.id`

3. **Plans d'Action → Utilisateurs**
   - `action_prise.responsable_id` → `utilisateur.user_id`
   - `action.responsable_id` → `utilisateur.user_id`
   - `verification.verificateur_id` → `utilisateur.user_id`

4. **Plans d'Action → Audits/Incidents**
   - `action_prise.source_id` (selon `source`) → `audits.audit_id` OU `incidents.id`

---

## 📄 Scripts de Migration H2 → MySQL

### Script Complet de Création

```sql
-- =============================================
-- SCRIPT DE CRÉATION COMPLÈTE - HSE MICROSERVICES
-- =============================================

-- BASE 1: UTILISATEURS
-- =============================================
USE db_utilisateurs;

CREATE TABLE utilisateur (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE role (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50) NOT NULL UNIQUE,
    INDEX idx_nom (nom)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES utilisateur(user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role(role_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Données initiales
INSERT INTO role (nom) VALUES ('ROLE_ADMIN'), ('ROLE_MANAGER'), ('ROLE_AUDITEUR'), ('ROLE_USER');
INSERT INTO utilisateur (nom, prenom, username, password) VALUES 
    ('Admin', 'Système', 'admin@hse.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);

-- BASE 2: INCIDENTS
-- =============================================
USE db_incidents;

CREATE TABLE incidents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATETIME NOT NULL,
    description TEXT,
    location VARCHAR(200),
    creator_name VARCHAR(100) NOT NULL,
    statut VARCHAR(50) NOT NULL,
    type_incident VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_statut (statut),
    INDEX idx_type (type_incident),
    INDEX idx_date (date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- BASE 3: AUDITS
-- =============================================
USE db_audits;

CREATE TABLE check_list (
    template_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(200) NOT NULL,
    description TEXT,
    type_audit VARCHAR(50) NOT NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    version INT NOT NULL DEFAULT 1,
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    createur_id BIGINT,
    INDEX idx_type (type_audit),
    INDEX idx_actif (actif)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE audits (
    audit_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(200) NOT NULL,
    description TEXT,
    type_audit VARCHAR(50) NOT NULL,
    date_debut DATETIME,
    date_fin DATETIME,
    date_planifiee DATETIME,
    statut VARCHAR(50) NOT NULL DEFAULT 'PLANIFIE',
    auditeur_id BIGINT NOT NULL,
    departement VARCHAR(100),
    zone VARCHAR(100),
    score_global DOUBLE,
    taux_conformite DOUBLE,
    observations TEXT,
    recommandations TEXT,
    recommendations TEXT,
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    checklist_template_id BIGINT,
    INDEX idx_statut (statut),
    INDEX idx_auditeur (auditeur_id),
    FOREIGN KEY (checklist_template_id) REFERENCES check_list(template_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE question_audit (
    question_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    libelle TEXT NOT NULL,
    description TEXT,
    type_reponse VARCHAR(50) NOT NULL DEFAULT 'OUI_NON',
    exigence_reglementaire TEXT,
    critere TEXT,
    ponderation INT DEFAULT 1,
    criticite VARCHAR(50) NOT NULL DEFAULT 'MOYENNE',
    ordre INT NOT NULL DEFAULT 0,
    obligatoire BOOLEAN NOT NULL DEFAULT TRUE,
    categorie_id BIGINT,
    categorie VARCHAR(100),
    INDEX idx_categorie (categorie_id),
    FOREIGN KEY (categorie_id) REFERENCES check_list(template_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE reponse_audit (
    reponse_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    valeur VARCHAR(500) NOT NULL,
    conforme BOOLEAN NOT NULL DEFAULT TRUE,
    commentaire TEXT,
    observation TEXT,
    action_requise BOOLEAN NOT NULL DEFAULT FALSE,
    date_reponse DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    utilisateur_id BIGINT NOT NULL,
    audit_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    INDEX idx_audit (audit_id),
    INDEX idx_question (question_id),
    FOREIGN KEY (audit_id) REFERENCES audits(audit_id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES question_audit(question_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE non_comformite (
    id BIGINT PRIMARY KEY,
    description TEXT NOT NULL,
    gravite VARCHAR(50) NOT NULL DEFAULT 'MINEURE',
    cause_racine TEXT,
    action_immediate TEXT,
    plan_action_id BIGINT,
    statut VARCHAR(50) NOT NULL DEFAULT 'OUVERTE',
    date_detection DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_cloture DATETIME,
    responsable_id BIGINT,
    audit_id BIGINT NOT NULL,
    reponse_id BIGINT,
    INDEX idx_audit (audit_id),
    FOREIGN KEY (audit_id) REFERENCES audits(audit_id) ON DELETE CASCADE,
    FOREIGN KEY (reponse_id) REFERENCES reponse_audit(reponse_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- BASE 4: PLANS D'ACTION
-- =============================================
USE db_planaction;

CREATE TABLE action_prise (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(200),
    description TEXT,
    statut VARCHAR(50),
    type VARCHAR(50) NOT NULL,
    source VARCHAR(50) NOT NULL,
    source_id BIGINT,
    priorite VARCHAR(50),
    date_creation DATETIME,
    date_echeance DATETIME,
    date_cloture DATETIME,
    responsable_id BIGINT,
    budget_estime DOUBLE,
    cout_reel DOUBLE,
    valideur_id BIGINT,
    date_validation DATETIME,
    INDEX idx_statut (statut),
    INDEX idx_responsable (responsable_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE action (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    description TEXT,
    avancement INT DEFAULT 0,
    type VARCHAR(50),
    date_debut DATETIME,
    date_echeance DATETIME,
    statut VARCHAR(50) NOT NULL DEFAULT 'A_FAIRE',
    priorite VARCHAR(50) DEFAULT 'MOYENNE',
    duree_estimee INT,
    duree_reelle INT,
    ressources_necessaires TEXT,
    responsable_id BIGINT,
    indicateur_efficacite VARCHAR(200),
    ressources_reelle TEXT,
    plan_action_id BIGINT,
    INDEX idx_statut (statut),
    INDEX idx_plan (plan_action_id),
    FOREIGN KEY (plan_action_id) REFERENCES action_prise(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE escalade (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    motif VARCHAR(50) NOT NULL,
    niveau VARCHAR(50) NOT NULL,
    date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    statut VARCHAR(50) NOT NULL DEFAULT 'OUVERTE',
    description TEXT,
    resolution TEXT,
    traite_par BIGINT,
    plan_action_id BIGINT,
    action_id BIGINT,
    INDEX idx_statut (statut),
    FOREIGN KEY (plan_action_id) REFERENCES action_prise(id) ON DELETE CASCADE,
    FOREIGN KEY (action_id) REFERENCES action(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE suivi_action (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    commentaire TEXT NOT NULL,
    avancement INT NOT NULL,
    difficultes TEXT,
    solutions TEXT,
    utilisateur_id BIGINT NOT NULL,
    action_id BIGINT NOT NULL,
    INDEX idx_action (action_id),
    FOREIGN KEY (action_id) REFERENCES action(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE verification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date_verification DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    verificateur_id BIGINT NOT NULL,
    resultat VARCHAR(50) NOT NULL,
    commentaire TEXT,
    efficace BOOLEAN NOT NULL DEFAULT FALSE,
    date_prochaine_surveillance DATETIME,
    action_id BIGINT NOT NULL,
    INDEX idx_action (action_id),
    FOREIGN KEY (action_id) REFERENCES action(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

## 📝 Fichiers application.properties à Modifier

### 1. service_utilisateurs/src/main/resources/application.properties

```properties
spring.application.name=service_utilisateurs
jwt.secret=bXlzZWNyZXRrZXlmb3Jqd3RteXNlY3JldGtleWZvcmp3dA==
server.port=8080
jwt.expiration=86400000

# Eureka Client Configuration
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true

# MySQL Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/db_utilisateurs?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=hse_user
spring.datasource.password=HseSecure2026!

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### 2. incidents/src/main/resources/application.properties

```properties
spring.application.name=incidents
server.port=8081
jwt.secret=bXlzZWNyZXRrZXlmb3Jqd3RteXNlY3JldGtleWZvcmp3dA==
jwt.expiration=86400000

# Eureka Client Configuration
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true

# MySQL Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/db_incidents?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=hse_user
spring.datasource.password=HseSecure2026!

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### 3. MicroService_Audit/src/main/resources/application.properties

```properties
spring.application.name=microservice-audit
server.port=8082
jwt.secret=bXlzZWNyZXRrZXlmb3Jqd3RteXNlY3JldGtleWZvcmp3dA==
jwt.expiration=86400000

# Eureka Client Configuration
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true

# MySQL Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/db_audits?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=hse_user
spring.datasource.password=HseSecure2026!

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### 4. microService_PlanAction/src/main/resources/application.properties

```properties
spring.application.name=microservice-planaction
server.port=8083
jwt.secret=bXlzZWNyZXRrZXlmb3Jqd3RteXNlY3JldGtleWZvcmp3dA==
jwt.expiration=86400000

# Eureka Client Configuration
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true

# MySQL Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/db_planaction?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=hse_user
spring.datasource.password=HseSecure2026!

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

---

## ✅ Checklist de Migration

### Étapes de Migration H2 → MySQL

- [ ] **1. Installer MySQL 8.0+**
- [ ] **2. Créer les 4 bases de données** (script ci-dessus)
- [ ] **3. Créer l'utilisateur MySQL** (`hse_user`)
- [ ] **4. Exécuter le script de création des tables**
- [ ] **5. Ajouter la dépendance MySQL dans chaque pom.xml** :

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

- [ ] **6. Modifier les 4 fichiers application.properties** (voir ci-dessus)
- [ ] **7. Tester la connexion de chaque service**
- [ ] **8. Changer `ddl-auto` de `update` à `validate` en production**

---

## 🎯 Résumé des Bases de Données

| Service | Base MySQL | Tables | Relations Principales |
|---------|------------|--------|----------------------|
| **service_utilisateurs** | `db_utilisateurs` | 3 | `utilisateur` ↔ `role` (Many-to-Many) |
| **incidents** | `db_incidents` | 1 | Standalone |
| **MicroService_Audit** | `db_audits` | 5 | `audits` → `check_list`, `question_audit`, `reponse_audit`, `non_comformite` |
| **microService_PlanAction** | `db_planaction` | 5 | `action_prise` → `action` → `escalade`/`suivi_action`/`verification` |

**Total : 4 bases de données | 14 tables**

---

**Document créé le : 20 janvier 2026**  
**Dernière mise à jour : 20 janvier 2026**
