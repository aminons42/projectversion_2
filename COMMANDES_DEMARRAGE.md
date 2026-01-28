# 🚀 COMMANDES DE DÉMARRAGE DES SERVICES HSE

**⚠️ IMPORTANT**: Le port pour `db_audits` a été changé de **3309 → 3311** (conflit détecté avec un autre processus)

## ✅ ÉTAPE 1: Bases de Données MySQL (COMPLÉTÉE)

```powershell
# Dans: d:\copiservice\services_repo
docker-compose up -d
```

**Status**: ✅ **5 conteneurs opérationnels**
- mysql-utilisateurs: `localhost:3307`
- mysql-incidents: `localhost:3308`
- mysql-audits: `localhost:3311` ⚠️ **(changé de 3309)**
- mysql-planaction: `localhost:3310`
- phpMyAdmin: `http://localhost:8090`

**Vérification**:
```powershell
docker-compose ps
```

---

## 📌 ÉTAPE 2: Eureka Server (Service Discovery)

**Port**: `8761`

**Commande** (dans un nouveau terminal PowerShell):
```powershell
cd d:\copiservice\services_repo\eureka-server
.\mvnw.cmd spring-boot:run
```

**Vérification**: 
- Ouvrir http://localhost:8761
- Vérifier le tableau de bord Eureka

**⏱️ Temps de démarrage**: ~30-40 secondes

---

## 📌 ÉTAPE 3: Service Utilisateurs

**Port**: `8080`  
**Base de données**: `db_utilisateurs` (port 3307)

**Commande** (dans un nouveau terminal PowerShell):
```powershell
cd d:\copiservice\services_repo\service_utilisateurs
.\mvnw.cmd spring-boot:run
```

**Log clé à surveiller**:
```
HikariPool-1 - Start completed.
Started ServiceUtilisateursApplication in X.XX seconds
```

**⏱️ Temps de démarrage**: ~30-50 secondes (dépend de la connexion DB)

---

## 📌 ÉTAPE 4: Service Incidents

**Port**: `8081`  
**Base de données**: `db_incidents` (port 3308)

**Commande** (dans un nouveau terminal PowerShell):
```powershell
cd d:\copiservice\services_repo\incidents
.\mvnw.cmd spring-boot:run
```

**Log clé à surveiller**:
```
HikariPool-1 - Start completed.
Started IncidentsApplication in X.XX seconds
```

**⏱️ Temps de démarrage**: ~30-50 secondes

---

## 📌 ÉTAPE 5: MicroService Audit

**Port**: `8082`  
**Base de données**: `db_audits` (port **3311** ⚠️)

**Commande** (dans un nouveau terminal PowerShell):
```powershell
cd d:\copiservice\services_repo\MicroService_Audit
.\mvnw.cmd spring-boot:run
```

**Log clé à surveiller**:
```
HikariPool-1 - Start completed.
Started MicroServiceAuditApplication in X.XX seconds
```

**⏱️ Temps de démarrage**: ~30-50 secondes

---

## 📌 ÉTAPE 6: MicroService Plan Action

**Port**: `8083`  
**Base de données**: `db_planaction` (port 3310)

**Commande** (dans un nouveau terminal PowerShell):
```powershell
cd d:\copiservice\services_repo\microService_PlanAction
.\mvnw.cmd spring-boot:run
```

**Log clé à surveiller**:
```
HikariPool-1 - Start completed.
Started MicroServicePlanActionApplication in X.XX seconds
```

**⏱️ Temps de démarrage**: ~30-50 secondes

---

## 📌 ÉTAPE 7: API Gateway

**Port**: `8084`

**Commande** (dans un nouveau terminal PowerShell):
```powershell
cd d:\copiservice\services_repo\api-gateway
.\mvnw.cmd spring-boot:run
```

**Log clé à surveiller**:
```
Started ApiGatewayApplication in X.XX seconds
```

**⏱️ Temps de démarrage**: ~20-30 secondes

---

## 📌 ÉTAPE 8: Angular Frontend

**Port**: `4200`

**Commande** (dans un nouveau terminal PowerShell):
```powershell
cd d:\copiservice\services_repo\angular_project\my-login-app
npm install  # (première fois seulement)
ng serve
```

**Vérification**: 
- Ouvrir http://localhost:4200
- Tester login avec: `admin@hse.com` / `admin123`

**⏱️ Temps de démarrage**: ~10-20 secondes (après `npm install`)

---

## 🔍 VÉRIFICATIONS FINALES

### 1. Vérifier Eureka Dashboard
```
http://localhost:8761
```

**Devrait afficher 4 instances enregistrées**:
- SERVICE-UTILISATEURS (8080)
- INCIDENTS (8081)
- MICROSERVICE-AUDIT (8082)
- MICROSERVICE-PLANACTION (8083)

