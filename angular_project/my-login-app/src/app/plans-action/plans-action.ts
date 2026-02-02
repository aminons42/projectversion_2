import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PlanActionService } from '../core/services/plan-action.service';
import { 
  PlanAction, 
  Action, 
  Escalade, 
  SuiviAction, 
  Verification,
  CreatePlanActionRequest 
} from '../core/models';
import { tick } from '@angular/core/testing';

@Component({
  selector: 'app-plans-action',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './plans-action.html',
  styleUrls: ['./plans-action.css']
})
export class PlansActionPage implements OnInit {
  activeTab = 'plans';
  plans: PlanAction[] = [];
  actions: Action[] = [];
  escalades: Escalade[] = [];
  suivis: SuiviAction[] = [];
  verifications: Verification[] = [];

  // Filter state
  selectedPlanId: string | null = null;
  selectedStatus = 'all';

  // Modal state
  showModal = false;
  isEditMode = false;
  currentItem: any = null;

  // Form data
  formData: any = {};

  constructor(private planActionService: PlanActionService) {}

  ngOnInit() {
    this.loadAllData();
  }

  loadAllData() {
    this.planActionService.getAllPlans().subscribe(data => {
      this.plans = data;
    });
    this.planActionService.getAllActions().subscribe(data => {
      this.actions = data;
      this.applyActionFilters();
    });
    this.planActionService.getAllEscalades().subscribe(data => {
      this.escalades = data;
    });
    this.planActionService.getAllSuivis().subscribe(data => {
      this.suivis = data;
    });
    this.planActionService.getAllVerifications().subscribe(data => {
      this.verifications = data;
    });
  }

  // === PLANS TAB ===
  openPlanModal(plan?: PlanAction) {
    this.isEditMode = !!plan;
    this.currentItem = plan;
    this.formData = plan ? { ...plan } : { dateDebut: '', dateEcheance: '', responsableId: '', titre: '', description: '',  type: 'ACTION_CORRECTIVE',
      source: 'AUDIT'};
    this.showModal = true;
  }

  submitPlanForm() {
    if (this.isEditMode) {
      this.planActionService.updatePlan(this.currentItem.id, this.formData).subscribe(() => {
        this.loadAllData();
        this.closeModal();
      });
    } else {
      const request: CreatePlanActionRequest = {
        dateDebut: this.formData.dateDebut,
        dateEcheance: this.formData.dateEcheance,
        responsableId: this.formData.responsableId,
        titre: this.formData.titre,
        description: this.formData.description,
        type: this.formData.type,      
        source: this.formData.source   

      };
      this.planActionService.createPlan(request).subscribe(() => {
        this.loadAllData();
        this.closeModal();
      });
    }
  }

