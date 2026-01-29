import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../core/services/auth.service';
import { User } from '../core/models';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="profile-page">
      <div class="profile-card" *ngIf="user; else noUser">
        <h1>Profil</h1>
        <div class="row"><span>Prénom:</span><strong>{{ user?.prenom }}</strong></div>
        <div class="row"><span>Nom:</span><strong>{{ user?.nom }}</strong></div>
        <div class="row"><span>Email:</span><strong>{{ user?.username }}</strong></div>
        <div class="row"><span>Rôle:</span><strong>{{ user?.roles?.[0]?.nom || 'Utilisateur' }}</strong></div>
        <a class="btn" routerLink="/dashboard">Retour au dashboard</a>
      </div>
      <ng-template #noUser>
        <p>Utilisateur non trouvé. Veuillez vous reconnecter.</p>
        <a class="btn" routerLink="/login">Aller au login</a>
      </ng-template>
    </div>
  `,
  styles: [
    `.profile-page{padding:2rem;max-width:800px;margin:0 auto;}`,
    `.profile-card{background:#fff;border-radius:12px;box-shadow:0 2px 8px rgba(0,0,0,.08);padding:2rem;}`,
    `h1{margin-top:0;color:#1f2937;}`,
    `.row{display:flex;gap:.75rem;margin:.5rem 0;color:#374151;}`,
    `.row span{min-width:90px;color:#6b7280;}`,
    `.btn{display:inline-block;margin-top:1.25rem;padding:.6rem 1rem;border-radius:8px;background:#2563eb;color:#fff;text-decoration:none;}`
  ]
})
export class ProfilePage implements OnInit {
  user: User | null = null;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.user = this.authService.getUser();
  }
}
