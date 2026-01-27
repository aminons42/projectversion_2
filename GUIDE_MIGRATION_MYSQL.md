# 🚀 Guide de Migration H2 → MySQL - HSE Microservices

## 📋 Vue d'Ensemble

Ce guide détaille la migration de H2 (base en mémoire) vers MySQL pour les 4 microservices du projet HSE.

**Durée estimée** : 30-45 minutes  
**Niveau** : Intermédiaire

---

## ✅ Prérequis

Avant de commencer, vérifiez que vous avez :

- [ ] **MySQL 8.0+** ou **MariaDB 10.6+** installé
- [ ] Accès administrateur MySQL (`root`)
- [ ] MySQL en cours d'exécution sur le port **3306**
- [ ] Client MySQL (mysql.exe, MySQL Workbench, ou HeidiSQL)
- [ ] Les 4 microservices arrêtés

---

## 📦 ÉTAPE 1 : Installation de MySQL (si nécessaire)

### Windows

**Option A : MySQL Installer**
```powershell
# Télécharger depuis https://dev.mysql.com/downloads/installer/
# Choisir "Custom Install"
# Sélectionner: MySQL Server 8.0 + MySQL Workbench
# Définir le mot de passe root pendant l'installation
```

**Option B : Chocolatey**
```powershell
choco install mysql -y
```

### Vérifier l'installation

```powershell
# Tester la connexion MySQL
mysql -u root -p

# Dans MySQL, vérifier la version
SELECT VERSION();
```

---

## 🗄️ ÉTAPE 2 : Créer les Bases de Données

### Méthode 1 : Script SQL Automatique (Recommandé)

1. **Ouvrir un terminal MySQL**

```powershell
cd d:\services_repo
mysql -u root -p < mysql_init_script.sql
```

2. **Entrer votre mot de passe root** quand demandé

3. **Vérifier la création**

```sql
mysql -u root -p

-- Dans MySQL
SHOW DATABASES;
-- Devrait afficher:
-- db_utilisateurs
-- db_incidents
-- db_audits
-- db_planaction

-- Vérifier les privilèges de hse_user
SELECT User, Host FROM mysql.user WHERE User = 'hse_user';

-- Tester la connexion avec hse_user
EXIT;
mysql -u hse_user -p
-- Mot de passe: HseSecure2026!
```

### Méthode 2 : Création Manuelle (Ligne par Ligne)

```sql
-- Se connecter à MySQL en tant que root
mysql -u root -p

-- Créer les bases de données
CREATE DATABASE db_utilisateurs CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE db_incidents CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE db_audits CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE db_planaction CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Créer l'utilisateur
CREATE USER 'hse_user'@'localhost' IDENTIFIED BY 'HseSecure2026!';

-- Accorder les privilèges
GRANT ALL PRIVILEGES ON db_utilisateurs.* TO 'hse_user'@'localhost';
GRANT ALL PRIVILEGES ON db_incidents.* TO 'hse_user'@'localhost';
GRANT ALL PRIVILEGES ON db_audits.* TO 'hse_user'@'localhost';
GRANT ALL PRIVILEGES ON db_planaction.* TO 'hse_user'@'localhost';
FLUSH PRIVILEGES;

-- Vérifier
SHOW DATABASES;
```

---

## 🔧 ÉTAPE 3 : Ajouter la Dépendance MySQL dans les POM.xml

Tous les 4 services ont déjà la dépendance MySQL dans leur `pom.xml`. Vérifiez sa présence :

### Vérification

```powershell
# Service Utilisateurs
cd d:\services_repo\service_utilisateurs
grep -A 3 "mysql-connector" pom.xml

# Incidents
cd d:\services_repo\incidents
grep -A 3 "mysql-connector" pom.xml

# MicroService_Audit
cd d:\services_repo\MicroService_Audit
grep -A 3 "mysql-connector" pom.xml

# microService_PlanAction
cd d:\services_repo\microService_PlanAction
grep -A 3 "mysql-connector" pom.xml
```

**Si absente**, ajoutez dans chaque `pom.xml` dans la section `<dependencies>` :

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

---

## ⚙️ ÉTAPE 4 : Modifier les Fichiers application.properties

Remplacez la configuration H2 par MySQL dans **chaque service**.

### 4.1 Service Utilisateurs

**Fichier** : `service_utilisateurs/src/main/resources/application.properties`

**Remplacer** :
```properties
# H2 Database (base de données en mémoire pour tests)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# H2 Console (accès via navigateur)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

**Par** :
```properties
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

### 4.2 Service Incidents

