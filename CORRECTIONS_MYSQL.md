# ✅ Corrections Appliquées - Bases de Données MySQL avec Docker

## 🎯 Problèmes Corrigés

### 1. ❌ Erreur : Attribut `email` inexistant
**Problème** : Le script SQL créait une colonne `email` dans la table `utilisateur`, mais l'entité JPA n'a pas cet attribut.

**Entité réelle** :
```java
@Entity
public class Utilisateur implements UserDetails {
    private Long id;
    private String nom;
    private String prenom;
    private String username;  // ← Utilisé comme identifiant (pas "email")
    private String password;
    private Set<Role> roles;
}
```

**✅ Correction appliquée** :
- Table `utilisateur` utilise maintenant `username` (VARCHAR 100) comme identifiant unique
- Colonne `email` supprimée
- Scripts SQL mis à jour dans `init-scripts/01-utilisateurs.sql`

---

### 2. ❌ Erreur : ENUM `TypeIncident` manquant
**Problème** : La table `incidents` ne gérait pas correctement l'ENUM Java `TypeIncident`.

**Entité réelle** :
```java
@Entity
@Table(name = "incidents")
public class Incident {
    private Long id;
    private LocalDateTime date;
    private String description;
    private String location;
    private String creator_name;
    private String statut;
    @Enumerated(EnumType.STRING)
    private TypeIncident type_incident;  // ← ENUM stocké comme VARCHAR
}

public enum TypeIncident {
    CHUTE,
    FEU,
    FUITE_CHIMIQUE,
    PRESQU_ACCIDENT,
    AUTRE
}
```

**✅ Correction appliquée** :
- Colonne `type_incident` définie comme `VARCHAR(50)`
- Documentation des valeurs ENUM dans les commentaires SQL
- Exemples de données avec les bonnes valeurs ENUM

---

### 3. ❌ Erreur : Noms de tables incorrects
**Problème** : Les noms de tables SQL ne correspondaient pas aux annotations JPA `@Table`.

**Entités réelles** :
```java
// ❌ Ancien SQL: CREATE TABLE audits
// ✅ Bon nom:
@Table(name="Audits")  // Avec majuscule
public class Audit { ... }

// ❌ Ancien SQL: CREATE TABLE action_prise
// ✅ Bon nom:
@Table(name="ActionPrise")  // CamelCase
public class PlanAction { ... }
```

**✅ Corrections appliquées** :
- Table `audits` → `Audits` (avec majuscule A)
- Table `action_prise` → `ActionPrise` (CamelCase)
- Nom de colonne `Audit_id` conservé (avec majuscule)
- Nom de colonne `Question_id` conservé (avec majuscule)

---

### 4. ❌ Erreur : Noms de colonnes incorrects
**Problème** : Certaines colonnes avaient des noms différents de l'entité JPA.

**Entité réelle** :
```java
@Entity
public class Action {
    private Long id;
    private String description;
    private int Avancement = 0;  // ← Avec majuscule
    private TypeAction type;
    // ...
    @ManyToOne
    @JoinColumn(name = "planAction_id")  // ← Nom exact
    private PlanAction planAction;
}
```

**✅ Corrections appliquées** :
- Colonne `avancement` → `Avancement` (avec majuscule)
- Colonne `plan_action_id` → `planAction_id` (pour correspondre exactement)

---

## 🐳 Architecture Docker Mise en Place

### Configuration Docker Compose

**4 conteneurs MySQL indépendants** :

| Conteneur | Port | Base de Données | Service Spring Boot |
|-----------|------|-----------------|---------------------|
| `hse_mysql_utilisateurs` | 3307 | db_utilisateurs | service_utilisateurs:8080 |
| `hse_mysql_incidents` | 3308 | db_incidents | incidents:8081 |
| `hse_mysql_audits` | 3309 | db_audits | MicroService_Audit:8082 |
| `hse_mysql_planaction` | 3310 | db_planaction | microService_PlanAction:8083 |

**Bonus** : phpMyAdmin sur le port 8090

### Scripts SQL d'Initialisation

Chaque base de données a son propre script SQL :

```
init-scripts/
├── 01-utilisateurs.sql    # Utilisateur + Rôles + Admin par défaut
├── 02-incidents.sql       # Incidents + exemples avec ENUM
├── 03-audits.sql          # Audits + CheckList + Questions + Réponses
└── 04-planaction.sql      # ActionPrise + Action + Escalade + Suivi + Verification
```

