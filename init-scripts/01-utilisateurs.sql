-- =============================================
-- SCRIPT INIT BASE: db_utilisateurs
-- Service: service_utilisateurs (Port 8080)
-- =============================================

USE db_utilisateurs;

-- Table: utilisateur (CORRIGÉE selon l'entité JPA)
CREATE TABLE IF NOT EXISTS utilisateur (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100),
    prenom VARCHAR(100),
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Table des utilisateurs - username est utilisé comme identifiant';

-- Table: role
CREATE TABLE IF NOT EXISTS role (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50) NOT NULL UNIQUE,
    INDEX idx_nom (nom)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Table des rôles';

-- Table: user_roles (Many-to-Many)
CREATE TABLE IF NOT EXISTS user_roles (
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
    ('ROLE_USER')
ON DUPLICATE KEY UPDATE nom=VALUES(nom);

-- Données initiales: Admin par défaut
-- Username: admin@hse.com
-- Password: admin123 (BCrypt hash)
INSERT INTO utilisateur (nom, prenom, username, password) VALUES 
    ('Admin', 'Système', 'admin@hse.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy')
ON DUPLICATE KEY UPDATE nom=VALUES(nom);

-- Assigner le rôle ADMIN (seulement si pas déjà assigné)
INSERT IGNORE INTO user_roles (user_id, role_id) 
SELECT u.user_id, r.role_id 
FROM utilisateur u, role r 
WHERE u.username = 'admin@hse.com' AND r.nom = 'ROLE_ADMIN';
