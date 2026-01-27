# HSE Microservices - Quick Testing Guide

## ⚡ Quick Start (All-in-One)

### Windows PowerShell Commands

```powershell
# Terminal 1: Eureka Server
cd d:\services_repo\eureka-server
.\mvnw.cmd spring-boot:run

# Terminal 2: service_utilisateurs
cd d:\services_repo\service_utilisateurs
.\mvnw.cmd spring-boot:run

# Terminal 3: incidents
cd d:\services_repo\incidents
.\mvnw.cmd spring-boot:run

# Terminal 4: MicroService_Audit
cd d:\services_repo\MicroService_Audit
.\mvnw.cmd spring-boot:run

# Terminal 5: microService_PlanAction
cd d:\services_repo\microService_PlanAction
.\mvnw.cmd spring-boot:run

# Terminal 6: api-gateway
cd d:\services_repo\api-gateway
.\mvnw.cmd spring-boot:run

# Terminal 7: Angular Frontend
cd d:\services_repo\angular_project\my-login-app
npm start
```

## 🔍 Service Health Check

Once all services are running, verify they're registered:

**Eureka Dashboard**: http://localhost:8761
- Should show 5 services registered:
  - SERVICE-UTILISATEURS
  - INCIDENTS
  - MICROSERVICE-AUDIT
  - MICROSERVICE-PLANACTION

**API Gateway**: http://localhost:8084
- Test with: `curl http://localhost:8084/api/users`
- Should return 401 (unauthorized - need JWT)

**Frontend**: http://localhost:4200
- Should redirect to login page

---

## 🧪 Test Scenario 1: Authentication

### Step 1: Register New User
1. Go to http://localhost:4200
2. Click "S'inscrire"
3. Fill form:
   - Nom: `Dupont`
   - Prenom: `Jean`
   - Email: `jean.dupont@hse.com`
   - Password: `Password123!`
4. Click "S'inscrire"
5. Should redirect to login

### Step 2: Login
1. Email: `jean.dupont@hse.com`
2. Password: `Password123!`
3. Click "Connexion"
4. Should redirect to dashboard

**Expected Result**: ✅ Dashboard loads with KPI cards

---

## 🧪 Test Scenario 2: Dashboard KPIs

### Verify All 4 KPI Cards Display:
1. ✅ Incidents Critiques (red card)
2. ✅ Actions en Retard (orange card)
3. ✅ Conformité Globale (green card)
4. ✅ Employés Actifs (blue card)

### Verify Dashboard Data Loads:
- Recent incidents table (left column)
- My tasks table (right top)
- Upcoming audits section (right bottom)

**Expected Result**: ✅ All sections display without errors

---

## 🧪 Test Scenario 3: Incidents Management

### Create Incident
1. Click "Incidents" in sidebar
2. Click "+ Nouvel Incident"
3. Fill form:
   - Titre: `Incident de Sécurité Test`
   - Type: `Accident`
   - Description: `Test description`
   - Severity: `HIGH`
   - Statut: `OUVERT`
4. Click "Ajouter"

**Expected Result**: ✅ Incident appears in table

### Edit Incident
1. Click "✏️" button on incident row
2. Change Titre to `Incident Modifié`
3. Click "Modifier"

**Expected Result**: ✅ Table updates with new title

### Delete Incident
1. Click "🗑️" button on incident row
2. Confirm deletion

**Expected Result**: ✅ Incident removed from table

### Filter Incidents
1. Change "Type" filter
2. Change "Statut" filter
3. Table should update

**Expected Result**: ✅ Filtering works correctly

---

## 🧪 Test Scenario 4: Audits Management

### Create Audit
1. Click "Audits" in sidebar
2. Click "+ Nouvel Audit"
3. Fill form:
   - Titre: `Audit Sécurité Q1`
   - Description: `Audit sécurité trimestriel`
   - Date Début: `2026-01-15`
   - Département: `Production`
4. Click "Créer"

**Expected Result**: ✅ Audit appears with PLANIFIE status

### Start Audit
1. Locate new audit
2. Click "▶️" button
3. Status should change to EN_COURS

**Expected Result**: ✅ Status badge changes to orange

### Finish Audit
1. Click "✓" button on IN_PROGRESS audit
2. Status should change to TERMINE

**Expected Result**: ✅ Status badge changes to green

### Filter by Status
1. Use "Statut" dropdown filter
2. Select "EN_COURS"

**Expected Result**: ✅ Only EN_COURS audits display

---

## 🧪 Test Scenario 5: Plans d'Action

### Tab 1: Plans (Création)
1. Click "Plans d'Action" in sidebar
2. "Plans d'Action" tab should be active
3. Click "+ Nouveau Plan"
4. Fill form:
   - Titre: `Plan Correctif Zone A`
   - Responsable: `Jean Dupont`
   - Date Début: `2026-01-20`
   - Date Échéance: `2026-03-20`
5. Click "Créer"

**Expected Result**: ✅ Plan appears in table

