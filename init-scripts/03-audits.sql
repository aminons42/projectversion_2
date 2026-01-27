-- =============================================
-- SCRIPT INIT BASE: db_audits
-- Service: MicroService_Audit (Port 8082)
-- =============================================

USE db_audits;

-- Table: check_list (Templates de checklists)
CREATE TABLE IF NOT EXISTS check_list (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Templates de checklists pour les audits';

-- Table: audits (CORRIGÉE - nom de table = "Audits" selon @Table)
CREATE TABLE IF NOT EXISTS Audits (
    Audit_id BIGINT AUTO_INCREMENT PRIMARY KEY,
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
    INDEX idx_type (type_audit),
    INDEX idx_auditeur (auditeur_id),
    INDEX idx_date_planifiee (date_planifiee),
    FOREIGN KEY (checklist_template_id) REFERENCES check_list(template_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Table des audits HSE - Nom exact: Audits (avec majuscule)';

-- Table: question_audit
CREATE TABLE IF NOT EXISTS question_audit (
    Question_id BIGINT AUTO_INCREMENT PRIMARY KEY,
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
    INDEX idx_ordre (ordre),
    FOREIGN KEY (categorie_id) REFERENCES check_list(template_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Questions des checklists audit';

-- Table: reponse_audit
CREATE TABLE IF NOT EXISTS reponse_audit (
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
    FOREIGN KEY (audit_id) REFERENCES Audits(Audit_id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES question_audit(Question_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Réponses aux questions audit';

-- Table: non_comformite
CREATE TABLE IF NOT EXISTS non_comformite (
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
    INDEX idx_statut (statut),
    INDEX idx_gravite (gravite),
    INDEX idx_audit (audit_id),
    FOREIGN KEY (audit_id) REFERENCES Audits(Audit_id) ON DELETE CASCADE,
    FOREIGN KEY (reponse_id) REFERENCES reponse_audit(reponse_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Non-conformités détectées lors des audits';
