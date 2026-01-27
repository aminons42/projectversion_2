export interface User {
    id: number;
    username: string;
    password?: string;
    nom: string;
    prenom: string;
    email?: string;
    actif: boolean;
    roles: Role[];
}

export interface Role {
    id:number;
    nom: string;
}