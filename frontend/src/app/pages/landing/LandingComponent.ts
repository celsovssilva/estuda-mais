import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
    selector: 'app-landing',
    standalone: true,
    imports: [CommonModule, RouterLink],
    templateUrl: './landing.component.html',
    styleUrl: './landing.component.css'
})
export class LandingComponent {
    features = [
        {
            icon: '📅',
            title: 'Agenda que cobra de você',
            text: 'Marque um compromisso, cumpra, e o dia fica verde no calendário. Deixou pela metade, laranja. Não fez nada, vermelho — sem enrolação.'
        },
        {
            icon: '📝',
            title: 'Anotações com anexo junto',
            text: 'Guarde o resumo da aula e o PDF do professor na mesma nota. Sem pasta separada pra procurar depois.'
        },
        {
            icon: '⏱️',
            title: 'Horas por categoria, sem se enganar',
            text: 'Veja quanto tempo real você deu pra Estudos, Academia e Cuidado Pessoal — separado, dia a dia, sem misturar tudo num número só.'
        }
    ];

    faqs = [
        {
            q: 'Preciso configurar algo complicado pra começar?',
            a: 'Não. Cria a conta, cadastra seu primeiro compromisso, e pronto — já está usando.'
        },
        {
            q: 'O cadastro agora é pago?',
            a: 'Não. Enquanto estamos em acesso antecipado, o cadastro é gratuito. O valor mostrado aqui é o preço planejado para quando os planos pagos entrarem no ar.'
        },
        {
            q: 'Funciona no celular?',
            a: 'Sim, o EstudaMais funciona direto no navegador do seu celular, sem precisar instalar nada.'
        }
    ];
}