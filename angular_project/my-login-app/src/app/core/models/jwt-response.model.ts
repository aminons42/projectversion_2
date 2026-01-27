import { User } from "./user.model";

export interface JwtResponse {
  token: string;
  user: User;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  nom: string;
  prenom: string;
}