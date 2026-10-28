# 📚 StudyClean

Plataforma web para organização de estudos, voltada para quem está se preparando para o ENEM e vestibulares. Une agenda de estudos, anotações com anexos, banco de questões reais do ENEM (2009–2023) para simulados, e um painel de acompanhamento de desempenho.

🔗 **Acesse:** [estuda-mais-front.onrender.com](https://estuda-mais-front.onrender.com)

---

## ✨ Funcionalidades

### 📅 Agenda inteligente
- Cadastro de compromissos por categoria (Estudos, Academia, Cuidado Pessoal, Simulado, Aulas, Leitura, Redação, entre outras)
- Calendário com indicador visual de status por dia: **verde** (tudo concluído), **laranja** (parcial) e **vermelho** (nada concluído)
- Compromissos concluídos somem da lista ativa automaticamente

### 📝 Anotações com anexos
- Editor de notas vinculado a uma data de referência
- Upload de anexos (PDF, imagens) armazenados diretamente no banco de dados
- Visualização inline dos arquivos anexados

### 📊 Dashboard de desempenho
- Métricas de compromissos concluídos, pendentes e totais
- Horário de estudo por categoria, com visão semanal detalhada por dia
- Percentual de cumprimento mensal por categoria

### 🎯 Simulados ENEM
- Banco de questões reais do ENEM, de 2009 a 2023, importado da [API pública enem.dev](https://docs.enem.dev)
- Simulados filtráveis por ano, dia de prova (1º ou 2º dia) e idioma eletivo (inglês/espanhol)
- Sistema de eliminação de alternativas durante a resolução
- Estimativa de nota na escala ENEM (300–1000), com aproximação baseada em Teoria de Resposta ao Item (TRI)
- Correção automática com gabarito detalhado ao final

### 🔔 Lembretes por e-mail
- Notificações automáticas às 7h com o resumo do dia
- Lembretes reforçados às 12h e 18h para quem ainda não concluiu nenhuma atividade
- Envio via SMTP (Brevo)

### 🛡️ Painel administrativo
- Gestão de usuários (ativar/desativar contas, editar dados)
- Visualização dos compromissos de qualquer aluno
- Controle de acesso por papel (`ADMIN` / `USER`) via Spring Security

### 🔐 Autenticação e segurança
- Login e cadastro com JWT
- Senhas com hash via BCrypt
- Rotas protegidas por papel de usuário

---

## 🛠️ Stack Técnica

**Backend**
- Java 21 + Spring Boot 3
- Spring Security + JWT
- Spring Data JPA + Hibernate
- Spring Mail
- PostgreSQL

**Frontend**
- Angular (standalone components)
- TypeScript
- RxJS

**Infraestrutura**
- Deploy em [Render](https://render.com)
- Integração com API pública [enem.dev](https://docs.enem.dev) para importação de questões

---

## 📂 Estrutura do repositório

```
estuda-mais-monorepo/
├── backend/     # API REST em Spring Boot
└── frontend/    # SPA em Angular
```

---

## 🚀 Rodando localmente

### Pré-requisitos
- Java 21+
- Node.js 18+
- PostgreSQL

### Backend
```bash
cd backend/backend
# Configure src/main/resources/application-local.properties
# com suas credenciais de banco e SMTP
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

### Frontend
```bash
cd frontend
npm install
ng serve
```

Acesse `http://localhost:4200`.

---

## 🗺️ Roadmap

- [ ] Integração de pagamento para planos premium
- [ ] Migração de infraestrutura para ambiente sempre ativo
- [ ] Backup automatizado do banco de dados
- [ ] Expansão do banco de questões para outras bancas (além do ENEM)

---

## 📄 Licença

Projeto pessoal em desenvolvimento contínuo.
