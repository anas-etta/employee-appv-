import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable, from } from 'rxjs';
import { switchMap } from 'rxjs/operators';  // Assure-toi d'importer switchMap
import { KeycloakService } from './services/keycloak.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private keycloakService: KeycloakService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Rafraîchir le token avant chaque requête
    return from(this.keycloakService.refreshToken()).pipe(
      // Utiliser 'switchMap' pour gérer la logique de la requête après le rafraîchissement du token
      switchMap(() => {
        const token = this.keycloakService.getToken();
        
        if (token) {
          // Si un jeton est disponible, ajouter l'en-tête Authorization
          const clonedRequest = req.clone({
            setHeaders: {
              Authorization: `Bearer ${token}`
            }
          });

          return next.handle(clonedRequest);
        } else {
          // Si aucun jeton n'est disponible, envoyer la requête sans l'en-tête Authorization
          return next.handle(req);
        }
      })
    );
  }
}
