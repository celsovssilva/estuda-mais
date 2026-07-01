import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from '../core/interceptors/auth/auth.interceptors';

export const appConfig: ApplicationConfig = {
    providers: [
        provideRouter(routes),
        // Registra o cliente HTTP e ativa o interceptor de token criado anteriormente
        provideHttpClient(withInterceptors([authInterceptor]))
    ]
};