  deletePlan(id: string) {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce plan ?')) {
      this.planActionService.deletePlan(id).subscribe(() => {
        this.loadAllData();
      });
    }
  }

  // === ACTIONS TAB ===
  openActionModal(action?: Action) {
    this.isEditMode = !!action;
    this.currentItem = action;
    this.formData = action 
      ? { ...action } 
      : { 
          planId: this.selectedPlanId, 
          description: '', 
          typeAction: 'ACTION_CORRECTIVE',
          priorite: 'MOYENNE', 
          responsableId: 1,
          dateEcheance: '',
          ressourcesNecessaires: '',
          titre:''
        };
    this.showModal = true;
}

  submitActionForm() {
    if (this.isEditMode) {
      this.planActionService.updateAction(this.currentItem.id, this.formData).subscribe(() => {
        this.loadAllData();
        this.closeModal();
      });
    } else {

       console.log('planId =', this.formData.planId);
       console.log('type =', typeof this.formData.planId); // 👈 très important

      const actionData = {
        titre: this.formData.titre,
        description: this.formData.description,
        typeAction: this.formData.typeAction,
        priorite: this.formData.priorite,
        responsableId: this.formData.responsableId,
        dateDebut: this.formData.dateDebut || null,
        ressourcesNecessaires: this.formData.ressourcesNecessaires || null
      };
      this.planActionService.createAction(this.formData.planId, actionData).subscribe(() => {
        this.loadAllData();
        this.closeModal();
      });
    }
}



  deleteAction(id: string) {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette action ?')) {
      this.planActionService.deleteAction(id).subscribe(() => {
        this.loadAllData();
      });
    }
  }

  markActionComplete(action: Action) {
    this.planActionService.updateAction(action.id, { ...action, statutAction: 'TERMINE' }).subscribe(() => {
      this.loadAllData();
    });
  }

  markActionLate(action: Action) {
    this.planActionService.updateAction(action.id, { ...action, statutAction: 'EN_RETARD' }).subscribe(() => {
      this.loadAllData();
    });
  }

  // === ESCALADES TAB ===
  openEscaladeModal(escalade?: Escalade) {
    this.isEditMode = !!escalade;
    this.currentItem = escalade;
    this.formData = escalade 
      ? { ...escalade } 
      : { actionId: '', motif: '', dateEscalade: '', niveauEscalade: 'MANAGERS' };
    this.showModal = true;
  }

  submitEscaladeForm() {
    if (this.isEditMode) {
      this.planActionService.updateEscalade(this.currentItem.id, this.formData).subscribe(() => {
        this.loadAllData();
        this.closeModal();
      });
    } else {
      this.planActionService.createEscalade(this.formData).subscribe(() => {
        this.loadAllData();
        this.closeModal();
      });
    }
  }

  deleteEscalade(id: string) {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette escalade ?')) {
      this.planActionService.deleteEscalade(id).subscribe(() => {
        this.loadAllData();
      });
    }
  }

  // === SUIVIS TAB ===
  openSuiviModal(suivi?: SuiviAction) {
    this.isEditMode = !!suivi;
    this.currentItem = suivi;
    this.formData = suivi 
      ? { ...suivi } 
      : { actionId: '', dateSuivi: '', statut: 'EN_COURS', remarques: '' };
    this.showModal = true;
  }

  submitSuiviForm() {
    if (this.isEditMode) {
      this.planActionService.updateSuivi(this.currentItem.id, this.formData).subscribe(() => {
        this.loadAllData();
        this.closeModal();
      });
    } else {
      this.planActionService.createSuivi(this.formData).subscribe(() => {
        this.loadAllData();
        this.closeModal();
      });
    }
  }

  deleteSuivi(id: string) {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce suivi ?')) {
      this.planActionService.deleteSuivi(id).subscribe(() => {
        this.loadAllData();
      });
    }
  }

  // === VERIFICATIONS TAB ===
  openVerificationModal(verification?: Verification) {
    this.isEditMode = !!verification;
    this.currentItem = verification;
    this.formData = verification 
      ? { ...verification } 
      : { actionId: '', dateVerification: '', resultat: 'CONFORME', observateurs: '' };
    this.showModal = true;
  }

  submitVerificationForm() {
    if (this.isEditMode) {
      this.planActionService.updateVerification(this.currentItem.id, this.formData).subscribe(() => {
        this.loadAllData();
        this.closeModal();
      });
    } else {
      this.planActionService.createVerification(this.formData).subscribe(() => {
        this.loadAllData();
        this.closeModal();
      });
    }
  }

  deleteVerification(id: string) {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette vérification ?')) {
      this.planActionService.deleteVerification(id).subscribe(() => {
        this.loadAllData();
      });
    }
  }

  // === FILTERS & HELPERS ===
  applyActionFilters() {
    let filtered = this.actions;
    if (this.selectedPlanId) {
      filtered = filtered.filter(a => a.planId === this.selectedPlanId);
    }
    if (this.selectedStatus !== 'all') {
      filtered = filtered.filter(a => a.statutAction === this.selectedStatus);
    }
    this.actions = filtered;
  }

  closeModal() {
    this.showModal = false;
    this.formData = {};
    this.currentItem = null;
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'À_FAIRE': return 'status-todo';
      case 'EN_COURS': return 'status-in-progress';
      case 'TERMINE': return 'status-done';
      case 'EN_RETARD': return 'status-late';
      default: return '';
    }
  }

  getPriorityColor(priority: string): string {
    switch (priority) {
      case 'HAUTE': return 'priority-high';
      case 'MOYENNE': return 'priority-medium';
      case 'BASSE': return 'priority-low';
      default: return '';
    }
  }

  getEscaladeLevel(level: string): string {
    switch (level) {
      case 'MANAGERS': return 'Managers';
      case 'DIRECTION': return 'Direction';
      case 'CEE': return 'CEE';
      default: return level;
    }
  }
}