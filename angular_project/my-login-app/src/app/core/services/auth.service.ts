import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Router } from '@angular/router';

import { environment } from '../../../environments/environment';
import { 
  LoginRequest, 
  RegisterRequest, 
  JwtResponse, 
  User 
} from '../models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  
  private apiUrl = environment.authUrl; // http://localhost:8084/api/auth

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}
  
  // LOGIN
  login(loginRequest: LoginRequest): Observable<JwtResponse> {
    return this.http.post<JwtResponse>(
      `${this.apiUrl}/login`,
      loginRequest
    ).pipe(
      tap(response => {
        // Sauvegarder le token
        localStorage.setItem('token', response.token);
        
        // Sauvegarder l'user seulement s'il existe dans la réponse
        if (response.user) {
          localStorage.setItem('user', JSON.stringify(response.user));
        }
        
        // Redirection automatique vers le dashboard
        this.router.navigate(['/dashboard']);
      })
    );
  }
  
  // REGISTER
  register(registerRequest: RegisterRequest): Observable<any> {
    return this.http.post<any>(
      `${this.apiUrl}/register`,
      registerRequest,
      { responseType: 'text' as 'json' }  // Accepter les réponses texte
    );
  }
  
  // LOGOUT
  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.router.navigate(['/login']);
  }
  
  // Vérifier si connecté
  isAuthenticated(): boolean {
    return !!localStorage.getItem('token');
  }
  
  // Récupérer le token
  getToken(): string | null {
    return localStorage.getItem('token');
  }
  
  // Récupérer l'utilisateur
  getUser(): User | null {
    const userJson = localStorage.getItem('user');
    if (!userJson || userJson === 'undefined') {
      return null;
    }
    try {
      return JSON.parse(userJson);
    } catch (e) {
      console.error('Erreur parsing user:', e);
      return null;
    }
  }
}