### Tab 2: Actions (Création)
1. Click "Actions" tab
2. Click "+ Nouvelle Action"
3. Fill form:
   - Titre: `Installer équipement sécurité`
   - Priorité: `HAUTE`
   - Statut: `À_FAIRE`
   - Assigné à: `Jean Dupont`
4. Click "Créer"

**Expected Result**: ✅ Action appears with HAUTE priority (red)

### Tab 3: Escalades
1. Click "Escalades" tab
2. Click "+ Nouvelle Escalade"
3. Fill form and create

**Expected Result**: ✅ Escalation record created

### Tab 4: Suivis
1. Click "Suivis" tab
2. Click "+ Nouveau Suivi"
3. Create follow-up record

**Expected Result**: ✅ Follow-up created

### Tab 5: Vérifications
1. Click "Vérifications" tab
2. Click "+ Nouvelle Vérification"
3. Create verification record

**Expected Result**: ✅ Verification created

---

## 🧪 Test Scenario 6: Navigation & User Menu

### Sidebar Navigation
1. Click each sidebar item:
   - "Tableau de Bord" → Should show dashboard
   - "Incidents" → Should show incidents table
   - "Audits" → Should show audits table
   - "Plans d'Action" → Should show plans tabs

**Expected Result**: ✅ All navigation works

### User Menu
1. Click user avatar (top right)
2. Should show dropdown with:
   - "Profil"
   - "Déconnexion"
3. Click "Déconnexion"

**Expected Result**: ✅ Redirects to login page, session cleared

---

## 🔧 API Testing with cURL

### Test Authentication
```bash
# Register
curl -X POST http://localhost:8084/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"nom":"Test","prenom":"User","email":"test@test.com","password":"Test123!"}'

# Login
curl -X POST http://localhost:8084/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"Test123!"}'
# Response should include: {"token":"eyJ..."}
```

### Test Incidents (with JWT token)
```bash
# Get token first (replace TOKEN with actual token from login)
TOKEN="your_jwt_token_here"

# Create incident
curl -X POST http://localhost:8084/api/incidents \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"titre":"API Test","type":"Accident","description":"Test","severity":"HIGH","statut":"OUVERT"}'

# List incidents
curl -X GET http://localhost:8084/api/incidents \
  -H "Authorization: Bearer $TOKEN"

# Get incident stats
curl -X GET http://localhost:8084/api/incidents/stats \
  -H "Authorization: Bearer $TOKEN"
```

### Test Audits (with JWT token)
```bash
# Create audit
curl -X POST http://localhost:8084/api/audits \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"titre":"API Audit","description":"Test","dateDebut":"2026-01-15","auditeurId":1,"departement":"Test"}'

# List audits
curl -X GET http://localhost:8084/api/audits \
  -H "Authorization: Bearer $TOKEN"

# Get conformity score
curl -X GET http://localhost:8084/api/audits/conformite \
  -H "Authorization: Bearer $TOKEN"
```

---

## ❌ Common Issues & Solutions

### Issue: "Service not found" errors
**Solution**: Ensure Eureka server is running first, wait 30 seconds for other services to register

### Issue: "401 Unauthorized" when accessing protected routes
**Solution**: Make sure JWT token is being sent. Check browser console for token in localStorage

### Issue: "Cannot GET /" in browser
**Solution**: Make sure Angular dev server is running (`npm start` in my-login-app directory)

### Issue: "CORS errors" in browser console
**Solution**: Check that API Gateway is running and routes are properly configured

### Issue: Database errors on startup
**Solution**: H2 database is in-memory, should auto-create tables. Check service logs for errors

### Issue: Angular build fails
**Solution**: Ensure Node.js 18+ is installed. Run `npm install` before `npm start`

---

## 📊 Performance Baseline

Expected response times (local machine):
- Login: < 500ms
- List incidents: < 200ms
- Create incident: < 300ms
- Dashboard load: < 1s
- KPI calculations: < 100ms

---

## 🎯 Test Coverage Checklist

- [ ] User can register
- [ ] User can login
- [ ] Dashboard displays all 4 KPIs
- [ ] Can create incident
- [ ] Can edit incident
- [ ] Can delete incident
- [ ] Can filter incidents
- [ ] Can create audit
- [ ] Can start audit
- [ ] Can finish audit
- [ ] Can create plan
- [ ] Can create action
- [ ] Can create escalade
- [ ] Can create suivi
- [ ] Can create verification
- [ ] Navigation between pages works
- [ ] User menu shows and works
- [ ] Logout redirects to login
- [ ] Protected routes redirect to login when not authenticated

---

## 🚨 Emergency Contacts

- **Frontend Issues**: Check Angular console (F12) for TypeScript errors
- **Backend Issues**: Check terminal where services are running for stack traces
- **Gateway Issues**: Check http://localhost:8084 is accessible
- **Eureka Issues**: Check http://localhost:8761 for service registration status

---

**Testing Guide v1.0** | 2026-01-06
