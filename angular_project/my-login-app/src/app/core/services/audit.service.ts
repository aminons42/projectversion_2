import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Audit, CreateAuditRequest } from '../models/audit.model';

@Injectable({ providedIn: 'root' })
export class AuditService {
  private apiUrl = environment.auditsUrl;
    private templateUrl = environment.auditsUrl.replace('/audits', '/templates'); // http://localhost:8082/api/templates


  constructor(private http: HttpClient) {}

  createAudit(request: CreateAuditRequest): Observable<Audit> {
    return this.http.post<Audit>(this.apiUrl, request);
  }

  getAllAudits(): Observable<Audit[]> {
    return this.http.get<Audit[]>(this.apiUrl);
  }
  

  getAuditById(id: number): Observable<Audit> {
    return this.http.get<Audit>(`${this.apiUrl}/${id}`);
  }

  getMyAudits(): Observable<Audit[]> {
    return this.http.get<Audit[]>(`${this.apiUrl}/mes-audits`);
  }

  demarrerAudit(id: number): Observable<Audit> {
    return this.http.put<Audit>(`${this.apiUrl}/${id}/demarrer`, {});
  }

  terminerAudit(id: number): Observable<Audit> {
    return this.http.put<Audit>(`${this.apiUrl}/${id}/terminer`, {});
  }

  // Stats pour KPI
  getConformiteGlobale(): Observable<{conformite: number}> {
    return this.http.get<{conformite: number}>(`${this.apiUrl}/conformite`);
  }
}
