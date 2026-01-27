# 🐳 Guide Docker - Bases de Données MySQL pour HSE Microservices

## 📋 Vue d'Ensemble

Ce projet utilise **Docker Compose** pour gérer 4 conteneurs MySQL indépendants, un pour chaque microservice.

### Architecture Docker

```
┌──────────────────────────────────────────────────────┐
│              Docker Network: hse-network             │
├──────────────────────────────────────────────────────┤
│                                                      │
│  mysql-utilisateurs  →  Port 3307  →  db_utilisateurs│
│  mysql-incidents     →  Port 3308  →  db_incidents   │
│  mysql-audits        →  Port 3309  →  db_audits      │
│  mysql-planaction    →  Port 3310  →  db_planaction  │
│                                                      │
│  phpMyAdmin          →  Port 8090                   │
│                                                      │
└──────────────────────────────────────────────────────┘
```

---

## 🚀 Démarrage Rapide

### 1. Prérequis

- **Docker Desktop** installé ([Télécharger](https://www.docker.com/products/docker-desktop))
- **Docker Compose** (inclus dans Docker Desktop)

Vérifier l'installation :
```powershell
docker --version
docker-compose --version
```

### 2. Démarrer les Bases de Données

```powershell
# Aller dans le dossier du projet
cd d:\services_repo

# Démarrer tous les conteneurs MySQL
docker-compose up -d

# Vérifier que les 5 conteneurs sont démarrés
docker-compose ps
```

**Résultat attendu** :
```
NAME                   STATUS          PORTS
hse_mysql_utilisateurs  Up 30 seconds   0.0.0.0:3307->3306/tcp
hse_mysql_incidents     Up 30 seconds   0.0.0.0:3308->3306/tcp
hse_mysql_audits        Up 30 seconds   0.0.0.0:3309->3306/tcp
hse_mysql_planaction    Up 30 seconds   0.0.0.0:3310->3306/tcp
hse_phpmyadmin          Up 30 seconds   0.0.0.0:8090->80/tcp
```

### 3. Vérifier les Bases de Données

**Option A : Via phpMyAdmin** (Recommandé pour les débutants)

1. Ouvrir : http://localhost:8090
2. Choisir un serveur :
   - `mysql-utilisateurs` pour db_utilisateurs
   - `mysql-incidents` pour db_incidents
   - `mysql-audits` pour db_audits
   - `mysql-planaction` pour db_planaction
3. Se connecter :
   - **Utilisateur** : `hse_user`
   - **Mot de passe** : `HseSecure2026!`

**Option B : Via ligne de commande**

```powershell
# Se connecter à db_utilisateurs
docker exec -it hse_mysql_utilisateurs mysql -u hse_user -p
# Entrer le mot de passe: HseSecure2026!

# Vérifier les tables
USE db_utilisateurs;
SHOW TABLES;
SELECT * FROM utilisateur;
```

### 4. Démarrer les Microservices

```powershell
# Terminal 1: Eureka
cd d:\services_repo\eureka-server
.\mvnw.cmd spring-boot:run

# Terminal 2: Service Utilisateurs (attendre 10 secondes après chaque)
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

# Terminal 7: Angular Frontend
cd d:\services_repo\angular_project\my-login-app
npm start
```

---

## 🔧 Configuration des Ports

| Service | Type | Port Docker | Port Spring Boot | Base de Données |
|---------|------|-------------|------------------|-----------------|
| **mysql-utilisateurs** | MySQL | 3307 | - | db_utilisateurs |
| **mysql-incidents** | MySQL | 3308 | - | db_incidents |
| **mysql-audits** | MySQL | 3309 | - | db_audits |
| **mysql-planaction** | MySQL | 3310 | - | db_planaction |
| **phpMyAdmin** | Web UI | 8090 | - | - |
| **service_utilisateurs** | Spring Boot | - | 8080 | → 3307 |
| **incidents** | Spring Boot | - | 8081 | → 3308 |
| **MicroService_Audit** | Spring Boot | - | 8082 | → 3309 |
| **microService_PlanAction** | Spring Boot | - | 8083 | → 3310 |

---

## 📊 Détails des Bases de Données

### 1. db_utilisateurs (Port 3307)

**Tables** : `utilisateur`, `role`, `user_roles`

**Données initiales** :
- Admin : `admin@hse.com` / `admin123`
- Rôles : ROLE_ADMIN, ROLE_MANAGER, ROLE_AUDITEUR, ROLE_USER

**Vérification** :
```sql
USE db_utilisateurs;
SELECT u.username, r.nom 
FROM utilisateur u 
JOIN user_roles ur ON u.user_id = ur.user_id 
JOIN role r ON ur.role_id = r.role_id;
```

### 2. db_incidents (Port 3308)

**Tables** : `incidents`

**Colonnes** :
- `id`, `date`, `description`, `location`, `creator_name`
- `statut`, `type_incident` (ENUM: CHUTE, FEU, FUITE_CHIMIQUE, PRESQU_ACCIDENT, AUTRE)

**Données exemples** : 3 incidents créés

### 3. db_audits (Port 3309)

**Tables** : `Audits`, `check_list`, `question_audit`, `reponse_audit`, `non_comformite`

⚠️ **Attention** : Nom exact de la table = `Audits` (avec majuscule)

**Relations** :
- `Audits` ← `check_list` (checklist template)
- `Audits` → `question_audit` → `reponse_audit`
- `reponse_audit` → `non_comformite`

### 4. db_planaction (Port 3310)

**Tables** : `ActionPrise`, `action`, `escalade`, `suivi_action`, `verification`

⚠️ **Attention** : 
- Nom exact de la table = `ActionPrise` (avec majuscule)
- Colonne `Avancement` avec majuscule dans la table `action`

**Relations** :
- `ActionPrise` → `action` → `suivi_action`, `verification`, `escalade`

---

## 🛠️ Commandes Docker Utiles

### Gestion des Conteneurs

```powershell
# Démarrer tous les conteneurs
docker-compose up -d

# Arrêter tous les conteneurs
docker-compose down

# Redémarrer un conteneur spécifique
docker-compose restart mysql-utilisateurs

# Voir les logs d'un conteneur
docker-compose logs -f mysql-utilisateurs

# Voir l'état des conteneurs
docker-compose ps

# Arrêter et SUPPRIMER les volumes (⚠️ perte de données)
docker-compose down -v
```

### Accès aux Conteneurs

```powershell
# Se connecter au conteneur MySQL
docker exec -it hse_mysql_utilisateurs bash

# Exécuter une commande MySQL directement
docker exec -it hse_mysql_utilisateurs mysql -u root -proot123 -e "SHOW DATABASES;"

# Copier un fichier vers le conteneur
docker cp backup.sql hse_mysql_utilisateurs:/tmp/

# Copier un fichier depuis le conteneur
docker cp hse_mysql_utilisateurs:/tmp/dump.sql ./
```

### Maintenance

```powershell
# Voir l'utilisation de l'espace disque
docker system df

# Nettoyer les volumes inutilisés
docker volume prune

# Nettoyer tout (⚠️ attention)
docker system prune -a
```

---

## 💾 Sauvegarde et Restauration

### Sauvegarder une Base

```powershell
# Sauvegarder db_utilisateurs
docker exec hse_mysql_utilisateurs mysqldump -u hse_user -pHseSecure2026! db_utilisateurs > backup_utilisateurs.sql

# Sauvegarder toutes les bases
docker exec hse_mysql_utilisateurs mysqldump -u root -proot123 --all-databases > backup_all.sql
```

### Restaurer une Base

```powershell
# Restaurer db_utilisateurs
docker exec -i hse_mysql_utilisateurs mysql -u hse_user -pHseSecure2026! db_utilisateurs < backup_utilisateurs.sql
```

---

## 🔍 Résolution de Problèmes

### Problème : "Cannot connect to MySQL server"

**Solution 1** : Vérifier que les conteneurs sont démarrés
```powershell
docker-compose ps
```

**Solution 2** : Vérifier les logs
```powershell
docker-compose logs mysql-utilisateurs
```

**Solution 3** : Redémarrer les conteneurs
```powershell
docker-compose restart
```

### Problème : "Access denied for user 'hse_user'"

**Solution** : Se connecter en root et recréer l'utilisateur
```powershell
docker exec -it hse_mysql_utilisateurs mysql -u root -proot123

# Dans MySQL
DROP USER IF EXISTS 'hse_user'@'%';
CREATE USER 'hse_user'@'%' IDENTIFIED BY 'HseSecure2026!';
GRANT ALL PRIVILEGES ON db_utilisateurs.* TO 'hse_user'@'%';
FLUSH PRIVILEGES;
```

### Problème : "Table doesn't exist"

**Solution** : Hibernate doit créer les tables automatiquement au démarrage du service

Vérifier dans `application.properties` :
```properties
spring.jpa.hibernate.ddl-auto=update
```

Si les tables ne se créent pas, vérifier les logs Spring Boot pour les erreurs SQL.

### Problème : Port déjà utilisé (3307, 3308, etc.)

**Solution** : Modifier les ports dans `docker-compose.yml`

```yaml
ports:
  - "3307:3306"  # Changer 3307 en 3317 par exemple
```

---

## 🔐 Credentials

### MySQL Root
- **User** : `root`
- **Password** : `root123`

### MySQL Application
- **User** : `hse_user`
- **Password** : `HseSecure2026!`

### Application Admin
- **Username** : `admin@hse.com`
- **Password** : `admin123`

### phpMyAdmin
- **URL** : http://localhost:8090
- **Serveur** : Choisir parmi les 4 serveurs MySQL
- **User** : `hse_user` ou `root`

---

## 📁 Structure des Fichiers

```
services_repo/
├── docker-compose.yml           # Configuration Docker Compose
├── init-scripts/                # Scripts SQL d'initialisation
│   ├── 01-utilisateurs.sql      # Tables + données utilisateurs
│   ├── 02-incidents.sql         # Tables + exemples incidents
│   ├── 03-audits.sql            # Tables audits
│   └── 04-planaction.sql        # Tables plans d'action
├── service_utilisateurs/
│   └── src/main/resources/
│       └── application.properties  # Port 3307
├── incidents/
│   └── src/main/resources/
│       └── application.properties  # Port 3308
├── MicroService_Audit/
│   └── src/main/resources/
│       └── application.properties  # Port 3309
└── microService_PlanAction/
    └── src/main/resources/
        └── application.properties  # Port 3310
```

---

## ✅ Checklist de Démarrage

- [ ] Docker Desktop installé et démarré
- [ ] `docker-compose up -d` exécuté avec succès
- [ ] 5 conteneurs en cours d'exécution (4 MySQL + 1 phpMyAdmin)
- [ ] Connexion réussie à phpMyAdmin (http://localhost:8090)
- [ ] Tables créées dans chaque base
- [ ] Admin créé dans db_utilisateurs
- [ ] Les 4 fichiers `application.properties` modifiés
- [ ] Eureka Server démarré (port 8761)
- [ ] 4 microservices démarrés et connectés à MySQL
- [ ] API Gateway démarré (port 8084)
- [ ] Frontend Angular démarré (port 4200)
- [ ] Test de connexion frontend réussi

---

## 🎯 Prochaines Étapes

1. ✅ Bases de données Docker configurées
2. ⏳ Tester le démarrage de tous les services
3. ⏳ Valider la création automatique des tables
4. ⏳ Tester l'authentification (login)
5. ⏳ Tester les CRUD (incidents, audits, plans)
6. ⏳ Configurer des sauvegardes automatiques
7. ⏳ Préparer le déploiement production

---

**Créé le** : 20 janvier 2026  
**Dernière mise à jour** : 20 janvier 2026  
**Version** : 1.0
