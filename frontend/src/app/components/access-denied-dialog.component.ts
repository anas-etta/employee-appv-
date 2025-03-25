import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog'; // Importer MatDialogRef

@Component({
  selector: 'app-access-denied-dialog',
  template: `
    <h1 mat-dialog-title>Accès Refusé</h1>
    <div mat-dialog-content>
      <p>Vous n'avez pas l'autorisation d'accéder à cette page.</p>
    </div>
    <div mat-dialog-actions>
      <button mat-button (click)="close()">Fermer</button>
    </div>
  `,
})
export class AccessDeniedDialogComponent {
  constructor(private dialogRef: MatDialogRef<AccessDeniedDialogComponent>) {}

  close() {
    this.dialogRef.close(); // Fermeture du dialog
  }
}
