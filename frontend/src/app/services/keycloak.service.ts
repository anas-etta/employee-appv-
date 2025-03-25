import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';
import { Observable, BehaviorSubject } from 'rxjs';

@Injectable({
    providedIn: 'root'
  })
  export class KeycloakService {
    private keycloakAuth!: Keycloak.KeycloakInstance;
    private isAuthenticated: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
    private userRoles: BehaviorSubject<string[]> = new BehaviorSubject<string[]>([]);
    private initialized = false; // Flag pour vérifier si Keycloak a été initialisé
  
    constructor() { }
  
    init(): Promise<any> {
      // Si déjà initialisé, éviter de réinitialiser
      if (this.initialized) {
        return Promise.resolve(true);
      }
  
      // Initialisation de Keycloak avec la configuration
      this.keycloakAuth = new Keycloak({
        url: 'http://localhost:8081',
        realm: 'Employee',
        clientId: 'frontend_employee',
      });
  
      // Retourner une promesse qui se résout après l'initialisation de Keycloak
      return new Promise((resolve, reject) => {
        this.keycloakAuth.init({
          onLoad: 'login-required',  // Force la page de login si l'utilisateur n'est pas authentifié
          redirectUri: window.location.origin + window.location.pathname, // Ajout de la redirection vers la page actuelle
        }).then((authenticated) => {
          if (authenticated) {
            console.log('Keycloak initialized and user authenticated');
            this.isAuthenticated.next(true);
            this.userRoles.next(this.keycloakAuth.realmAccess?.roles || []);  // Mettre à jour les rôles
            console.log('User roles:', this.keycloakAuth.realmAccess?.roles);
          } else {
            console.log('User not authenticated');
            this.isAuthenticated.next(false);
          }
          this.initialized = true; // Marquer comme initialisé
          resolve(authenticated);
        }).catch((error) => {
          console.error('Keycloak initialization failed:', error);
          reject('Keycloak initialization failed');
        });
      });
    }
  
    login(): void {
      console.log('Attempting to login...');
      this.keycloakAuth.login().catch((error) => {
        console.error('Login failed:', error);
      });
    }
  
    logout(): void {
      console.log('Logging out...');
      this.keycloakAuth.logout({
        redirectUri: window.location.origin, // Redirige après la déconnexion
      }).catch((error) => {
        console.error('Logout failed:', error);
      });
    }
  
    isLoggedIn(): Observable<boolean> {
      return this.isAuthenticated.asObservable();
    }
  
    getRoles(): Observable<string[]> {
      return this.userRoles.asObservable();
    }
  
    getUsername(): string {
      const username = this.keycloakAuth.tokenParsed ? this.keycloakAuth.tokenParsed['preferred_username'] : '';
      console.log('Current username:', username);
      return username;
    }
  
    getToken(): string {
      if (this.keycloakAuth.token) {
        console.log('Current token:', this.keycloakAuth.token);
        return this.keycloakAuth.token;
      } else {
        console.error('Token is not available!');
        return '';
      }
    }
  
    // Méthode pour rafraîchir le token
    refreshToken(): Promise<any> {
      console.log('Refreshing token...');
      return new Promise((resolve, reject) => {
        this.keycloakAuth.updateToken(30).then((refreshed) => {
          if (refreshed) {
            console.log('Token refreshed');
            resolve(true);
          } else {
            console.log('Token not refreshed');
            reject('Token refresh failed');
          }
        }).catch((error) => {
          console.error('Token refresh failed:', error);
          reject('Token refresh failed');
        });
      });
    }
  }
  