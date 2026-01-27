-- =============================================
-- SCRIPT DE CRÉATION COMPLÈTE - HSE MICROSERVICES
-- MySQL 8.0+ / MariaDB 10.6+
-- Date: 20 Janvier 2026
-- =============================================

-- =============================================
-- ÉTAPE 1: CRÉATION DES BASES DE DONNÉES
-- =============================================

CREATE DATABASE IF NOT EXISTS db_utilisateurs CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS db_incidents CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS db_audits CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS db_planaction CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- =============================================
-- ÉTAPE 2: CRÉATION DE L'UTILISATEUR
-- =============================================

CREATE USER IF NOT EXISTS 'hse_user'@'localhost' IDENTIFIED BY 'HseSecure2026!';
GRANT ALL PRIVILEGES ON db_utilisateurs.* TO 'hse_user'@'localhost';
GRANT ALL PRIVILEGES ON db_incidents.* TO 'hse_user'@'localhost';
GRANT ALL PRIVILEGES ON db_audits.* TO 'hse_user'@'localhost';
GRANT ALL PRIVILEGES ON db_planaction.* TO 'hse_user'@'localhost';
FLUSH PRIVILEGES;

-- =============================================
-- BASE 1: DB_UTILISATEURS (Service Utilisateurs)
-- =============================================

USE db_utilisateurs;

