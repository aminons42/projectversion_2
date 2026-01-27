import { Injectable, signal } from '@angular/core';

export interface Notification {
  id: number;
  message: string;
  type: 'success' | 'error' | 'warning' | 'info';
  icon: string;
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private notifications = signal<Notification[]>([]);
  
  // Expose notifications as readonly
  readonly notifications$ = this.notifications.asReadonly();

  success(message: string) {
    this.show(message, 'success', '✅');
  }

  error(message: string) {
    this.show(message, 'error', '❌');
  }

  warning(message: string) {
    this.show(message, 'warning', '⚠️');
  }

  info(message: string) {
    this.show(message, 'info', 'ℹ️');
  }

  private show(message: string, type: 'success' | 'error' | 'warning' | 'info', icon: string) {
    const notification: Notification = {
      id: Date.now() + Math.random(),
      message,
      type,
      icon
    };

    this.notifications.update(notifications => [...notifications, notification]);

    // Auto-remove after 3 seconds
    setTimeout(() => this.remove(notification.id), 3000);
  }

  private remove(id: number) {
    this.notifications.update(notifications => 
      notifications.filter(n => n.id !== id)
    );
  }

  clear() {
    this.notifications.set([]);
  }
}
