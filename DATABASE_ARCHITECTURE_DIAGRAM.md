# 🗄️ Architecture des Bases de Données - HSE Microservices

## 📊 Vue d'Ensemble de l'Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                        MYSQL SERVER (Port 3306)                     │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐ │
│  │ db_utilisateurs  │  │   db_incidents   │  │    db_audits     │ │
│  ├──────────────────┤  ├──────────────────┤  ├──────────────────┤ │
│  │ 3 tables         │  │ 1 table          │  │ 5 tables         │ │
│  │                  │  │                  │  │                  │ │
│  │ • utilisateur    │  │ • incidents      │  │ • audits         │ │
│  │ • role           │  │                  │  │ • check_list     │ │
│  │ • user_roles     │  │                  │  │ • question_audit │ │
│  │                  │  │                  │  │ • reponse_audit  │ │
│  │                  │  │                  │  │ • non_comformite │ │
│  └──────────────────┘  └──────────────────┘  └──────────────────┘ │
│                                                                     │
│  ┌──────────────────┐                                              │
│  │  db_planaction   │                                              │
│  ├──────────────────┤                                              │
│  │ 5 tables         │                                              │
│  │                  │                                              │
│  │ • action_prise   │                                              │
│  │ • action         │                                              │
│  │ • escalade       │                                              │
│  │ • suivi_action   │                                              │
│  │ • verification   │                                              │
│  └──────────────────┘                                              │
│                                                                     │
│  Utilisateur: hse_user                                             │
│  Total: 14 tables                                                  │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 📦 BASE 1 : db_utilisateurs

### Diagramme de Relations

```
┌─────────────────────┐
│    utilisateur      │
├─────────────────────┤
│ PK user_id (BIGINT) │
│    nom              │
│    prenom           │
│ UK username         │◄───────┐
│    password         │        │
│    created_at       │        │
│    updated_at       │        │
└─────────────────────┘        │
         │                     │
         │ Many-to-Many        │
         ▼                     │
┌─────────────────────┐        │
│    user_roles       │        │
├─────────────────────┤        │
│ PK,FK user_id       │────────┘
│ PK,FK role_id       │────────┐
└─────────────────────┘        │
                               │
                               ▼
                    ┌─────────────────────┐
                    │        role         │
                    ├─────────────────────┤
                    │ PK role_id (INT)    │
                    │ UK nom              │
                    └─────────────────────┘
```

### Données Initiales

| Table | Nombre de lignes | Données |
|-------|------------------|---------|
| **utilisateur** | 1 | admin@hse.com (password: admin123) |
| **role** | 4 | ROLE_ADMIN, ROLE_MANAGER, ROLE_AUDITEUR, ROLE_USER |
| **user_roles** | 1 | admin ↔ ROLE_ADMIN |

### Statistiques Exposées

- **GET /api/users/stats/actifs** → `SELECT COUNT(*) FROM utilisateur`

---

## 📦 BASE 2 : db_incidents

### Diagramme de Relations

```
┌─────────────────────────┐
│       incidents         │
├─────────────────────────┤
│ PK id (BIGINT)          │
│    date                 │
│    description (TEXT)   │
│    location             │
│    creator_name         │
│    statut               │ ← ENUM: OUVERT, EN_COURS, RESOLU, CLOS
│    type_incident        │ ← ENUM: ACCIDENT_TRAVAIL, PRESQU_ACCIDENT, etc.
│    created_at           │
│    updated_at           │
└─────────────────────────┘
```

### Valeurs ENUM

**statut** :
- `OUVERT`
- `EN_COURS`
- `RESOLU`
- `CLOS`

**type_incident** :
- `ACCIDENT_TRAVAIL`
- `PRESQU_ACCIDENT`
- `INCIDENT_ENVIRONNEMENTAL`
- `INCIDENT_SECURITE`
- `VIOLATION_PROCEDURE`
- `AUTRE`

### Statistiques Exposées

- **GET /api/incidents/stats** → `SELECT COUNT(*) FROM incidents WHERE type_incident = 'ACCIDENT_TRAVAIL'`

