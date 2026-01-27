# 📚 Guide de Découverte du Code - HSE Synergy

## 🎯 Vue d'ensemble
Application Angular 17 avec 6 microservices Spring Boot pour la gestion HSE (Hygiène, Sécurité, Environnement).

---

## 📖 Parcours d'Apprentissage Recommandé

### **ÉTAPE 1 : Architecture de Base** (30 min)

#### 1.1 Fichier de configuration principal
📁 `src/environments/environment.ts`
```typescript
// Regardez TOUTES les URLs des microservices (11 routes)
gatewayUrl, authUrl, usersUrl, incidentsUrl, auditsUrl...
```
**💡 Ce qu'il faut comprendre :**
- Chaque URL pointe vers un microservice Spring Boot
- `localhost:8084` = API Gateway (point d'entrée unique)
- Les autres ports (8081, 8082...) sont les microservices individuels

---

#### 1.2 Point d'entrée de l'application
📁 `src/main.ts` → Lance l'application Angular
📁 `src/app/app.component.ts` → Composant racine
📁 `src/app/app.routes.ts` → **COMMENCEZ ICI !**

```typescript
// Routes de l'application (navigation)
/login          → Page de connexion
/dashboard      → Tableau de bord (avec authGuard)
/incidents      → Liste des incidents
/audits         → Liste des audits
/utilisateurs   → Gestion des utilisateurs (ADMIN)
```

**💡 Concept clé : authGuard**
- Protège les routes
- Vérifie si l'utilisateur est connecté
- Redirige vers /login si non authentifié

---

### **ÉTAPE 2 : Système d'Authentification** (45 min)

#### 2.1 Modèles de données
📁 `src/app/core/models/user.model.ts`
```typescript
export interface User {
  id: number;
  username: string;
  password?: string;  // Optionnel pour la sécurité
  roles: Role[];      // ADMIN ou EMPLOYEE
  actif: boolean;     // Compte actif/inactif
}
```

📁 `src/app/core/models/jwt-response.model.ts`
```typescript
// Réponse du backend après login
{
  token: string,      // JWT token
  type: "Bearer",
  username: string,
  roles: string[]
}
```

#### 2.2 Service d'authentification
📁 `src/app/core/services/auth.service.ts`

**Lisez dans cet ordre :**
1. `login()` → Envoie username/password au backend
2. `saveToken()` → Stocke le JWT dans localStorage
3. `isAuthenticated()` → Vérifie si le token existe
4. `logout()` → Supprime le token

**💡 Flow d'authentification :**
```
User → login.ts → AuthService.login() → Backend
                                       ↓
                          Backend renvoie JWT token
                                       ↓
                   AuthService.saveToken(token)
                                       ↓
                         Redirect vers /dashboard
```

#### 2.3 Intercepteur HTTP
📁 `src/app/core/interceptors/token.interceptor.ts`

**Rôle :** Ajoute automatiquement le token à TOUTES les requêtes HTTP
```typescript
// Avant : GET /gateway/incidents
// Après : GET /gateway/incidents
//         Header: Authorization: Bearer eyJhbGc...
```

#### 2.4 Guard de protection
📁 `src/app/core/guards/auth.guard.ts`

**Lisez comment il protège les routes :**
```typescript
canActivate() {
  if (isAuthenticated()) {
    return true;  // Laisse passer
  } else {
    navigate('/login');  // Redirige
    return false;
  }
}
```

---

### **ÉTAPE 3 : Page de Login** (30 min)

📁 `src/app/login/login.ts` (TypeScript)
📁 `src/app/login/login.html` (Template)
📁 `src/app/login/login.css` (Styles)

**Structure d'un composant Angular :**
```typescript
@Component({
  selector: 'app-login',          // <app-login></app-login>
  standalone: true,               // Pas besoin de module
  imports: [FormsModule],         // Pour [(ngModel)]
  templateUrl: './login.html'
})
export class Login {
  // DONNÉES
  username = '';
  password = '';
  
  // SERVICES INJECTÉS
  private authService = inject(AuthService);
  
  // MÉTHODES
  onSubmit() {
    this.authService.login(...)
  }
}
```

**💡 Regardez dans le HTML :**
- `[(ngModel)]="username"` → Two-way binding
- `(ngSubmit)="onSubmit()"` → Appelle la méthode onSubmit()
- `@if (errorMessage)` → Affichage conditionnel (Angular 17)

---

### **ÉTAPE 4 : Gestion des Erreurs** (30 min)

#### 4.1 Service de notifications
📁 `src/app/core/services/notification.service.ts`

**Concept : Signals (Angular 17)**
```typescript
notifications = signal<Notification[]>([]);

success(message) {
  // Ajoute une notification success
  this.notifications.update(n => [...n, newNotif]);
  
  // Auto-supprime après 3 secondes
  setTimeout(() => removeNotif(), 3000);
}
```

#### 4.2 Intercepteur d'erreurs
📁 `src/app/core/interceptors/error.interceptor.ts`

**Gère automatiquement les erreurs HTTP :**
- 401 → Session expirée, redirect login
- 403 → Accès refusé
- 404 → Ressource non trouvée
- 500 → Erreur serveur
- 0 → Pas de connexion réseau

#### 4.3 Toast UI
📁 `src/app/app.component.html` (lignes 1-20)

**Affichage des notifications :**
```html
@for (notif of notifications(); track notif.id) {
  <div class="toast toast-{{notif.type}}">
    {{ notif.message }}
  </div>
}
```

---

### **ÉTAPE 5 : Tableau de Bord** (45 min)

📁 `src/app/dashboard/dashboard.ts`
📁 `src/app/dashboard/dashboard.html`

**Structure du dashboard :**
```
┌─────────────────────────────────────┐
│ Sidebar                │ Header     │
│ - Dashboard            │ User Menu  │
│ - Incidents            ├────────────│
│ - Audits               │            │
│ - Plans d'Action       │  KPI Cards │
│ - Non-Conformités      │            │
│ - Templates            │            │
│ - Utilisateurs (admin) │            │
└────────────────────────┴────────────┘
```

**💡 Concepts clés :**
1. `ngOnInit()` → Chargement des KPI au démarrage
2. `isAdmin()` → Affiche le menu Utilisateurs si admin
3. `navigateTo(route)` → Navigation programmatique
4. `toggleUserMenu()` → Affiche/cache le menu déroulant

---

### **ÉTAPE 6 : CRUD Incidents** (1h)

📁 `src/app/core/models/incident.model.ts`
```typescript
export interface Incident {
  id: number;
  titre: string;
  description: string;
  gravite: 'FAIBLE' | 'MOYENNE' | 'ELEVEE';
  dateIncident: string;
  statut: 'OUVERT' | 'EN_COURS' | 'RESOLU';
}
```

📁 `src/app/core/services/incident.service.ts`
```typescript
// Tous les appels HTTP vers le backend
getAllIncidents()      → GET /gateway/incidents
getIncidentById(id)    → GET /gateway/incidents/1
createIncident(data)   → POST /gateway/incidents
updateIncident(id, data) → PUT /gateway/incidents/1
deleteIncident(id)     → DELETE /gateway/incidents/1
```

📁 `src/app/incidents/incidents.ts`

**Flow CRUD complet :**
```typescript
// 1. LECTURE (au chargement)
ngOnInit() {
  this.incidentService.getAllIncidents()
    .subscribe(incidents => this.incidents.set(incidents));
}

// 2. CRÉATION
saveIncident() {
  this.incidentService.createIncident(formData)
    .subscribe(() => {
      this.notificationService.success('Créé !');
      this.loadIncidents();  // Refresh
      this.closeModal();
    });
}

// 3. MODIFICATION
editIncident(incident) {
  this.editingIncident.set(incident);
  this.formData = {...incident};  // Copie les données
  this.showModal.set(true);
}

// 4. SUPPRESSION
deleteIncident(id) {
  if (confirm('Supprimer ?')) {
    this.incidentService.deleteIncident(id)
      .subscribe(() => this.loadIncidents());
  }
}
```

**💡 Pattern Modal :**
```typescript
showModal = signal(false);       // Contrôle l'affichage
editingIncident = signal(null);  // null = création, object = modification

openCreateModal() {
  this.resetForm();
  this.editingIncident.set(null);
  this.showModal.set(true);
}

openEditModal(incident) {
  this.formData = {...incident};
  this.editingIncident.set(incident);
  this.showModal.set(true);
}
```

---

### **ÉTAPE 7 : CRUD Audits** (30 min)

📁 `src/app/audits/audits.ts`

**Même pattern que les incidents, mais avec :**
- Date de réalisation
- Conformité (boolean)
- Observations (texte long)

**💡 Astuce :** Comparez incidents.ts et audits.ts côte à côte pour voir le pattern réutilisable.

---

### **ÉTAPE 8 : Plans d'Action** (30 min)

📁 `src/app/plans-action/plans-action.ts`

**Nouveauté : Relations entre entités**
```typescript
// Plan d'Action appartient à un Incident
interface PlanAction {
  incidentId: number;  // Clé étrangère
  ...
}

// Afficher le nom de l'incident
getIncidentTitre(incidentId) {
  const incident = this.incidents().find(i => i.id === incidentId);
  return incident?.titre || `Incident #${incidentId}`;
}
```

**💡 Nouveau concept : Dropdown Select**
```html
<select [(ngModel)]="formData.incidentId">
  @for (incident of incidents(); track incident.id) {
    <option [value]="incident.id">{{ incident.titre }}</option>
  }