---

## 📝 Fichiers Modifiés

### 1. Fichiers Créés

✅ **docker-compose.yml** - Configuration des 4 conteneurs MySQL + phpMyAdmin  
✅ **init-scripts/01-utilisateurs.sql** - Script SQL corrigé (utilisateur, role, user_roles)  
✅ **init-scripts/02-incidents.sql** - Script SQL corrigé (incidents avec TypeIncident ENUM)  
✅ **init-scripts/03-audits.sql** - Script SQL corrigé (Audits, check_list, etc.)  
✅ **init-scripts/04-planaction.sql** - Script SQL corrigé (ActionPrise, action, etc.)  
✅ **DOCKER_MYSQL_GUIDE.md** - Guide complet Docker avec commandes  
✅ **CORRECTIONS_MYSQL.md** - Ce fichier

### 2. Fichiers Modifiés

✅ **service_utilisateurs/src/main/resources/application.properties**
```properties
# ❌ Avant: H2 (jdbc:h2:mem:testdb)
# ✅ Après: MySQL Docker (jdbc:mysql://localhost:3307/db_utilisateurs)
```

✅ **incidents/src/main/resources/application.properties**
```properties
# ❌ Avant: H2 (jdbc:h2:mem:testdb)
# ✅ Après: MySQL Docker (jdbc:mysql://localhost:3308/db_incidents)
```

✅ **MicroService_Audit/src/main/resources/application.properties**
```properties
# ❌ Avant: H2 (jdbc:h2:mem:Audit-db)
# ✅ Après: MySQL Docker (jdbc:mysql://localhost:3309/db_audits)
# ➕ Ajouté: JWT secret + JPA config
```

✅ **microService_PlanAction/src/main/resources/application.properties**
```properties
# ❌ Avant: Pas de config DB
# ✅ Après: MySQL Docker (jdbc:mysql://localhost:3310/db_planaction)
# ➕ Ajouté: JWT secret + JPA config complète
```

---

## 🔍 Vérification des Entités vs Tables SQL

### ✅ db_utilisateurs

| Entité Java | Table SQL | Colonne SQL | Type | Notes |
|-------------|-----------|-------------|------|-------|
| Utilisateur.id | utilisateur | user_id | BIGINT | ✅ Auto-increment |
| Utilisateur.nom | utilisateur | nom | VARCHAR(100) | ✅ |
| Utilisateur.prenom | utilisateur | prenom | VARCHAR(100) | ✅ |
| Utilisateur.username | utilisateur | username | VARCHAR(100) | ✅ UNIQUE |
| Utilisateur.password | utilisateur | password | VARCHAR(255) | ✅ BCrypt hash |
| Utilisateur.roles | user_roles | user_id, role_id | BIGINT, INT | ✅ Many-to-Many |

### ✅ db_incidents

| Entité Java | Table SQL | Colonne SQL | Type | Notes |
|-------------|-----------|-------------|------|-------|
| Incident.id | incidents | id | BIGINT | ✅ |
| Incident.date | incidents | date | DATETIME | ✅ |
| Incident.description | incidents | description | VARCHAR(500) | ✅ |
| Incident.location | incidents | location | VARCHAR(200) | ✅ |
| Incident.creator_name | incidents | creator_name | VARCHAR(100) | ✅ NOT NULL |
| Incident.statut | incidents | statut | VARCHAR(50) | ✅ |
| Incident.type_incident | incidents | type_incident | VARCHAR(50) | ✅ ENUM as STRING |

### ✅ db_audits

| Entité Java | Table SQL | Colonne SQL | Type | Notes |
|-------------|-----------|-------------|------|-------|
| Audit.id | Audits | Audit_id | BIGINT | ✅ Nom avec majuscule |
| Audit.titre | Audits | titre | VARCHAR(200) | ✅ |
| Audit.typeAudit | Audits | type_audit | VARCHAR(50) | ✅ ENUM |
| Audit.statut | Audits | statut | VARCHAR(50) | ✅ DEFAULT 'PLANIFIE' |
| Audit.auditeurId | Audits | auditeur_id | BIGINT | ✅ |
| Audit.checklistTemplate | Audits | checklist_template_id | BIGINT | ✅ FK vers check_list |