**Fichier** : `incidents/src/main/resources/application.properties`

**Remplacer** :
```properties
# H2 Database (base en mémoire pour tests)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

**Par** :
```properties
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

### 4.3 MicroService_Audit

**Fichier** : `MicroService_Audit/src/main/resources/application.properties`

**Ajouter/Modifier** :
```properties
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

### 4.4 microService_PlanAction

**Fichier** : `microService_PlanAction/src/main/resources/application.properties`

**Ajouter/Modifier** :
```properties
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

## 🧪 ÉTAPE 5 : Tester la Connexion de Chaque Service

### 5.1 Démarrer les Services dans l'Ordre

```powershell
# Terminal 1: Eureka Server (ne nécessite pas de base de données)
cd d:\services_repo\eureka-server
.\mvnw.cmd spring-boot:run

# Attendre que Eureka démarre (environ 30 secondes)

# Terminal 2: Service Utilisateurs
cd d:\services_repo\service_utilisateurs
.\mvnw.cmd spring-boot:run

# Terminal 3: Incidents
cd d:\services_repo\incidents
.\mvnw.cmd spring-boot:run

# Terminal 4: MicroService_Audit
cd d:\services_repo\MicroService_Audit
.\mvnw.cmd spring-boot:run

# Terminal 5: microService_PlanAction
cd d:\services_repo\microService_PlanAction
.\mvnw.cmd spring-boot:run

# Terminal 6: API Gateway
cd d:\services_repo\api-gateway
.\mvnw.cmd spring-boot:run
```

### 5.2 Vérifier les Logs de Connexion

Dans chaque terminal, cherchez ces messages de **SUCCÈS** :

```
✅ HikariPool-1 - Starting...
✅ HikariPool-1 - Start completed.
✅ Hibernate: create table ...
✅ Started [Service]Application in X.XXX seconds
```

### 5.3 Vérifier les Tables Créées

```sql
-- Se connecter à MySQL
mysql -u hse_user -p
-- Mot de passe: HseSecure2026!

-- Vérifier db_utilisateurs
USE db_utilisateurs;
SHOW TABLES;
-- Devrait afficher: utilisateur, role, user_roles

-- Vérifier db_incidents
USE db_incidents;
SHOW TABLES;
-- Devrait afficher: incidents

-- Vérifier db_audits
USE db_audits;
SHOW TABLES;
-- Devrait afficher: audits, check_list, question_audit, reponse_audit, non_comformite

-- Vérifier db_planaction
USE db_planaction;
SHOW TABLES;
-- Devrait afficher: action_prise, action, escalade, suivi_action, verification
```

---

## 📊 ÉTAPE 6 : Vérifier les Données Initiales

### 6.1 Vérifier l'Admin par Défaut

```sql
USE db_utilisateurs;

-- Vérifier l'utilisateur admin
SELECT * FROM utilisateur;
-- Devrait afficher: admin@hse.com

-- Vérifier les rôles
SELECT * FROM role;
-- Devrait afficher: ROLE_ADMIN, ROLE_MANAGER, ROLE_AUDITEUR, ROLE_USER

-- Vérifier l'association
SELECT u.username, r.nom 
FROM utilisateur u 
JOIN user_roles ur ON u.user_id = ur.user_id 
JOIN role r ON ur.role_id = r.role_id;
-- Devrait afficher: admin@hse.com | ROLE_ADMIN
```

### 6.2 Tester l'API de Connexion

```powershell
# Tester le login via API Gateway
curl -X POST http://localhost:8084/api/auth/login `
  -H "Content-Type: application/json" `
  -d '{\"username\":\"admin@hse.com\",\"password\":\"admin123\"}'

# Devrait retourner un JWT token
```

---

## 🔒 ÉTAPE 7 : Sécuriser pour la Production

### 7.1 Changer le Mot de Passe MySQL

```sql
-- Dans MySQL
ALTER USER 'hse_user'@'localhost' IDENTIFIED BY 'VotreMotDePasseFort2026!';
FLUSH PRIVILEGES;
```

Puis mettre à jour les **4 fichiers `application.properties`** :

```properties
spring.datasource.password=VotreMotDePasseFort2026!
```

### 7.2 Externaliser les Credentials (Production)

**Créer** : `application-prod.properties` dans chaque service

```properties
# Production MySQL Configuration
spring.datasource.url=jdbc:mysql://mysql-server.production.com:3306/db_utilisateurs?useSSL=true&requireSSL=true
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# Hibernate en mode validation (ne crée plus les tables)
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
```

**Démarrer en production** :

```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=prod
```