### 2. Vérifier phpMyAdmin
```
http://localhost:8090
```

**Connexion**:
- Serveur: `hse_mysql_utilisateurs` (ou `incidents`, `audits`, `planaction`)
- Utilisateur: `hse_user`
- Mot de passe: `HseSecure2026!`

**Vérifier les tables créées**:
- db_utilisateurs: `utilisateur`, `role`, `user_roles`
- db_incidents: `incidents`
- db_audits: `Audits`, `check_list`, `question_audit`, `reponse_audit`, `non_comformite`
- db_planaction: `ActionPrise`, `action`, `escalade`, `suivi_action`, `verification`

### 3. Tester l'admin user
```sql
-- Dans phpMyAdmin, db_utilisateurs
SELECT * FROM utilisateur WHERE username = 'admin@hse.com';
```

**Devrait afficher**:
- user_id: 1
- nom: Admin
- prenom: System
- username: admin@hse.com
- password: (BCrypt hash de "admin123")

---

## ⚠️ RÉSOLUTION DE PROBLÈMES

### Problème: Port déjà utilisé
**Symptôme**: `bind: Only one usage of each socket address`

**Solution**:
```powershell
# Trouver le processus
netstat -ano | findstr :PORT_NUMBER

# Tuer le processus (remplacer PID)
taskkill /PID <PID> /F
```

### Problème: Connexion MySQL échoue
**Symptôme**: `Communications link failure`

**Vérifications**:
1. Docker containers en cours: `docker-compose ps`
2. Logs MySQL: `docker-compose logs mysql-utilisateurs`
3. Port correct dans `application.properties`

### Problème: Service ne s'enregistre pas dans Eureka
**Symptôme**: Service démarre mais n'apparaît pas dans Eureka dashboard

**Vérifications**:
1. Eureka est démarré **AVANT** les services
2. `eureka.client.service-url.defaultZone=http://localhost:8761/eureka/` présent
3. Logs du service: chercher "DiscoveryClient" ou "Eureka"

---

## 📊 ORDRE DE DÉMARRAGE RECOMMANDÉ

1. ✅ **Docker Compose** (bases de données) - **COMPLÉTÉ**
2. ⏳ **Eureka Server** (8761)
3. ⏳ **Service Utilisateurs** (8080) - attend ~10s après Eureka
4. ⏳ **Service Incidents** (8081) - peut démarrer en parallèle avec #3
5. ⏳ **MicroService Audit** (8082) - peut démarrer en parallèle
6. ⏳ **MicroService Plan Action** (8083) - peut démarrer en parallèle
7. ⏳ **API Gateway** (8084) - après que tous les services soient enregistrés
8. ⏳ **Angular Frontend** (4200) - à tout moment

**Temps total estimé**: ~3-5 minutes pour tout démarrer

---

## 🎯 CHECKLIST DE DÉPLOIEMENT

- [x] Docker Desktop démarré
- [x] 5 conteneurs Docker opérationnels (4 MySQL + phpMyAdmin)
- [x] Port 3311 utilisé pour db_audits (au lieu de 3309)
- [ ] Eureka Server accessible sur http://localhost:8761
- [ ] 4 services enregistrés dans Eureka
- [ ] API Gateway démarré
- [ ] Angular frontend accessible sur http://localhost:4200
- [ ] Login avec admin@hse.com / admin123 réussi
- [ ] Tables créées dans les 4 bases de données
- [ ] Aucun log d'erreur dans les services

---

## 💡 ASTUCES

### Démarrage Rapide (1 script)
Créer un fichier `start-all.ps1`:
```powershell
# start-all.ps1
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd 'd:\copiservice\services_repo\eureka-server'; .\mvnw.cmd spring-boot:run"
Start-Sleep -Seconds 15
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd 'd:\copiservice\services_repo\service_utilisateurs'; .\mvnw.cmd spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd 'd:\copiservice\services_repo\incidents'; .\mvnw.cmd spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd 'd:\copiservice\services_repo\MicroService_Audit'; .\mvnw.cmd spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd 'd:\copiservice\services_repo\microService_PlanAction'; .\mvnw.cmd spring-boot:run"
Start-Sleep -Seconds 30
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd 'd:\copiservice\services_repo\api-gateway'; .\mvnw.cmd spring-boot:run"
```

### Arrêt Rapide
```powershell
# Arrêter Docker
docker-compose down

# Arrêter tous les Java processes (attention: tue TOUS les Java!)
taskkill /F /IM java.exe
```

---

**Date de modification**: 2026-01-28  
**Status**: Étape 1/8 complétée - Bases de données opérationnelles
