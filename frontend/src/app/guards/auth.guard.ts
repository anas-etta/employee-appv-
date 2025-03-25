import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { KeycloakService } from '../services/keycloak.service';
import { map, catchError, switchMap } from 'rxjs/operators';
import { MatDialog } from '@angular/material/dialog';  // Importer MatDialog
import { AccessDeniedDialogComponent } from '../components/access-denied-dialog.component';  // Importer ton composant de popup

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private keycloakService: KeycloakService, private router: Router, private dialog: MatDialog) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> {

    return this.keycloakService.isLoggedIn().pipe(
      switchMap(loggedIn => {
        if (!loggedIn) {
          this.router.navigate(['/login']);
          return [false];
        }

        // Vérification des rôles
        return this.keycloakService.getRoles().pipe(
          map((roles: string[]) => {
            const requiredRoles = next.data['roles'] as Array<string>;
            if (requiredRoles && !requiredRoles.some(role => roles.includes(role))) {
              // Afficher un popup d'accès refusé si l'utilisateur n'a pas les rôles nécessaires
              this.dialog.open(AccessDeniedDialogComponent);  // Ouvrir le dialog
              this.router.navigate(['/access-denied']);
              return false;
            }
            return true;
          }),
          catchError(() => {
            this.router.navigate(['/access-denied']);
            return [false];
          })
        );
      })
    );
  }
}
