import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PlanAction, Action, Escalade, SuiviAction, Verification, CreatePlanActionRequest } from '../models/plan-action.model';



@Injectable({ providedIn: 'root' })
export class PlanActionService {
  private plansUrl = environment.plansUrl;
  private actionsUrl = environment.actionsUrl;
  private escaladeUrl = environment.escaladesUrl;
  private suiviUrl = environment.suivisUrl;
  private verificationUrl = environment.verificationUrl;

  constructor(private http: HttpClient) {}

  // === PLANS ===
  getAllPlans(): Observable<PlanAction[]> {
    return this.http.get<PlanAction[]>(this.plansUrl);
  }

  getPlanById(id: string): Observable<PlanAction> {
    return this.http.get<PlanAction>(`${this.plansUrl}/${id}`);
  }

  createPlan(request: CreatePlanActionRequest): Observable<PlanAction> {
    return this.http.post<PlanAction>(this.plansUrl, request);
  }

  updatePlan(id: string, data: Partial<PlanAction>): Observable<PlanAction> {
    return this.http.put<PlanAction>(`${this.plansUrl}/${id}`, data);
  }

  deletePlan(id: string): Observable<void> {
    return this.http.delete<void>(`${this.plansUrl}/${id}`);
  }

  // === ACTIONS ===
  getAllActions(): Observable<Action[]> {
    return this.http.get<Action[]>(this.actionsUrl);
  }

  getActionById(id: string): Observable<Action> {
    return this.http.get<Action>(`${this.actionsUrl}/${id}`);
  }

  getActionsByPlan(planId: string): Observable<Action[]> {
    return this.http.get<Action[]>(`${this.actionsUrl}/plan/${planId}`);
  }

  getActionsByResponsable(responsableId: string): Observable<Action[]> {
    return this.http.get<Action[]>(`${this.actionsUrl}/responsable/${responsableId}`);
  }

 createAction(planId: string, data: Partial<Action>): Observable<Action> {
    return this.http.post<Action>(`${this.actionsUrl}/plan/${planId}`, data);
}

  updateAction(id: string, action: Partial<Action>): Observable<Action> {
    return this.http.put<Action>(`${this.actionsUrl}/${id}`, action);
  }

  deleteAction(id: string): Observable<void> {
    return this.http.delete<void>(`${this.actionsUrl}/${id}`);
  }

  getActionsEnRetard(): Observable<{count: number}> {
    return this.http.get<{count: number}>(`${this.actionsUrl}/stats/retard`);
  }

  // === ESCALADES ===
  getAllEscalades(): Observable<Escalade[]> {
    return this.http.get<Escalade[]>(`${this.escaladeUrl}/statut/OUVERTE`);
}

  getEscaladeById(id: string): Observable<Escalade> {
    return this.http.get<Escalade>(`${this.escaladeUrl}/${id}`);
  }

  createEscalade(data: Partial<Escalade>): Observable<Escalade> {
    return this.http.post<Escalade>(this.escaladeUrl, data);
  }

  updateEscalade(id: string, data: Partial<Escalade>): Observable<Escalade> {
    return this.http.put<Escalade>(`${this.escaladeUrl}/${id}`, data);
  }

  deleteEscalade(id: string): Observable<void> {
    return this.http.delete<void>(`${this.escaladeUrl}/${id}`);
  }

  // === SUIVIS ===
  getAllSuivis(): Observable<SuiviAction[]> {
    return this.http.get<SuiviAction[]>(`${this.suiviUrl}/action/1`);
}

  getSuiviById(id: string): Observable<SuiviAction> {
    return this.http.get<SuiviAction>(`${this.suiviUrl}/${id}`);
  }

  createSuivi(data: Partial<SuiviAction>): Observable<SuiviAction> {
    return this.http.post<SuiviAction>(this.suiviUrl, data);
  }

  updateSuivi(id: string, data: Partial<SuiviAction>): Observable<SuiviAction> {
    return this.http.put<SuiviAction>(`${this.suiviUrl}/${id}`, data);
  }

  deleteSuivi(id: string): Observable<void> {
    return this.http.delete<void>(`${this.suiviUrl}/${id}`);
  }

  // === VERIFICATIONS ===
  getAllVerifications(): Observable<Verification[]> {
    return this.http.get<Verification[]>(this.verificationUrl);
  }

  getVerificationById(id: string): Observable<Verification> {
    return this.http.get<Verification>(`${this.verificationUrl}/${id}`);
  }

  createVerification(data: Partial<Verification>): Observable<Verification> {
    return this.http.post<Verification>(this.verificationUrl, data);
  }

  updateVerification(id: string, data: Partial<Verification>): Observable<Verification> {
    return this.http.put<Verification>(`${this.verificationUrl}/${id}`, data);
  }

  deleteVerification(id: string): Observable<void> {
    return this.http.delete<void>(`${this.verificationUrl}/${id}`);
  }
}


