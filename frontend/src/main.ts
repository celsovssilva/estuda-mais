import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app/app.component';
import { appConfig } from './app/app/app.config';

// Esse comando liga a aplicação passando o componente principal e as configurações
bootstrapApplication(AppComponent, appConfig)
    .catch((err) => console.error(err));