---

## 📦 BASE 3 : db_audits

### Diagramme de Relations

```
                    ┌─────────────────────┐
                    │     check_list      │
                    ├─────────────────────┤
                    │ PK template_id      │
                    │    nom              │
    ┌───────────────┤    type_audit       │
    │               │    actif            │
    │               │    version          │
    │               └─────────────────────┘
    │                         │
    │                         │ 1:N
    │                         ▼
    │               ┌─────────────────────┐
    │               │   question_audit    │
    │               ├─────────────────────┤
    │               │ PK question_id      │
    │               │    libelle          │
    │               │    type_reponse     │
    │           ┌───┤ FK categorie_id     │
    │           │   │    criticite        │
    │           │   │    ponderation      │
    │           │   └─────────────────────┘
    │           │             │
    │           │             │ 1:N
    │           │             ▼
    │           │   ┌─────────────────────┐
    │           │   │   reponse_audit     │◄───┐
    │           │   ├─────────────────────┤    │
    │           │   │ PK reponse_id       │    │
    │           │   │    valeur           │    │
    │           │   │    conforme         │    │
    │           │   │ FK audit_id         │────┤
    │           │   │ FK question_id      │    │
    │           │   │    utilisateur_id   │────┼───► Référence vers db_utilisateurs.user_id
    │           │   └─────────────────────┘    │
    │           │             │                │
    │           │             │ 1:1            │
    │           │             ▼                │
    │           │   ┌─────────────────────┐    │
    │           │   │  non_comformite     │    │
    │           │   ├─────────────────────┤    │
    │           │   │ PK id               │    │
    │           │   │    description      │    │
    │           │   │    gravite          │    │
    │           │   │ FK audit_id         │────┤
    │           │   │ FK reponse_id       │────┘
    │           │   │    plan_action_id   │───────► Référence vers db_planaction.action_prise.id
    │           │   │    statut           │
    │           │   └─────────────────────┘
    │           │
    │           │
    ▼           ▼
┌─────────────────────┐
│       audits        │
├─────────────────────┤
│ PK audit_id         │
│    titre            │
│    type_audit       │
│    statut           │ ← ENUM: PLANIFIE, EN_COURS, TERMINE
│    auditeur_id      │───────► Référence vers db_utilisateurs.user_id
│    score_global     │
│    taux_conformite  │
│ FK checklist_id     │
└─────────────────────┘
```

### Statistiques Exposées

- **GET /api/audits/conformite** → `SELECT AVG(taux_conformite) FROM audits WHERE statut = 'TERMINE'`

---

## 📦 BASE 4 : db_planaction

### Diagramme de Relations

```
┌──────────────────────┐
│   action_prise       │ (PlanAction)
├──────────────────────┤
│ PK id                │
│    titre             │
│    type              │ ← ENUM: CORRECTIF, PREVENTIF, AMELIORATION
│    source            │ ← ENUM: INCIDENT, AUDIT, INSPECTION, etc.
│    source_id         │───────► ID de incident ou audit
│    responsable_id    │───────► Référence vers db_utilisateurs.user_id
│    statut            │
│    priorite          │
└──────────────────────┘
         │
         │ 1:N
         ▼
┌──────────────────────┐
│       action         │
├──────────────────────┤
│ PK id                │
│    description       │
│    avancement        │ (0-100%)
│    statut            │ ← ENUM: A_FAIRE, EN_COURS, TERMINEE, etc.
│    priorite          │
│    responsable_id    │───────► Référence vers db_utilisateurs.user_id
│ FK plan_action_id    │◄───────┐
└──────────────────────┘        │
         │                      │
         ├──────────────────────┤
         │                      │
         ▼                      │
┌──────────────────────┐        │
│    suivi_action      │        │
├──────────────────────┤        │
│ PK id                │        │
│    date              │        │
│    commentaire       │        │
│    avancement        │        │
│ FK action_id         │────────┤
│    utilisateur_id    │─────┐  │
└──────────────────────┘     │  │
                             │  │
         ▼                   │  │
┌──────────────────────┐     │  │
│    verification      │     │  │
├──────────────────────┤     │  │
│ PK id                │     │  │
│    date_verification │     │  │
│    resultat          │     │  │
│    efficace          │     │  │
│ FK action_id         │─────┼──┤
│    verificateur_id   │─────┘  │
└──────────────────────┘        │
                                │
         ▼                      │
┌──────────────────────┐        │
│      escalade        │        │
├──────────────────────┤        │
│ PK id                │        │
│    motif             │        │
│    niveau            │        │
│    statut            │        │
│ FK plan_action_id    │────────┘
│ FK action_id         │─────► (peut référencer action)
│    traite_par        │─────► Référence vers db_utilisateurs.user_id
└──────────────────────┘
```