</select>
```

---

### **ÉTAPE 9 : Non-Conformités** (45 min)

📁 `src/app/pages/non-conformites/non-conformites.component.ts`

**Concepts identiques aux Plans d'Action :**
- Lien avec Audit (auditId)
- 3 niveaux de sévérité (MINEURE, MAJEURE, CRITIQUE)
- 3 statuts (OUVERTE, EN_TRAITEMENT, RESOLUE)

**💡 Nouveauté : Classes CSS dynamiques**
```typescript
getSeveriteClass(severite) {
  return `badge-${severite.toLowerCase()}`;
}
// CRITIQUE → "badge-critique" (CSS rouge)
// MAJEURE  → "badge-majeure" (CSS orange)
```

---

### **ÉTAPE 10 : Templates** (1h)

📁 `src/app/pages/templates/templates.component.ts`

**Architecture Master-Detail :**
```typescript
selectedTemplate = signal(null);

// Vue LISTE
@if (!selectedTemplate()) {
  <div>Grille de tous les templates</div>
}

// Vue DÉTAIL
@else {
  <div>Questions du template sélectionné</div>
}
```

**Opérations imbriquées :**
1. Créer un template (nom, description)
2. Voir les détails d'un template
3. Ajouter des questions au template
4. Supprimer le template

**💡 Concept : Refresh après ajout**
```typescript
addQuestion() {
  this.templateService.addQuestion(templateId, question)
    .subscribe(() => {
      this.viewTemplate(template);  // Recharge les détails
    });
}
```

---

### **ÉTAPE 11 : Gestion Utilisateurs (Admin)** (1h)

📁 `src/app/pages/utilisateurs/utilisateurs.component.ts`

**Nouveaux concepts :**

1. **Modification partielle :**
```typescript
updateUser(id, data) {
  // Envoie seulement les champs modifiés
  const updateData = {
    email: this.formData.email,
    actif: this.formData.actif
  };
  
  // Password optionnel
  if (this.formData.password) {
    updateData.password = this.formData.password;
  }
}
```

2. **Toggle activé/désactivé :**
```typescript
toggleActif(user) {
  this.userService.updateUser(user.id, { 
    actif: !user.actif  // Inverse la valeur
  }).subscribe(() => this.loadUsers());
}
```

3. **Affichage conditionnel selon rôle :**
```html
<!-- Dans dashboard.html -->
<a *ngIf="isAdmin()">Utilisateurs</a>
```

---

## 🎨 Concepts CSS à Comprendre

### Badges colorés
```css
.badge-critique { background: #dc3545; }  /* Rouge */
.badge-majeure  { background: #ff9800; }  /* Orange */
.badge-mineure  { background: #ffc107; }  /* Jaune */
```

### Modal responsive
```css
.modal-overlay {
  position: fixed;           /* Plein écran */
  background: rgba(0,0,0,0.5);  /* Fond sombre */
  z-index: 1000;            /* Au-dessus de tout */
}
```

### Grid layout
```css
.templates-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  /* Auto-adapte le nombre de colonnes selon la largeur */
}
```

---

## 🔗 Comment les Fichiers sont Connectés

```
app.routes.ts
    ↓
dashboard.ts (navigation)
    ↓
incidents.ts → incident.service.ts → Backend API
    ↑                                     ↓
    ← token.interceptor.ts (ajoute JWT) ←
    ← error.interceptor.ts (gère erreurs) ←
    ↓
notification.service.ts → app.component.ts (affiche toasts)
```

---

## 📝 Ordre de Lecture Recommandé

### **Jour 1 : Fondations**
1. `environment.ts` (5 min)
2. `app.routes.ts` (10 min)
3. `user.model.ts` (5 min)
4. `auth.service.ts` (20 min)
5. `token.interceptor.ts` (15 min)
6. `auth.guard.ts` (10 min)
7. `login.ts` + `login.html` (30 min)

### **Jour 2 : Notifications & Dashboard**
1. `notification.service.ts` (20 min)
2. `error.interceptor.ts` (20 min)
3. `app.component.ts` + `app.html` (20 min)
4. `dashboard.ts` + `dashboard.html` (45 min)

### **Jour 3 : Premier CRUD**
1. `incident.model.ts` (5 min)
2. `incident.service.ts` (15 min)
3. `incidents.ts` + `incidents.html` (1h)
4. Testez en local pour voir le fonctionnement

### **Jour 4 : Autres CRUDs**
1. `audits.ts` (30 min)
2. `plans-action.ts` (30 min)
3. `non-conformites.component.ts` (30 min)

### **Jour 5 : Features Avancées**
1. `templates.component.ts` (1h)
2. `utilisateurs.component.ts` (1h)

---

## 🧪 Comment Tester Votre Compréhension

### Test 1 : Authentification
1. Ouvrez `login.ts`
2. Tracez mentalement le flow : `onSubmit()` → `authService.login()` → backend
3. Que se passe-t-il si le login échoue ?
4. Où est stocké le token JWT ?

### Test 2 : CRUD
1. Ouvrez `incidents.ts`
2. Identifiez les 4 opérations : Create, Read, Update, Delete
3. Quel service est utilisé ?
4. Comment le modal sait s'il est en mode création ou édition ?

### Test 3 : Relations
1. Ouvrez `plans-action.ts`
2. Comment afficher le titre de l'incident lié ?
3. Quelle méthode charge les incidents ?
4. Pourquoi charger les incidents au `ngOnInit()` ?

---

## 🎯 Concepts Clés à Maîtriser

### 1. **Signals (Angular 17)**
```typescript
incidents = signal<Incident[]>([]);  // État réactif
this.incidents.set([...]);           // Remplace
this.incidents.update(i => [...]);   // Modifie
```

### 2. **Observables & Subscribe**
```typescript
this.service.getData().subscribe({
  next: (data) => console.log(data),
  error: (err) => console.error(err)
});
```

### 3. **Injection de dépendances**
```typescript
private service = inject(MyService);
// Angular fournit automatiquement l'instance
```

### 4. **Two-way binding**
```html
<input [(ngModel)]="username">
<!-- Synchronise automatiquement la vue et le modèle -->
```

### 5. **Control flow (@if, @for)**
```html
@if (loading()) { <div>Chargement...</div> }
@for (item of items(); track item.id) { ... }
```

---

## 🆘 En Cas de Blocage

### Problème : "Je ne comprends pas ce fichier"
**Solution :** 
1. Identifiez le type de fichier (service, composant, model)
2. Cherchez les commentaires dans le code
3. Regardez les imports pour comprendre les dépendances
4. Exécutez l'app et testez la fonctionnalité

### Problème : "Trop de fichiers"
**Solution :** 
Commencez par UN flow complet :
- Login → Dashboard → Voir les incidents → Créer un incident
- Suivez ce chemin dans le code

### Problème : "Je ne sais pas quel fichier modifier"
**Solution :**
- **Modifier les données** → model.ts
- **Modifier l'affichage** → component.html + .css
- **Modifier la logique** → component.ts
- **Ajouter une route API** → service.ts

---

## 📚 Ressources Complémentaires

- **Angular Signals** : https://angular.dev/guide/signals
- **RxJS Observables** : https://rxjs.dev/guide/overview
- **Standalone Components** : https://angular.dev/guide/components

---

**Bon apprentissage ! 🚀**

*Commencez par l'Étape 1, prenez votre temps, et testez chaque concept en modifiant légèrement le code.*