-- Table: utilisateur
CREATE TABLE utilisateur (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL COMMENT 'BCrypt hash',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Table des utilisateurs avec authentification';

-- Table: role
CREATE TABLE role (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50) NOT NULL UNIQUE,
    INDEX idx_nom (nom)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Table des rôles (ADMIN, MANAGER, AUDITEUR, USER)';

-- Table: user_roles (Many-to-Many)
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES utilisateur(user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role(role_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Table de jointure utilisateurs-rôles';

-- Données initiales: Rôles
INSERT INTO role (nom) VALUES 
    ('ROLE_ADMIN'),
    ('ROLE_MANAGER'),
    ('ROLE_AUDITEUR'),
    ('ROLE_USER');

-- Données initiales: Admin par défaut
-- Username: admin@hse.com
-- Password: admin123 (BCrypt hash)
INSERT INTO utilisateur (nom, prenom, username, password) VALUES 
    ('Admin', 'Système', 'admin@hse.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- Assigner le rôle ADMIN
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);

-- =============================================
-- BASE 2: DB_INCIDENTS (Service Incidents)
-- =============================================

USE db_incidents;

-- Table: incidents
CREATE TABLE incidents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATETIME NOT NULL,
    description TEXT,
    location VARCHAR(200),
    creator_name VARCHAR(100) NOT NULL,
    statut VARCHAR(50) NOT NULL COMMENT 'OUVERT, EN_COURS, RESOLU, CLOS',
    type_incident VARCHAR(50) NOT NULL COMMENT 'ACCIDENT_TRAVAIL, PRESQU_ACCIDENT, INCIDENT_ENVIRONNEMENTAL, etc.',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_statut (statut),
    INDEX idx_type (type_incident),
    INDEX idx_date (date),
    INDEX idx_creator (creator_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Table des incidents HSE';

-- Exemples de données
INSERT INTO incidents (date, description, location, creator_name, statut, type_incident) VALUES 
    (NOW(), 'Chute de plain-pied dans l\'entrepôt', 'Entrepôt A - Zone 3', 'Jean Dupont', 'OUVERT', 'ACCIDENT_TRAVAIL'),
    (NOW(), 'Fuite d\'eau détectée', 'Bureau B12', 'Marie Martin', 'EN_COURS', 'INCIDENT_ENVIRONNEMENTAL'),
    (NOW(), 'Équipement de protection défectueux', 'Atelier C', 'Pierre Durand', 'RESOLU', 'PRESQU_ACCIDENT');

-- =============================================
-- BASE 3: DB_AUDITS (Service Audits)
-- =============================================

USE db_audits;

-- Table: check_list (Templates de checklists)
CREATE TABLE check_list (
    template_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(200) NOT NULL,
    description TEXT,
    type_audit VARCHAR(50) NOT NULL COMMENT 'INTERNE, EXTERNE, REGLEMENTAIRE, etc.',
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    version INT NOT NULL DEFAULT 1,
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    createur_id BIGINT COMMENT 'Référence vers utilisateur.user_id',
    INDEX idx_type (type_audit),
    INDEX idx_actif (actif)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Templates de checklists pour les audits';

-- Table: audits
CREATE TABLE audits (
    audit_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(200) NOT NULL,
    description TEXT,
    type_audit VARCHAR(50) NOT NULL COMMENT 'INTERNE, EXTERNE, REGLEMENTAIRE, etc.',
    date_debut DATETIME,
    date_fin DATETIME,
    date_planifiee DATETIME,
    statut VARCHAR(50) NOT NULL DEFAULT 'PLANIFIE' COMMENT 'PLANIFIE, EN_COURS, TERMINE, ANNULE',
    auditeur_id BIGINT NOT NULL COMMENT 'Référence vers utilisateur.user_id',
    departement VARCHAR(100),
    zone VARCHAR(100),
    score_global DOUBLE COMMENT 'Score calculé automatiquement',
    taux_conformite DOUBLE COMMENT 'Taux de conformité en %',
    observations TEXT,
    recommandations TEXT,
    recommendations TEXT,
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    checklist_template_id BIGINT,
    INDEX idx_statut (statut),
    INDEX idx_type (type_audit),
    INDEX idx_auditeur (auditeur_id),
    INDEX idx_date_planifiee (date_planifiee),
    FOREIGN KEY (checklist_template_id) REFERENCES check_list(template_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Table des audits HSE';

-- Table: question_audit
CREATE TABLE question_audit (
    question_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    libelle TEXT NOT NULL,
    description TEXT,
    type_reponse VARCHAR(50) NOT NULL DEFAULT 'OUI_NON' COMMENT 'OUI_NON, NUMERIQUE, TEXTE, CHOIX_MULTIPLE',
    exigence_reglementaire TEXT,
    critere TEXT,
    ponderation INT DEFAULT 1,
    criticite VARCHAR(50) NOT NULL DEFAULT 'MOYENNE' COMMENT 'FAIBLE, MOYENNE, ELEVEE, CRITIQUE',
    ordre INT NOT NULL DEFAULT 0,
    obligatoire BOOLEAN NOT NULL DEFAULT TRUE,
    categorie_id BIGINT COMMENT 'Référence vers check_list.template_id',
    categorie VARCHAR(100),
    INDEX idx_categorie (categorie_id),
    INDEX idx_ordre (ordre),
    FOREIGN KEY (categorie_id) REFERENCES check_list(template_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Questions des checklists d\'audit';

-- Table: reponse_audit
CREATE TABLE reponse_audit (
    reponse_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    valeur VARCHAR(500) NOT NULL,
    conforme BOOLEAN NOT NULL DEFAULT TRUE,
    commentaire TEXT,
    observation TEXT,
    action_requise BOOLEAN NOT NULL DEFAULT FALSE,
    date_reponse DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    utilisateur_id BIGINT NOT NULL COMMENT 'Référence vers utilisateur.user_id',
    audit_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    INDEX idx_audit (audit_id),
    INDEX idx_question (question_id),
    INDEX idx_conforme (conforme),
    FOREIGN KEY (audit_id) REFERENCES audits(audit_id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES question_audit(question_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Réponses aux questions d\'audit';

-- Table: non_comformite
CREATE TABLE non_comformite (
    id BIGINT PRIMARY KEY COMMENT 'ID défini manuellement (pas AUTO_INCREMENT)',
    description TEXT NOT NULL,
    gravite VARCHAR(50) NOT NULL DEFAULT 'MINEURE' COMMENT 'MINEURE, MAJEURE, CRITIQUE',
    cause_racine TEXT,
    action_immediate TEXT,
    plan_action_id BIGINT COMMENT 'Référence vers action_prise.id (inter-service)',
    statut VARCHAR(50) NOT NULL DEFAULT 'OUVERTE' COMMENT 'OUVERTE, EN_TRAITEMENT, CLOTUREE',
    date_detection DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_cloture DATETIME,
    responsable_id BIGINT COMMENT 'Référence vers utilisateur.user_id',
    audit_id BIGINT NOT NULL,
    reponse_id BIGINT,
    INDEX idx_statut (statut),
    INDEX idx_gravite (gravite),
    INDEX idx_audit (audit_id),
    FOREIGN KEY (audit_id) REFERENCES audits(audit_id) ON DELETE CASCADE,
    FOREIGN KEY (reponse_id) REFERENCES reponse_audit(reponse_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Non-conformités détectées lors des audits';

-- =============================================
-- BASE 4: DB_PLANACTION (Service Plans d'Action)
-- =============================================

USE db_planaction;

-- Table: action_prise (PlanAction)
CREATE TABLE action_prise (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(200),
    description TEXT,
    statut VARCHAR(50) COMMENT 'BROUILLON, EN_ATTENTE, VALIDE, EN_COURS, CLOTURE, ANNULE',
    type VARCHAR(50) NOT NULL COMMENT 'CORRECTIF, PREVENTIF, AMELIORATION',
    source VARCHAR(50) NOT NULL COMMENT 'INCIDENT, AUDIT, INSPECTION, SUGGESTION, NON_CONFORMITE',
    source_id BIGINT COMMENT 'ID de la source (incident_id, audit_id, etc.)',
    priorite VARCHAR(50) COMMENT 'FAIBLE, MOYENNE, ELEVEE, CRITIQUE',
    date_creation DATETIME,
    date_echeance DATETIME,
    date_cloture DATETIME,
    responsable_id BIGINT COMMENT 'Référence vers utilisateur.user_id',
    budget_estime DOUBLE,
    cout_reel DOUBLE,
    valideur_id BIGINT COMMENT 'Référence vers utilisateur.user_id',
    date_validation DATETIME,
    INDEX idx_statut (statut),
    INDEX idx_type (type),
    INDEX idx_source (source, source_id),
    INDEX idx_responsable (responsable_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Plans d\'action correctifs/préventifs';

-- Table: action
CREATE TABLE action (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    description TEXT,
    avancement INT DEFAULT 0 COMMENT 'Pourcentage 0-100',
    type VARCHAR(50) COMMENT 'CORRECTIF, PREVENTIF, AMELIORATION',
    date_debut DATETIME,
    date_echeance DATETIME,
    statut VARCHAR(50) NOT NULL DEFAULT 'A_FAIRE' COMMENT 'A_FAIRE, EN_COURS, EN_ATTENTE, TERMINEE, VERIFIEE, ANNULEE',
    priorite VARCHAR(50) DEFAULT 'MOYENNE' COMMENT 'FAIBLE, MOYENNE, ELEVEE, CRITIQUE',
    duree_estimee INT COMMENT 'Durée en heures',
    duree_reelle INT COMMENT 'Durée réelle en heures',
    ressources_necessaires TEXT,
    responsable_id BIGINT COMMENT 'Référence vers utilisateur.user_id',
    indicateur_efficacite VARCHAR(200),
    ressources_reelle TEXT,
    plan_action_id BIGINT,
    INDEX idx_statut (statut),
    INDEX idx_priorite (priorite),
    INDEX idx_plan (plan_action_id),
    INDEX idx_responsable (responsable_id),
    INDEX idx_date_echeance (date_echeance),
    FOREIGN KEY (plan_action_id) REFERENCES action_prise(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Actions individuelles d\'un plan';

-- Table: escalade
CREATE TABLE escalade (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    motif VARCHAR(50) NOT NULL COMMENT 'RETARD, BLOCAGE, RESSOURCES_INSUFFISANTES, COMPLEXITE, etc.',
    niveau VARCHAR(50) NOT NULL COMMENT 'NIVEAU_1_SUPERVISEUR, NIVEAU_2_MANAGER, NIVEAU_3_DIRECTION',
    date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    statut VARCHAR(50) NOT NULL DEFAULT 'OUVERTE' COMMENT 'OUVERTE, EN_TRAITEMENT, RESOLUE, ANNULEE',
    description TEXT,
    resolution TEXT,
    traite_par BIGINT COMMENT 'Référence vers utilisateur.user_id',
    plan_action_id BIGINT,
    action_id BIGINT,
    INDEX idx_statut (statut),
    INDEX idx_niveau (niveau),
    INDEX idx_motif (motif),
    INDEX idx_date (date),
    FOREIGN KEY (plan_action_id) REFERENCES action_prise(id) ON DELETE CASCADE,
    FOREIGN KEY (action_id) REFERENCES action(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Escalades de problèmes sur les actions';

-- Table: suivi_action
CREATE TABLE suivi_action (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    commentaire TEXT NOT NULL,
    avancement INT NOT NULL COMMENT 'Pourcentage 0-100',
    difficultes TEXT,
    solutions TEXT,
    utilisateur_id BIGINT NOT NULL COMMENT 'Référence vers utilisateur.user_id',
    action_id BIGINT NOT NULL,
    INDEX idx_action (action_id),
    INDEX idx_date (date),
    FOREIGN KEY (action_id) REFERENCES action(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Suivi de l\'avancement des actions';

-- Table: verification
CREATE TABLE verification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date_verification DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    verificateur_id BIGINT NOT NULL COMMENT 'Référence vers utilisateur.user_id',
    resultat VARCHAR(50) NOT NULL COMMENT 'CONFORME, NON_CONFORME, PARTIELLEMENT_CONFORME',
    commentaire TEXT,
    efficace BOOLEAN NOT NULL DEFAULT FALSE,
    date_prochaine_surveillance DATETIME,
    action_id BIGINT NOT NULL,
    INDEX idx_action (action_id),
    INDEX idx_resultat (resultat),
    INDEX idx_date (date_verification),
    FOREIGN KEY (action_id) REFERENCES action(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Vérifications de l\'efficacité des actions';

-- =============================================
-- VÉRIFICATION DES TABLES CRÉÉES
-- =============================================

-- Lister toutes les tables de chaque base
SELECT 'db_utilisateurs' AS base_de_donnees, COUNT(*) AS nombre_tables 
FROM information_schema.tables 
WHERE table_schema = 'db_utilisateurs' AND table_type = 'BASE TABLE'
UNION ALL
SELECT 'db_incidents', COUNT(*) 
FROM information_schema.tables 
WHERE table_schema = 'db_incidents' AND table_type = 'BASE TABLE'
UNION ALL
SELECT 'db_audits', COUNT(*) 
FROM information_schema.tables 
WHERE table_schema = 'db_audits' AND table_type = 'BASE TABLE'
UNION ALL
SELECT 'db_planaction', COUNT(*) 
FROM information_schema.tables 
WHERE table_schema = 'db_planaction' AND table_type = 'BASE TABLE';

-- =============================================
-- FIN DU SCRIPT
-- =============================================
-- Total: 4 bases de données | 14 tables créées
-- Utilisateur: hse_user / HseSecure2026!
-- =============================================
