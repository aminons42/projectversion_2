import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Incident, CreateIncidentRequest } from '../models';

@Injectable({ providedIn: 'root' })
export class IncidentService {
  private apiUrl = environment.incidentsUrl;

  constructor(private http: HttpClient) {}

  // Créer un incident
  createIncident(request: CreateIncidentRequest): Observable<Incident> {
    return this.http.post<Incident>(this.apiUrl, request);
  }

  // Récupérer tous les incidents
  getAllIncidents(): Observable<Incident[]> {
    return this.http.get<Incident[]>(this.apiUrl);
  }

  // Récupérer un incident par ID
  getIncidentById(id: number): Observable<Incident> {
    return this.http.get<Incident>(`${this.apiUrl}/${id}`);
  }

  // Mettre à jour un incident
  updateIncident(id: number, incident: Partial<Incident>): Observable<Incident> {
    return this.http.put<Incident>(`${this.apiUrl}/${id}`, incident);
  }

  // Supprimer un incident
  deleteIncident(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Stats pour KPI
  getIncidentStats(): Observable<{total: number, critiques: number, ouverts: number}> {
    return this.http.get<{total: number, critiques: number, ouverts: number}>(`${this.apiUrl}/stats`);
  }
}
