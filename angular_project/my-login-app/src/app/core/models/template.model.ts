export interface ChecklistTemplate {
  id: number;
  nom: string;
  description: string;
  categorie: string;
  dateCreation: string;
  questions?: TemplateQuestion[];
}

export interface TemplateQuestion {
  id: number;
  question: string;
  typeReponse: 'OUI_NON' | 'TEXTE' | 'NUMERIQUE';
  obligatoire: boolean;
  ordre: number;
}

export interface CreateTemplateRequest {
  nom: string;
  description: string;
  categorie: string;
}

export interface CreateQuestionRequest {
  question: string;
  typeReponse: 'OUI_NON' | 'TEXTE' | 'NUMERIQUE';
  obligatoire: boolean;
  ordre: number;
}
