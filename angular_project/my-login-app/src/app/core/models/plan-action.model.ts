export type PlanStatut = 'EN_COURS' | 'VALIDE' | 'CLOTURE';

export interface PlanAction {
  id: string;
  titre: string;
  description: string;
  dateDebut: string;
  dateEcheance: string;
  responsableId: string;
  statut?: PlanStatut;
  source?: SourcePlan;
  type?: TypeAction;
}

export interface CreatePlanActionRequest {
  titre: string;
  description: string;
  dateDebut: string;
  dateEcheance: string;
  responsableId: string;
  source: SourcePlan;
  type: TypeAction // ajoute cette ligne


}

export interface Action {
  id: string;
  titre: string;
  description: string;
  priorite: 'BASSE' | 'MOYENNE' | 'HAUTE' | 'CRITIQUE';
  statutAction: 'À_FAIRE' | 'EN_COURS' | 'TERMINE' | 'EN_RETARD';
  assigneA?: string;
  planId: string;
}

export interface Escalade {
  id: string;
  actionId: string;
  motif: string;
  dateEscalade: string;
  niveauEscalade: 'MANAGERS' | 'DIRECTION' | 'CEE';
}

export interface SuiviAction {
  id: string;
  actionId: string;
  statut: 'À_FAIRE' | 'EN_COURS' | 'TERMINE' | 'EN_RETARD';
  dateSuivi: string;
  remarques?: string;
}

export interface Verification {
  id: string;
  actionId: string;
  dateVerification: string;
  resultat: 'CONFORME' | 'NON_CONFORME' | 'PARTIEL';
  observateurs?: string;
}

export type SourcePlan = 
  | 'AUDIT' 
  | 'INCIDENT' 
  | 'AMELIORATION_CONTINUE' 
  | 'INSPECTION' 
  | 'REMONTEE_TERRAIN';

export type TypeAction = 
  | 'ACTION_CORRECTIVE' 
  | 'ACTION_PREVENTIVE' 
  | 'ACTION_AMELIORATION' 
  | 'ACTION_URGENCE';