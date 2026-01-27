import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IncidentService } from '../core/services/incident.service';
import { NotificationService } from '../core/services/notification.service';
import { Incident, CreateIncidentRequest, IncidentType, IncidentStatus } from '../core/models';

@Component({
  selector: 'app-incidents',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './incidents.html',
  styleUrls: ['./incidents.css']
})
export class IncidentsPage implements OnInit {
  incidents: Incident[] = [];
  filteredIncidents: Incident[] = [];
  
  // Modal state
  showModal: boolean = false;
  isEditMode: boolean = false;
  currentIncidentId: number | null = null;
  
  // Form data
  formData: CreateIncidentRequest = this.getEmptyForm();
  
  // Filters
  filterType: IncidentType | '' = '';
  filterStatus: IncidentStatus | '' = '';
  
  // Types et statuts disponibles
  incidentTypes: IncidentType[] = ['CHUTE', 'FEU', 'FUITE_CHIMIQUE', 'PRESQU_ACCIDENT', 'AUTRE'];
  incidentStatuses: IncidentStatus[] = ['OUVERT', 'FERME'];

  constructor(
    private incidentService: IncidentService,
    private notificationService: NotificationService
  ) {}

  ngOnInit() {
    this.loadIncidents();
  }

  loadIncidents() {
    this.incidentService.getAllIncidents().subscribe({
      next: (data) => {
        this.incidents = data;
        this.applyFilters();
      },
      error: (err) => console.error('Erreur chargement incidents:', err)
    });
  }

  applyFilters() {
    this.filteredIncidents = this.incidents.filter(incident => {
      const matchType = !this.filterType || incident.typeIncident === this.filterType;
      const matchStatus = !this.filterStatus || incident.statut === this.filterStatus;
      return matchType && matchStatus;
    });
  }

  openCreateModal() {
    this.isEditMode = false;
    this.formData = this.getEmptyForm();
    this.showModal = true;
  }

  openEditModal(incident: Incident) {
    this.isEditMode = true;
    this.currentIncidentId = incident.id;
    this.formData = {
      creation_date: incident.dateTime,
      location: incident.location,
      type_incident: incident.typeIncident,
      description: incident.description
    };
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
    this.formData = this.getEmptyForm();
    this.currentIncidentId = null;
  }

  submitForm() {
    if (this.isEditMode && this.currentIncidentId) {
      this.incidentService.updateIncident(this.currentIncidentId, this.formData as any).subscribe({
        next: () => {
          this.loadIncidents();
          this.closeModal();
          this.notificationService.success('Incident modifié avec succès');
        }
      });
    } else {
      this.incidentService.createIncident(this.formData).subscribe({
        next: () => {
          this.loadIncidents();
          this.closeModal();
          this.notificationService.success('Incident créé avec succès');
        }
      });
    }
  }

  deleteIncident(id: number) {
    if (confirm('Êtes-vous sûr de vouloir supprimer cet incident ?')) {
      this.incidentService.deleteIncident(id).subscribe({
        next: () => {
          this.loadIncidents();
          this.notificationService.success('Incident supprimé avec succès');
        }
      });
    }
  }

  getStatusClass(statut: IncidentStatus): string {
    return statut === 'OUVERT' ? 'badge-danger' : 'badge-success';
  }

  getTypeColor(type: IncidentType): string {
    const colors: {[key: string]: string} = {
      'FEU': '#dc2626',
      'FUITE_CHIMIQUE': '#ea580c',
      'CHUTE': '#ca8a04',
      'PRESQU_ACCIDENT': '#0891b2',
      'AUTRE': '#6b7280'
    };
    return colors[type] || '#6b7280';
  }

  private getEmptyForm(): CreateIncidentRequest {
    return {
      creation_date: new Date().toISOString(),
      location: '',
      type_incident: 'AUTRE',
      description: ''
    };
  }
}
