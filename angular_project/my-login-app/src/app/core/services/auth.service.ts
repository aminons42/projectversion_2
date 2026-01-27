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
        // Si succès, sauvegarde le token et l'user
        localStorage.setItem('token', response.token);
        localStorage.setItem('user', JSON.stringify(response.user));
        // Redirection automatique vers le dashboard
        this.router.navigate(['/dashboard']);
      })
    );
  }
  
  // REGISTER
  register(registerRequest: RegisterRequest): Observable<string> {
    return this.http.post<string>(
      `${this.apiUrl}/register`,
      registerRequest
    ).pipe(
      tap(message => {
        // Afficher le message de succès
        alert(message); // "Utilisateur enregistré avec succès!"
        // Redirection vers la page login
        this.router.navigate(['/login']);
      })
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
    return userJson ? JSON.parse(userJson) : null;
  }
}