import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../core/services/auth.service';
import { IncidentService } from '../core/services/incident.service';
import { AuditService } from '../core/services/audit.service';
import { PlanActionService } from '../core/services/plan-action.service';
import { UserService } from '../core/services/user.service';
import { User, Incident, Audit, Action } from '../core/models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css']
})
export class Dashboard implements OnInit {
  currentUser: User | null = null;
  
  // KPIs
  incidentsCritiques: number = 0;
  actionsEnRetard: number = 0;
  conformiteGlobale: number = 0;
  employesActifs: number = 0;
  
  // Données pour affichage
  derniersIncidents: Incident[] = [];
  mesTaches: Action[] = [];
  prochainsAudits: Audit[] = [];

  // UI State
  loadingKpis: boolean = true;
  showUserMenu: boolean = false;

  constructor(
    private authService: AuthService,
    private incidentService: IncidentService,
    private auditService: AuditService,
    private planActionService: PlanActionService,
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit() {
    this.currentUser = this.authService.getUser();
    this.loadKpis();
    this.loadDashboardData();
  }

  loadKpis() {
    this.loadingKpis = true;
    
    // Mock data pour l'instant (remplacer par vrais appels API)
    setTimeout(() => {
      this.incidentsCritiques = 3;
      this.actionsEnRetard = 5;
      this.conformiteGlobale = 92;
      this.employesActifs = 150;
      this.loadingKpis = false;
    }, 500);
  }

  loadDashboardData() {
    // Charger les derniers incidents
    this.incidentService.getAllIncidents().subscribe({
      next: (incidents) => {
        this.derniersIncidents = incidents.slice(0, 5);
      },
      error: (err) => console.error('Erreur chargement incidents', err)
    });

    // Charger mes tâches (actions assignées à l'utilisateur)
    if (this.currentUser) {
      this.planActionService.getActionsByResponsable(String(this.currentUser.id)).subscribe({
        next: (actions) => {
          this.mesTaches = actions.filter(a => a.statutAction !== 'TERMINE').slice(0, 5);
        },
        error: (err) => console.error('Erreur chargement tâches', err)
      });
    }

    // Charger mes audits
    this.auditService.getMyAudits().subscribe({
      next: (audits) => {
        this.prochainsAudits = audits.filter(a => a.statut !== 'TERMINE').slice(0, 3);
      },
      error: (err) => console.error('Erreur chargement audits', err)
    });
  }

  toggleUserMenu() {
    this.showUserMenu = !this.showUserMenu;
  }

  logout() {
    this.authService.logout();
  }

  navigateTo(route: string) {
    this.router.navigate([route]);
  }

  getInitials(user: User | null): string {
    if (!user) return '?';
    return (user.prenom?.charAt(0) || '') + (user.nom?.charAt(0) || '');
  }

  getStatusBadgeClass(statut: string): string {
    const statusMap: {[key: string]: string} = {
      'OUVERT': 'badge-danger',
      'FERME': 'badge-success',
      'EN_COURS': 'badge-warning',
      'TERMINE': 'badge-success',
      'A_FAIRE': 'badge-info',
      'EN_RETARD': 'badge-danger'
    };
    return statusMap[statut] || 'badge-secondary';
  }

  isAdmin(): boolean {
    return this.currentUser?.roles?.some(r => r.nom === 'ROLE_ADMIN') || false;
  }
}
