import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TemplateService } from '../../core/services/template.service';
import { NotificationService } from '../../core/services/notification.service';
import { ChecklistTemplate, TemplateQuestion } from '../../core/models';

@Component({
  selector: 'app-templates',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './templates.component.html',
  styleUrl: './templates.component.css'
})
export class TemplatesComponent implements OnInit {
  private templateService = inject(TemplateService);
  private notificationService = inject(NotificationService);

  templates = signal<ChecklistTemplate[]>([]);
  selectedTemplate = signal<ChecklistTemplate | null>(null);
  loading = signal(false);
  showModal = signal(false);
  showQuestionModal = signal(false);

  templateForm = {
    nom: '',
    description: '',
    categorie: 'GENERAL'
  };

  questionForm = {
    question: '',
    ordre: 1,
    typeReponse: 'OUI_NON' as 'OUI_NON' | 'TEXTE' | 'NUMERIQUE',
    obligatoire: true
  };

  ngOnInit(): void {
    this.loadTemplates();
  }

  loadTemplates(): void {
    this.loading.set(true);
    this.templateService.getAllTemplates().subscribe({
      next: (templates) => {
        this.templates.set(templates);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }

  openCreateModal(): void {
    this.templateForm = { 
      nom: '', 
      description: '',
      categorie: 'GENERAL'
    };
    this.showModal.set(true);
  }

  closeModal(): void {
    this.showModal.set(false);
  }

  createTemplate(): void {
    this.templateService.createTemplate(this.templateForm).subscribe({
      next: () => {
        this.notificationService.success('Template créé avec succès');
        this.loadTemplates();
        this.closeModal();
      }
    });
  }

  viewTemplate(template: ChecklistTemplate): void {
    this.loading.set(true);
    this.templateService.getTemplateById(template.id).subscribe({
      next: (fullTemplate) => {
        this.selectedTemplate.set(fullTemplate);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }

  closeDetails(): void {
    this.selectedTemplate.set(null);
  }

  deleteTemplate(template: ChecklistTemplate): void {
    if (confirm(`Êtes-vous sûr de vouloir supprimer le template "${template.nom}" ?`)) {
      this.templateService.deleteTemplate(template.id).subscribe({
        next: () => {
          this.notificationService.success('Template supprimé avec succès');
          this.loadTemplates();
          if (this.selectedTemplate()?.id === template.id) {
            this.closeDetails();
          }
        }
      });
    }
  }

  openQuestionModal(): void {
    const currentQuestions = this.selectedTemplate()?.questions || [];
    this.questionForm = {
      question: '',
      ordre: currentQuestions.length + 1,
      typeReponse: 'OUI_NON',
      obligatoire: true
    };
    this.showQuestionModal.set(true);
  }

  closeQuestionModal(): void {
    this.showQuestionModal.set(false);
  }

  addQuestion(): void {
    const template = this.selectedTemplate();
    if (!template) return;

    this.templateService.addQuestion(template.id, this.questionForm).subscribe({
      next: () => {
        this.notificationService.success('Question ajoutée avec succès');
        this.viewTemplate(template); // Refresh
        this.closeQuestionModal();
      }
    });
  }
}
