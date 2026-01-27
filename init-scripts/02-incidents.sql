-- =============================================
-- SCRIPT INIT BASE: db_incidents
-- Service: incidents (Port 8081)
-- =============================================

USE db_incidents;

-- Table: incidents (CORRIGÉE selon l'entité JPA)
CREATE TABLE IF NOT EXISTS incidents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATETIME,
    description VARCHAR(500),
    location VARCHAR(200),
    creator_name VARCHAR(100) NOT NULL,
    statut VARCHAR(50),
    type_incident VARCHAR(50),
    INDEX idx_statut (statut),
    INDEX idx_type_incident (type_incident),
    INDEX idx_date (date),
    INDEX idx_creator (creator_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Table des incidents HSE - type_incident est un ENUM Java (CHUTE, FEU, FUITE_CHIMIQUE, PRESQU_ACCIDENT, AUTRE)';

-- Note: type_incident est stocké comme VARCHAR car c'est un @Enumerated(EnumType.STRING)
-- Valeurs possibles selon TypeIncident.java:
-- - CHUTE
-- - FEU
-- - FUITE_CHIMIQUE
-- - PRESQU_ACCIDENT
-- - AUTRE

-- Exemples de données
INSERT INTO incidents (date, description, location, creator_name, statut, type_incident) VALUES 
    (NOW(), 'Chute de plain-pied dans l\'entrepôt', 'Entrepôt A - Zone 3', 'Jean Dupont', 'OUVERT', 'CHUTE'),
    (NOW(), 'Début d\'incendie détecté et éteint', 'Atelier B', 'Marie Martin', 'EN_COURS', 'FEU'),
    (NOW(), 'Fuite de produit chimique mineure', 'Laboratoire C', 'Pierre Durand', 'RESOLU', 'FUITE_CHIMIQUE')
ON DUPLICATE KEY UPDATE description=VALUES(description);
