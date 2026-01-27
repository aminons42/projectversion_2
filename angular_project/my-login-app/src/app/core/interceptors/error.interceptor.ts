import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { NotificationService } from '../services/notification.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const notificationService = inject(NotificationService);
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'Une erreur est survenue';

      if (error.error instanceof ErrorEvent) {
        // Client-side error
        errorMessage = `Erreur: ${error.error.message}`;
      } else {
        // Server-side error
        switch (error.status) {
          case 401:
            errorMessage = 'Session expirée. Veuillez vous reconnecter.';
            // Redirect to login after showing message
            setTimeout(() => router.navigate(['/login']), 1000);
            break;
          case 403:
            errorMessage = 'Accès non autorisé';
            break;
          case 404:
            errorMessage = 'Ressource non trouvée';
            break;
          case 500:
            errorMessage = 'Erreur serveur. Veuillez réessayer plus tard.';
            break;
          case 0:
            errorMessage = 'Impossible de se connecter au serveur';
            break;
          default:
            if (error.error?.message) {
              errorMessage = error.error.message;
            } else {
              errorMessage = `Erreur ${error.status}: ${error.statusText}`;
            }
        }
      }

      // Show error notification
      notificationService.error(errorMessage);

      // Re-throw the error so components can still handle it if needed
      return throwError(() => error);
    })
  );
};
