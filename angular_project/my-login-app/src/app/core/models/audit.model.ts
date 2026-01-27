export type AuditStatut = 'PLANIFIE' | 'EN_COURS' | 'TERMINE';

export interface Audit {
  id: number;
  titre: string;
  description: string;
  dateDebut: string;
  dateFin?: string;
  statut: AuditStatut;
  auditeurId: number;
  departement: string;
}

export interface CreateAuditRequest {
  titre: string;
  description: string;
  dateDebut: string;
  auditeurId: number;
  departement: string;
}
