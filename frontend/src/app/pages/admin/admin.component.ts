import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../core/services/admin/admin.service';
import { AdminUser, AdminUpdateUserRequest } from '../../core/models/admin.models';
import { ScheduleResponse } from '../../core/models/schedule.models';
import { NavbarComponent } from "../../app/shared/navbar/navbar.component";

@Component({
    selector: 'app-admin',
    standalone: true,
    imports: [CommonModule, FormsModule, NavbarComponent],
    templateUrl: './admin.component.html',
    styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {
    private adminService = inject(AdminService);

    users: AdminUser[] = [];

    editingUser: AdminUser | null = null;
    editForm: AdminUpdateUserRequest = { name: '', email: '', password: '' };

    viewingUserId: number | null = null;
    viewingSchedules: ScheduleResponse[] = [];

    formMessage: string | null = null;
    formMessageType: 'error' | 'success' = 'error';

    ngOnInit(): void {
        this.loadUsers();
    }

    loadUsers(): void {
        this.adminService.getAllUsers().subscribe({
            next: (res: AdminUser[]) => this.users = res || [],
            error: (err) => {
                console.error('Erro ao carregar usuários:', err);
                this.formMessage = 'Não foi possível carregar a lista de usuários.';
                this.formMessageType = 'error';
            }
        });
    }

    startEdit(user: AdminUser): void {
        this.editingUser = user;
        this.editForm = { name: user.name, email: user.email, password: '' };
        this.formMessage = null;
        this.viewingUserId = null;
    }

    cancelEdit(): void {
        this.editingUser = null;
        this.editForm = { name: '', email: '', password: '' };
    }

    saveEdit(): void {
        if (!this.editingUser) return;

        if (!this.editForm.name.trim() || !this.editForm.email.trim()) {
            this.formMessage = 'Nome e e-mail são obrigatórios.';
            this.formMessageType = 'error';
            return;
        }

        this.adminService.updateUser(this.editingUser.id, this.editForm).subscribe({
            next: (updated: AdminUser) => {
                const idx = this.users.findIndex(u => u.id === updated.id);
                if (idx !== -1) this.users[idx] = updated;
                this.formMessage = 'Usuário atualizado com sucesso.';
                this.formMessageType = 'success';
                this.cancelEdit();
            },
            error: (err) => {
                console.error('Erro ao atualizar usuário:', err);
                this.formMessage = 'Não foi possível atualizar este usuário.';
                this.formMessageType = 'error';
            }
        });
    }

    toggleUser(user: AdminUser): void {
        const acao = user.enabled ? 'desativar' : 'ativar';
        if (!confirm(`Deseja ${acao} a conta de ${user.name}?`)) return;

        this.adminService.toggleUser(user.id).subscribe({
            next: (updated: AdminUser) => {
                const idx = this.users.findIndex(u => u.id === updated.id);
                if (idx !== -1) this.users[idx] = updated;
            },
            error: (err) => {
                console.error('Erro ao alterar status do usuário:', err);
                this.formMessage = 'Não foi possível alterar o status desta conta.';
                this.formMessageType = 'error';
            }
        });
    }

    viewSchedules(user: AdminUser): void {
        this.editingUser = null;
        this.viewingUserId = user.id;
        this.viewingSchedules = [];

        this.adminService.getUserSchedules(user.id).subscribe({
            next: (res: ScheduleResponse[]) => this.viewingSchedules = res || [],
            error: (err) => {
                console.error('Erro ao carregar compromissos:', err);
                this.formMessage = 'Não foi possível carregar os compromissos deste usuário.';
                this.formMessageType = 'error';
            }
        });
    }

    closeSchedules(): void {
        this.viewingUserId = null;
        this.viewingSchedules = [];
    }
}