### Statistiques Exposées

- **GET /api/actions/stats/retard** → `SELECT COUNT(*) FROM action WHERE statut NOT IN ('TERMINEE', 'VERIFIEE') AND date_echeance < NOW()`

---

## 🔗 Relations Inter-Bases (Microservices)

### Références Croisées via IDs

```
┌─────────────────┐
│db_utilisateurs  │
│                 │
│  utilisateur    │
│  • user_id ─────┼───┐
└─────────────────┘   │
                      │
        ┌─────────────┴──────────────┬──────────────────┐
        │                            │                  │
        ▼                            ▼                  ▼
┌─────────────────┐      ┌─────────────────┐  ┌─────────────────┐
│   db_audits     │      │ db_planaction   │  │  db_incidents   │
│                 │      │                 │  │                 │
│  audits         │      │  action_prise   │  │  incidents      │
│  • auditeur_id  │      │  • responsable  │  │  • creator_name │
│                 │      │                 │  │    (String)     │
│  reponse_audit  │      │  action         │  └─────────────────┘
│  • utilisateur  │      │  • responsable  │           │
│                 │      │                 │           │
│  non_comformite │      │  verification   │           │
│  • responsable  │      │  • verificateur │           │
│                 │◄─────┤                 │           │
│  non_comformite │      │  escalade       │           │
│  • plan_action  │      │  • traite_par   │           │
│    _id          │      │                 │           │
└─────────────────┘      │  action_prise   │───────────┘
                         │  • source_id    │
                         │    (incident_id │
                         │     ou audit_id)│
                         └─────────────────┘
```

### Types de Relations

| Depuis | Vers | Champ | Type | Description |
|--------|------|-------|------|-------------|
| **audits** | utilisateurs | auditeur_id | BIGINT | ID de l'auditeur |
| **reponse_audit** | utilisateurs | utilisateur_id | BIGINT | ID du répondant |
| **non_comformite** | utilisateurs | responsable_id | BIGINT | ID du responsable |
| **non_comformite** | planaction | plan_action_id | BIGINT | ID du plan d'action correctif |
| **action_prise** | utilisateurs | responsable_id | BIGINT | ID du responsable du plan |
| **action_prise** | incidents/audits | source_id | BIGINT | ID de la source (selon `source`) |
| **action** | utilisateurs | responsable_id | BIGINT | ID du responsable de l'action |
| **verification** | utilisateurs | verificateur_id | BIGINT | ID du vérificateur |
| **escalade** | utilisateurs | traite_par | BIGINT | ID du traiteur |

---

## 📈 Statistiques de Taille des Bases

### Estimation par Environnement

| Base | Tables | Dev (MB) | Test (MB) | Prod (estimé) |
|------|--------|----------|-----------|---------------|
| **db_utilisateurs** | 3 | < 1 | 2-5 | 10-50 |
| **db_incidents** | 1 | < 1 | 5-10 | 50-200 |
| **db_audits** | 5 | 1-2 | 10-20 | 100-500 |
| **db_planaction** | 5 | 1-2 | 10-20 | 100-500 |
| **TOTAL** | 14 | ~5 | ~50 | ~1000 (1 GB) |

### Commande de Vérification

