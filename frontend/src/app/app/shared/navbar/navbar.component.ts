import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth/auth.services';
import {CommonModule} from "@angular/common";

@Component({
    selector: 'app-navbar',
    standalone: true,
    imports: [RouterLink, RouterLinkActive,CommonModule],
    templateUrl: './navbar.component.html',
    styleUrl: './navbar.component.css'
})
export class NavbarComponent {
    private authService = inject(AuthService);
    private router = inject(Router);

    isAdmin(): boolean {
        return this.authService.isAdmin();
    }

    logout(): void {
        this.authService.logout();
        this.router.navigate(['/login']);
    }
}