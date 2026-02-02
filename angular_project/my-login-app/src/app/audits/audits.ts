import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuditService } from '../core/services/audit.service';
import { NotificationService } from '../core/services/notification.service';
import { Audit, CreateAuditRequest } from '../core/models/audit.model';

@Component({
  selector: 'app-audits',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './audits.html',
  styleUrls: ['./audits.css']
})
export class AuditsPage implements OnInit {
  audits: Audit[] = [];
  filteredAudits: Audit[] = [];
  templates: any[] = [];

  // Modal state
  showModal: boolean = false;
  isEditMode: boolean = false;
  currentAuditId: number | null = null;
  
  // Form data
  formData: CreateAuditRequest = this.getEmptyForm();
  
  // Filters
  filterStatus: string = '';
  
  // Statuts disponibles
  auditStatuts = ['PLANIFIE', 'EN_COURS', 'TERMINE'];

  constructor(
    private auditService: AuditService,
    private notificationService: NotificationService
  ) {}

  ngOnInit() {
    this.loadAudits();

  }

  loadAudits() {
    this.auditService.getAllAudits().subscribe({
      next: (data) => {
        this.audits = data;
        this.applyFilters();
      },
      error: (err) => console.error('Erreur chargement audits:', err)
    });
  }

  

  applyFilters() {
    this.filteredAudits = this.audits.filter(audit => {
      const matchStatus = !this.filterStatus || audit.statut === this.filterStatus;
      return matchStatus;
    });
  }

  openCreateModal() {
    this.isEditMode = false;
    this.formData = this.getEmptyForm();
    this.showModal = true;
  }

  openEditModal(audit: Audit) {
    this.isEditMode = true;
    this.currentAuditId = audit.id;
    this.formData = {
      titre: audit.titre,
      description: audit.description,
      dateDebut: audit.dateDebut,
      auditeurId: 1, // À remplacer par ID utilisateur connecté
      departement: audit.departement,
      typeAudit: audit.typeAudit || 'REGLEMENTAIRE'

    };
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
    this.formData = this.getEmptyForm();
    this.currentAuditId = null;
  }

  submitForm() {
  // Assurer que typeAudit a une valeur par défaut si vide
  if (!this.formData.typeAudit) {
    this.formData.typeAudit = 'REGLEMENTAIRE';
  }

  if (this.isEditMode && this.currentAuditId) {
    // Pour l'instant, la mise à jour est optionnelle
    // On peut juste fermer le modal
    this.closeModal();
  } else {
    this.auditService.createAudit(this.formData).subscribe({
      next: () => {
        this.loadAudits();
        this.closeModal();
        this.notificationService.success('Audit créé avec succès');
      },
      error: (err) => {
        console.error('Erreur création audit:', err);
        this.notificationService.error('Erreur lors de la création de l\'audit');
      }
    });
  }
}


  demarrerAudit(id: number) {
    this.auditService.demarrerAudit(id).subscribe({
      next: () => {
        this.loadAudits();
        this.notificationService.success('Audit démarré avec succès');
      }
    });
  }

  terminerAudit(id: number) {
    this.auditService.terminerAudit(id).subscribe({
      next: () => {
        this.loadAudits();
        this.notificationService.success('Audit terminé avec succès');
      }
    });
  }

  getStatusClass(statut: string): string {
    const classMap: {[key: string]: string} = {
      'PLANIFIE': 'badge-info',
      'EN_COURS': 'badge-warning',
      'TERMINE': 'badge-success'
    };
    return classMap[statut] || 'badge-secondary';
  }

  getStatusColor(statut: string): string {
    const colors: {[key: string]: string} = {
      'PLANIFIE': '#0891b2',
      'EN_COURS': '#f59e0b',
      'TERMINE': '#10b981'
    };
    return colors[statut] || '#6b7280';
  }

  canDemarrer(statut: string): boolean {
    return statut === 'PLANIFIE';
  }

  canTerminer(statut: string): boolean {
    return statut === 'EN_COURS';
  }

  private getEmptyForm(): CreateAuditRequest {
    return {
      titre: '',
      description: '',
      dateDebut: new Date().toISOString().split('T')[0],
      auditeurId: 1,
      departement: '',
      typeAudit:'REGLEMENTAIRE',
    };
  }
}
