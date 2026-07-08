import { Component, inject } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-root',
    standalone: true,
    imports: [CommonModule, RouterModule], // Garanta que RouterModule está aqui
    templateUrl: './app.component.html'
})
export class AppComponent {
    private router = inject(Router);

    isLoggedIn(): boolean {
        // Retorna true se houver um usuário ou token salvo no navegador
        return !!localStorage.getItem('currentUser');
    }

    logout(): void {
        localStorage.clear();
        this.router.navigate(['/login']);
    }
}