export type IncidentType = 'CHUTE'|'FEU'|'FUITE_CHIMIQUE'|'PRESQU_ACCIDENT'|'AUTRE';
export type IncidentStatus = 'OUVERT' | 'FERME';

export interface Incident {
  id: number;
  dateTime: string; // ou Date
  location: string;
  typeIncident: IncidentType;
  description: string;
  creatorName: string;
  statut: IncidentStatus;
}

export interface CreateIncidentRequest {
  creation_date: string; // format ISO
  location: string;
  type_incident: IncidentType;
  description: string;
}