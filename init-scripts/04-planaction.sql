-- =============================================
-- SCRIPT INIT BASE: db_planaction
-- Service: microService_PlanAction (Port 8083)
-- =============================================

USE db_planaction;

-- Table: action_prise (CORRIGÉE - nom exact selon @Table = "ActionPrise")
CREATE TABLE IF NOT EXISTS ActionPrise (
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
    INDEX idx_type (type),
    INDEX idx_source (source, source_id),
    INDEX idx_responsable (responsable_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Plans action correctifs/préventifs - Nom exact: ActionPrise';

-- Table: action (CORRIGÉE selon l'entité JPA)
CREATE TABLE IF NOT EXISTS action (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    description TEXT,
    Avancement INT DEFAULT 0,
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
    INDEX idx_priorite (priorite),
    INDEX idx_plan (plan_action_id),
    INDEX idx_responsable (responsable_id),
    INDEX idx_date_echeance (date_echeance),
    FOREIGN KEY (plan_action_id) REFERENCES ActionPrise(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Actions individuelles - Attention: colonne Avancement avec majuscule';

-- Table: escalade
CREATE TABLE IF NOT EXISTS escalade (
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
    INDEX idx_niveau (niveau),
    INDEX idx_motif (motif),
    INDEX idx_date (date),
    FOREIGN KEY (plan_action_id) REFERENCES ActionPrise(id) ON DELETE CASCADE,
    FOREIGN KEY (action_id) REFERENCES action(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Escalades de problèmes sur les actions';

-- Table: suivi_action
CREATE TABLE IF NOT EXISTS suivi_action (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    commentaire TEXT NOT NULL,
    avancement INT NOT NULL,
    difficultes TEXT,
    solutions TEXT,
    utilisateur_id BIGINT NOT NULL,
    action_id BIGINT NOT NULL,
    INDEX idx_action (action_id),
    INDEX idx_date (date),
    FOREIGN KEY (action_id) REFERENCES action(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Suivi avancement des actions';

-- Table: verification
CREATE TABLE IF NOT EXISTS verification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date_verification DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    verificateur_id BIGINT NOT NULL,
    resultat VARCHAR(50) NOT NULL,
    commentaire TEXT,
    efficace BOOLEAN NOT NULL DEFAULT FALSE,
    date_prochaine_surveillance DATETIME,
    action_id BIGINT NOT NULL,
    INDEX idx_action (action_id),
    INDEX idx_resultat (resultat),
    INDEX idx_date (date_verification),
    FOREIGN KEY (action_id) REFERENCES action(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Vérifications efficacité des actions';
