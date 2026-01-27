import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NonConformiteService } from '../../core/services/non-conformite.service';
import { AuditService } from '../../core/services/audit.service';
import { NotificationService } from '../../core/services/notification.service';
import { NonConformite, NCSeverite } from '../../core/models';
import { Audit } from '../../core/models/audit.model';

@Component({
  selector: 'app-non-conformites',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './non-conformites.component.html',
  styleUrl: './non-conformites.component.css'
})
export class NonConformitesComponent implements OnInit {
  private ncService = inject(NonConformiteService);
  private auditService = inject(AuditService);
  private notificationService = inject(NotificationService);

  nonConformites = signal<NonConformite[]>([]);
  audits = signal<Audit[]>([]);
  loading = signal(false);
  showModal = signal(false);
  editingNC = signal<NonConformite | null>(null);

  formData = {
    description: '',
    severite: 'MINEURE' as NCSeverite,
    auditId: 0,
    statut: 'OUVERTE' as 'OUVERTE' | 'EN_TRAITEMENT' | 'RESOLUE'
  };

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading.set(true);
    this.ncService.getAllNonConformites().subscribe({
      next: (ncs) => {
        this.nonConformites.set(ncs);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });

    this.auditService.getAllAudits().subscribe({
      next: (audits) => {
        this.audits.set(audits);
      }
    });
  }

  openCreateModal(): void {
    this.resetForm();
    this.editingNC.set(null);
    this.showModal.set(true);
  }

  openEditModal(nc: NonConformite): void {
    this.editingNC.set(nc);
    this.formData = {
      description: nc.description,
      severite: nc.severite,
      auditId: nc.auditId,
      statut: nc.statut
    };
    this.showModal.set(true);
  }

  closeModal(): void {
    this.showModal.set(false);
    this.resetForm();
  }

  resetForm(): void {
    this.formData = {
      description: '',
      severite: 'MINEURE',
      auditId: 0,
      statut: 'OUVERTE'
    };
  }

  saveNC(): void {
    const nc = this.editingNC();
    const data = {
      ...this.formData,
      dateDetection: new Date().toISOString().split('T')[0]
    };

    if (nc) {
      this.ncService.updateNonConformite(nc.id, data).subscribe({
        next: () => {
          this.notificationService.success('Non-conformité modifiée avec succès');
          this.loadData();
          this.closeModal();
        }
      });
    } else {
      this.ncService.createNonConformite(data).subscribe({
        next: () => {
          this.notificationService.success('Non-conformité créée avec succès');
          this.loadData();
          this.closeModal();
        }
      });
    }
  }

  deleteNC(nc: NonConformite): void {
    if (confirm(`Êtes-vous sûr de vouloir supprimer cette non-conformité ?`)) {
      this.ncService.deleteNonConformite(nc.id).subscribe({
        next: () => {
          this.notificationService.success('Non-conformité supprimée avec succès');
          this.loadData();
        }
      });
    }
  }

  getAuditName(auditId: number): string {
    const audit = this.audits().find(a => a.id === auditId);
    return audit ? audit.titre : `Audit #${auditId}`;
  }

  getSeveriteClass(severite: NCSeverite): string {
    return `badge-${severite.toLowerCase()}`;
  }

  getStatutClass(statut: string): string {
    switch(statut) {
      case 'OUVERTE': return 'badge-danger';
      case 'EN_TRAITEMENT': return 'badge-warning';
      case 'RESOLUE': return 'badge-success';
      default: return '';
    }
  }
}