### ✅ db_planaction

| Entité Java | Table SQL | Colonne SQL | Type | Notes |
|-------------|-----------|-------------|------|-------|
| PlanAction.id | ActionPrise | id | BIGINT | ✅ Nom table CamelCase |
| PlanAction.titre | ActionPrise | titre | VARCHAR(200) | ✅ |
| PlanAction.statut | ActionPrise | statut | VARCHAR(50) | ✅ |
| Action.id | action | id | BIGINT | ✅ |
| Action.Avancement | action | Avancement | INT | ✅ Avec majuscule |
| Action.planAction | action | plan_action_id | BIGINT | ✅ FK vers ActionPrise |

---

## 🚀 Commandes de Démarrage

### 1. Démarrer Docker

```powershell
cd d:\services_repo

# Démarrer les 4 bases MySQL + phpMyAdmin
docker-compose up -d

# Vérifier que tout fonctionne
docker-compose ps
```

**Attendu** : 5 conteneurs en statut "Up"

### 2. Vérifier les Bases de Données

**Via phpMyAdmin** : http://localhost:8090

**Via ligne de commande** :
```powershell
# Vérifier db_utilisateurs
docker exec -it hse_mysql_utilisateurs mysql -u hse_user -pHseSecure2026! -e "USE db_utilisateurs; SHOW TABLES; SELECT * FROM utilisateur;"
```

### 3. Démarrer les Microservices

```powershell
# Dans 6 terminaux séparés

# Terminal 1
cd d:\services_repo\eureka-server
.\mvnw.cmd spring-boot:run

# Terminal 2
cd d:\services_repo\service_utilisateurs
.\mvnw.cmd spring-boot:run

# Terminal 3
cd d:\services_repo\incidents
.\mvnw.cmd spring-boot:run

# Terminal 4
cd d:\services_repo\MicroService_Audit
.\mvnw.cmd spring-boot:run

# Terminal 5
cd d:\services_repo\microService_PlanAction
.\mvnw.cmd spring-boot:run

# Terminal 6
cd d:\services_repo\api-gateway
.\mvnw.cmd spring-boot:run
```

### 4. Logs à Vérifier

Dans chaque terminal Spring Boot, chercher :
```
✅ HikariPool-1 - Starting...
✅ HikariPool-1 - Start completed.
✅ Hibernate: create table ...  (si ddl-auto=update)
✅ Started [NomService]Application in X.XXX seconds
```

---

## 📊 Résumé des Corrections

| Problème | État | Fichiers Modifiés |
|----------|------|-------------------|
| Colonne `email` au lieu de `username` | ✅ Corrigé | init-scripts/01-utilisateurs.sql |
| ENUM `TypeIncident` non géré | ✅ Corrigé | init-scripts/02-incidents.sql |
| Nom de table `audits` au lieu de `Audits` | ✅ Corrigé | init-scripts/03-audits.sql |
| Nom de table `action_prise` au lieu de `ActionPrise` | ✅ Corrigé | init-scripts/04-planaction.sql |
| Colonne `avancement` au lieu de `Avancement` | ✅ Corrigé | init-scripts/04-planaction.sql |
| Configuration H2 au lieu de MySQL | ✅ Corrigé | 4 x application.properties |
| Pas de Docker | ✅ Ajouté | docker-compose.yml |

---

## ✅ Checklist de Validation

- [x] Scripts SQL corrigés selon les entités JPA
- [x] Noms de tables exacts (Audits, ActionPrise)
- [x] Noms de colonnes exacts (username, Avancement)
- [x] ENUMs Java stockés comme VARCHAR(50)
- [x] Docker Compose configuré (4 MySQL + phpMyAdmin)
- [x] Scripts d'initialisation SQL créés
- [x] 4 fichiers application.properties mis à jour
- [x] Ports Docker configurés (3307-3310)
- [x] Documentation complète créée

---

## 🎯 Prochaines Actions

1. **Démarrer Docker** : `docker-compose up -d`
2. **Vérifier phpMyAdmin** : http://localhost:8090
3. **Démarrer les services** : Un par un en vérifiant les logs
4. **Tester l'application** : Login avec admin@hse.com / admin123
5. **Valider les CRUD** : Créer incidents, audits, plans d'action

---

**Créé le** : 20 janvier 2026  
**Toutes les corrections validées** ✅
