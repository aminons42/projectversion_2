import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../core/services/user.service';
import { NotificationService } from '../../core/services/notification.service';
import { User } from '../../core/models';

@Component({
  selector: 'app-utilisateurs',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './utilisateurs.component.html',
  styleUrl: './utilisateurs.component.css'
})
export class UtilisateursComponent implements OnInit {
  private userService = inject(UserService);
  private notificationService = inject(NotificationService);

  users = signal<User[]>([]);
  loading = signal(false);
  showModal = signal(false);
  editingUser = signal<User | null>(null);

  formData = {
    username: '',
    password: '',
    email: '',
    role: 'EMPLOYEE',
    actif: true
  };

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading.set(true);
    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.users.set(users);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }

  openCreateModal(): void {
    this.resetForm();
    this.editingUser.set(null);
    this.showModal.set(true);
  }

  openEditModal(user: User): void {
    this.editingUser.set(user);
    this.formData = {
      username: user.username,
      password: '',
      email: user.email || '',
      role: user.roles[0]?.nom || 'EMPLOYEE',
      actif: user.actif
    };
    this.showModal.set(true);
  }

  closeModal(): void {
    this.showModal.set(false);
    this.resetForm();
  }

  resetForm(): void {
    this.formData = {
      username: '',
      password: '',
      email: '',
      role: 'EMPLOYEE',
      actif: true
    };
  }

  saveUser(): void {
    const user = this.editingUser();
    
    if (user) {
      // Mise à jour
      const updateData: any = {
        email: this.formData.email,
        actif: this.formData.actif
      };
      
      if (this.formData.password) {
        updateData.password = this.formData.password;
      }

      this.userService.updateUser(user.id, updateData).subscribe({
        next: () => {
          this.notificationService.success('Utilisateur modifié avec succès');
          this.loadUsers();
          this.closeModal();
        },
        error: () => {
          // Error handled by interceptor
        }
      });
    } else {
      // Création
      this.userService.createUser(this.formData as any).subscribe({
        next: () => {
          this.notificationService.success('Utilisateur créé avec succès');
          this.loadUsers();
          this.closeModal();
        },
        error: () => {
          // Error handled by interceptor
        }
      });
    }
  }

  deleteUser(user: User): void {
    if (confirm(`Êtes-vous sûr de vouloir supprimer l'utilisateur ${user.username} ?`)) {
      this.userService.deleteUser(user.id).subscribe({
        next: () => {
          this.notificationService.success('Utilisateur supprimé avec succès');
          this.loadUsers();
        },
        error: () => {
          // Error handled by interceptor
        }
      });
    }
  }

  toggleActif(user: User): void {
    this.userService.updateUser(user.id, { actif: !user.actif }).subscribe({
      next: () => {
        this.notificationService.success(`Utilisateur ${user.actif ? 'désactivé' : 'activé'}`);
        this.loadUsers();
      },
      error: () => {
        // Error handled by interceptor
      }
    });
  }
}
