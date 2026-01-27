import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { NonConformite } from '../models/non-conformite.model';

@Injectable({
  providedIn: 'root'
})
export class NonConformiteService {
  private http = inject(HttpClient);
  private apiUrl = environment.nonConformitesUrl;

  getAllNonConformites(): Observable<NonConformite[]> {
    return this.http.get<NonConformite[]>(this.apiUrl);
  }

  getNonConformiteById(id: number): Observable<NonConformite> {
    return this.http.get<NonConformite>(`${this.apiUrl}/${id}`);
  }

  getNonConformitesByAudit(auditId: number): Observable<NonConformite[]> {
    return this.http.get<NonConformite[]>(`${this.apiUrl}/audit/${auditId}`);
  }

  createNonConformite(nc: Partial<NonConformite>): Observable<NonConformite> {
    return this.http.post<NonConformite>(this.apiUrl, nc);
  }

  updateNonConformite(id: number, nc: Partial<NonConformite>): Observable<NonConformite> {
    return this.http.put<NonConformite>(`${this.apiUrl}/${id}`, nc);
  }

  deleteNonConformite(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
