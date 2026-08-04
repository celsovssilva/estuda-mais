import { Routes } from '@angular/router';
import { LoginComponent } from '../pages/login/login.component';
import { RegisterComponent } from '../pages/register/register.component';
import {DashboardComponent} from "../pages/dashboard/dashboard.component";
import {PerfilComponent}   from "../pages/profile/profile.component"
import {AgendaComponent} from "../pages/schedule/schedule.component";
import {NotesComponent} from "../pages/note/notes.component";
import {adminGuard} from "../core/guards/admin.guard";
import {AdminComponent} from "../pages/admin/admin.component";
import {LandingComponent} from "../pages/landing/landing.component";
import {SimuladoExecucaoComponent} from "../pages/simulados/simulado.component";


export const routes: Routes = [
    { path: '', redirectTo: 'landing', pathMatch: 'full' },
    { path: 'landing', component: LandingComponent },
    { path: 'login', component: LoginComponent },
    { path: 'login', component: LoginComponent },
    { path: 'register', component: RegisterComponent },
    {path: 'dashboard', component:DashboardComponent},
    {path: 'perfil', component: PerfilComponent},
    {path: 'schedule', component: AgendaComponent},
    {path: 'notes', component: NotesComponent},
    {path:'simulado',component:SimuladoExecucaoComponent},
    { path: 'adm', component: AdminComponent, canActivate: [adminGuard] },
    { path: '**', redirectTo: '/dashboard' }

];