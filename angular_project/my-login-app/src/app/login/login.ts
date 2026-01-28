import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../core/services/auth.service';
import { NotificationService } from '../core/services/notification.service';
import { LoginRequest, RegisterRequest } from '../core/models';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class Login {
  // Variables pour le login
  username: string = '';
  password: string = '';
  
  // Variables pour le modal register
  showRegisterModal: boolean = false;
  registerUsername: string = '';
  registerPassword: string = '';
  registerNom: string = '';
  registerPrenom: string = '';
  
  constructor(
    private authService: AuthService,
    private router: Router,
    private notificationService: NotificationService
  ) {}

  // LOGIN
  onLogin() {
    const loginRequest: LoginRequest = {
      username: this.username,
      password: this.password
    };
    
    this.authService.login(loginRequest).subscribe({
      next: () => {
        this.notificationService.success('Connexion réussie');
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        const message = err.error?.message || err.message || 'Email ou mot de passe incorrect';
        this.notificationService.error(message);
      }
    });
  }
  
  // OUVRIR MODAL REGISTER
  onRegister() {
    this.showRegisterModal = true;
  }
  
  // FERMER MODAL REGISTER
  closeRegisterModal() {
    this.showRegisterModal = false;
    this.registerUsername = '';
    this.registerPassword = '';
    this.registerNom = '';
    this.registerPrenom = '';
  }
  
  // SOUMETTRE REGISTER
  onRegisterSubmit() {
    // Vérifier que tous les champs sont remplis
    if (!this.registerUsername || !this.registerPassword || !this.registerNom || !this.registerPrenom) {
      this.notificationService.error('Tous les champs sont obligatoires');
      return;
    }

    const registerRequest: RegisterRequest = {
      username: this.registerUsername,
      password: this.registerPassword,
      nom: this.registerNom,
      prenom: this.registerPrenom
    };
    
    console.log('Envoi de l\'inscription:', registerRequest);
    
    this.authService.register(registerRequest).subscribe({
      next: (response) => {
        console.log('Inscription réussie:', response);
        this.notificationService.success('Inscription réussie! Redirection vers la page de connexion...');
        this.closeRegisterModal();
        // Redirection vers login après 1 seconde
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 1000);
      },
      error: (err) => {
        console.error('Erreur d\'inscription:', err);
        let message = 'Erreur lors de l\'inscription';
        
        // Extraire le message d'erreur correctement
        if (err.error) {
          if (typeof err.error === 'string') {
            message = err.error;
          } else if (err.error.message) {
            message = err.error.message;
          }
        } else if (err.message) {
          message = err.message;
        }
        
        this.notificationService.error(message);
      }
    });
  }
}