### 7.3 Changer `ddl-auto` en Production

⚠️ **IMPORTANT** : En production, changer :

```properties
# DÉVELOPPEMENT (crée/modifie les tables automatiquement)
spring.jpa.hibernate.ddl-auto=update

# PRODUCTION (valide uniquement, ne modifie pas)
spring.jpa.hibernate.ddl-auto=validate
```

---

## 🐛 Dépannage

### Erreur : "Access denied for user 'hse_user'@'localhost'"

**Solution** :

```sql
-- Vérifier les privilèges
SHOW GRANTS FOR 'hse_user'@'localhost';

-- Recréer les privilèges
GRANT ALL PRIVILEGES ON db_utilisateurs.* TO 'hse_user'@'localhost';
GRANT ALL PRIVILEGES ON db_incidents.* TO 'hse_user'@'localhost';
GRANT ALL PRIVILEGES ON db_audits.* TO 'hse_user'@'localhost';
GRANT ALL PRIVILEGES ON db_planaction.* TO 'hse_user'@'localhost';
FLUSH PRIVILEGES;
```

### Erreur : "Unknown database 'db_utilisateurs'"

**Solution** :

```sql
-- Recréer la base
CREATE DATABASE db_utilisateurs CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Erreur : "Table 'db_utilisateurs.utilisateur' doesn't exist"

**Solution** : Hibernate ne crée pas les tables automatiquement

1. Vérifier `application.properties` :

```properties
spring.jpa.hibernate.ddl-auto=update  # Doit être "update", pas "validate"
```

2. Redémarrer le service

### Erreur : "Communications link failure"

**Solution** : MySQL n'est pas démarré

```powershell
# Windows - Démarrer MySQL
net start MySQL80

# Vérifier le statut
sc query MySQL80
```

---

## ✅ Checklist de Migration Complète

- [ ] MySQL 8.0+ installé et démarré
- [ ] 4 bases de données créées (`db_utilisateurs`, `db_incidents`, `db_audits`, `db_planaction`)
- [ ] Utilisateur `hse_user` créé avec privilèges
- [ ] Dépendance MySQL présente dans les 4 `pom.xml`
- [ ] 4 fichiers `application.properties` modifiés
- [ ] Eureka Server démarré (port 8761)
- [ ] Service Utilisateurs démarré (port 8080) - 3 tables créées
- [ ] Service Incidents démarré (port 8081) - 1 table créée
- [ ] MicroService_Audit démarré (port 8082) - 5 tables créées
- [ ] microService_PlanAction démarré (port 8083) - 5 tables créées
- [ ] API Gateway démarré (port 8084)
- [ ] Admin par défaut créé (`admin@hse.com` / `admin123`)
- [ ] Test de connexion réussi via API
- [ ] Angular frontend démarré (port 4200)
- [ ] Test de login frontend réussi

---

## 📚 Ressources

### Fichiers Importants

- **Schéma complet** : [MYSQL_DATABASE_SCHEMA.md](MYSQL_DATABASE_SCHEMA.md)
- **Script SQL** : [mysql_init_script.sql](mysql_init_script.sql)
- **Guide de tests** : [TESTING_GUIDE.md](TESTING_GUIDE.md)

### Commandes MySQL Utiles

```sql
-- Lister toutes les bases
SHOW DATABASES;

-- Compter les tables par base
SELECT table_schema, COUNT(*) 
FROM information_schema.tables 
WHERE table_type = 'BASE TABLE' 
GROUP BY table_schema;

-- Voir la taille des bases
SELECT table_schema AS "Database", 
       ROUND(SUM(data_length + index_length) / 1024 / 1024, 2) AS "Size (MB)" 
FROM information_schema.tables 
GROUP BY table_schema;

-- Compter les données
USE db_utilisateurs;
SELECT 'utilisateur' AS table_name, COUNT(*) AS rows FROM utilisateur
UNION ALL
SELECT 'role', COUNT(*) FROM role;
```

---

## 🎯 Prochaines Étapes

Après la migration MySQL :

1. ✅ **Tester toutes les fonctionnalités** (CRUD, Stats, KPIs)
2. **Configurer les sauvegardes MySQL** (mysqldump automatisé)
3. **Optimiser les index** selon les requêtes
4. **Activer le monitoring** (MySQL Workbench, Prometheus)
5. **Documenter le schéma** (diagrammes ER)
6. **Préparer le déploiement** (Docker, cloud)

---

**Document créé le : 20 janvier 2026**  
**Dernière mise à jour : 20 janvier 2026**  
**Version : 1.0**
