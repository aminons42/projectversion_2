export type NCSeverite = 'MINEURE' | 'MAJEURE' | 'CRITIQUE';

export interface NonConformite {
  id: number;
  description: string;
  severite: NCSeverite;
  dateDetection: string;
  auditId: number;
  statut: 'OUVERTE' | 'EN_TRAITEMENT' | 'RESOLUE';
}
