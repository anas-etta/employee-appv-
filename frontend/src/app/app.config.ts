import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { APP_INITIALIZER } from '@angular/core';
import { MatDialogModule } from '@angular/material/dialog';  // Importer MatDialogModule

import { routes } from './app.routes';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { KeycloakService } from './services/keycloak.service'; // Importer KeycloakService

// Fonction pour initialiser Keycloak
export function initializeKeycloak(keycloakService: KeycloakService) {
  return (): Promise<any> => keycloakService.init().catch((error) => {
    console.error('Keycloak initialization failed', error);
    return Promise.reject(error);  // Refuse la promesse si l'initialisation échoue
  });
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideAnimationsAsync(),
    provideHttpClient(),
    provideAnimations(),
    KeycloakService,
    MatDialogModule,  // Ajouter MatDialogModule
    {
      provide: APP_INITIALIZER,
      useFactory: initializeKeycloak,
      deps: [KeycloakService],
      multi: true
    }
  ]
};