```sql
SELECT 
    table_schema AS "Base de données",
    COUNT(*) AS "Nombre de tables",
    ROUND(SUM(data_length + index_length) / 1024 / 1024, 2) AS "Taille (MB)"
FROM information_schema.tables
WHERE table_schema IN ('db_utilisateurs', 'db_incidents', 'db_audits', 'db_planaction')
GROUP BY table_schema
WITH ROLLUP;
```

---

## 🎯 Index et Optimisations

### Index Critiques Créés

| Table | Index | Colonnes | Raison |
|-------|-------|----------|--------|
| **utilisateur** | idx_username | username | Recherche par login (très fréquent) |
| **incidents** | idx_statut | statut | Filtrage par statut |
| **incidents** | idx_type | type_incident | Filtrage par type |
| **incidents** | idx_date | date | Tri chronologique |
| **audits** | idx_statut | statut | Filtrage dashboard |
| **audits** | idx_auditeur | auditeur_id | Mes audits |
| **reponse_audit** | idx_audit | audit_id | Réponses d'un audit |
| **reponse_audit** | idx_conforme | conforme | Calcul conformité |
| **action** | idx_statut | statut | Filtrage actions |
| **action** | idx_date_echeance | date_echeance | Calcul retards |
| **action** | idx_plan | plan_action_id | Actions d'un plan |

### Requêtes Optimisées

```sql
-- Incidents critiques (dashboard KPI)
SELECT COUNT(*) FROM incidents 
WHERE type_incident = 'ACCIDENT_TRAVAIL' 
-- Utilise: idx_type

-- Actions en retard (dashboard KPI)
SELECT COUNT(*) FROM action 
WHERE statut NOT IN ('TERMINEE', 'VERIFIEE') 
  AND date_echeance < NOW()
-- Utilise: idx_statut + idx_date_echeance

-- Taux de conformité global
SELECT AVG(taux_conformite) 
FROM audits 
WHERE statut = 'TERMINE'
-- Utilise: idx_statut
```

---

## 🔐 Sécurité des Données

### Privilèges de l'Utilisateur

```sql
-- Utilisateur: hse_user
-- Privilèges: ALL sur les 4 bases uniquement
-- Accès: localhost seulement
-- Pas d'accès: mysql, information_schema, performance_schema
```

### Données Sensibles

| Table | Colonne | Type | Protection |
|-------|---------|------|------------|
| **utilisateur** | password | VARCHAR(255) | ✅ BCrypt hash (force 10) |
| **utilisateur** | username | VARCHAR(100) | ⚠️ Email en clair (UNIQUE) |

### Recommandations Production

1. **Chiffrement au repos** : Activer MySQL encryption
2. **SSL/TLS** : Forcer connexions sécurisées
3. **Audit logs** : Activer general_log en prod
4. **Backups** : mysqldump quotidien + binary logs
5. **Rotation mots de passe** : Tous les 90 jours

---

## 🛠️ Maintenance

### Scripts de Maintenance

```sql
-- Nettoyer les audits terminés > 1 an
DELETE FROM audits 
WHERE statut = 'TERMINE' 
  AND date_fin < DATE_SUB(NOW(), INTERVAL 1 YEAR);

-- Archiver les incidents clos > 6 mois
INSERT INTO incidents_archive 
SELECT * FROM incidents 
WHERE statut = 'CLOS' 
  AND updated_at < DATE_SUB(NOW(), INTERVAL 6 MONTH);

DELETE FROM incidents 
WHERE statut = 'CLOS' 
  AND updated_at < DATE_SUB(NOW(), INTERVAL 6 MONTH);

-- Optimiser les tables
OPTIMIZE TABLE utilisateur, incidents, audits, action_prise, action;
```

### Monitoring

```sql
-- Vérifier la santé des tables
CHECK TABLE utilisateur, role, user_roles;
CHECK TABLE incidents;
CHECK TABLE audits, check_list, question_audit;
CHECK TABLE action_prise, action, escalade;

-- Analyser les performances
ANALYZE TABLE incidents, audits, action;
```

---

**Document créé le : 20 janvier 2026**  
**Version : 1.0**
