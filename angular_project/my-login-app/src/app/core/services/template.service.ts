import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ChecklistTemplate, CreateTemplateRequest, CreateQuestionRequest, TemplateQuestion } from '../models/template.model';

@Injectable({ providedIn: 'root' })
export class TemplateService {
  private apiUrl = environment.templatesUrl;

  constructor(private http: HttpClient) {}

  createTemplate(request: CreateTemplateRequest): Observable<ChecklistTemplate> {
    return this.http.post<ChecklistTemplate>(this.apiUrl, request);
  }

  getAllTemplates(): Observable<ChecklistTemplate[]> {
    return this.http.get<ChecklistTemplate[]>(this.apiUrl);
  }

  getTemplateById(id: number): Observable<ChecklistTemplate> {
    return this.http.get<ChecklistTemplate>(`${this.apiUrl}/${id}`);
  }

  addQuestion(templateId: number, request: CreateQuestionRequest): Observable<TemplateQuestion> {
    return this.http.post<TemplateQuestion>(`${this.apiUrl}/${templateId}/questions`, request);
  }

  deleteTemplate(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
