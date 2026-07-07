📚 Estuda+ 

Plataforma backend para gerenciamento de conteúdos educacionais, usuários e organização de estudos, desenvolvida com foco em escalabilidade, organização e boas práticas.

🔗 Repositório

Estuda+ no GitHub

📌 Visão Geral

O Estuda+ é um sistema backend que tem como objetivo fornecer uma base robusta para aplicações educacionais, permitindo:

📖 Gestão de conteúdos de estudo
🧠 Organização de aprendizado
🔐 Autenticação segura
📊 Estrutura pronta para dashboards

Sistemas educacionais modernos ajudam estudantes a organizar e reforçar o aprendizado de forma eficiente, sendo amplamente utilizados em contextos escolares e preparação acadêmica

🧱 Arquitetura

O projeto segue uma arquitetura limpa baseada em camadas:

src/main/java/com/example/
│
├── controller   # Camada de entrada (REST API)
├── service      # Regras de negócio
├── repository   # Acesso ao banco (JPA)
├── entity       # Entidades do sistema
├── request      # DTOs de entrada
├── response     # DTOs de saída
├── config       # Segurança e configurações
⚙️ Tecnologias Utilizadas
Backend
Java 21
Spring Boot
Spring Web
Spring Data JPA
Spring Validation
Segurança
Spring Security
JWT (JSON Web Token)
Banco de Dados
PostgreSQL
Outros
Lombok
Maven
🔐 Autenticação e Segurança

A API implementa autenticação baseada em JWT, garantindo:

Login seguro
Proteção de rotas
Controle de acesso

Fluxo:

Login → Geração de Token → Acesso às rotas protegidas
📦 Funcionalidades
👤 Usuários
Cadastro de usuários
Login autenticado
Controle de permissões
📚 Conteúdos
Organização de materiais de estudo
Estrutura para disciplinas e temas
📊 Organização de Estudos
Base para dashboards
Evolução de aprendizado
🔗 Rotas da API
🔐 Autenticação
POST /auth/login
POST /auth/register
PUT /auth/update

Descrição:

login: autentica o usuário e retorna token JWT
register: cria um novo usuário
update profile: atualiza o usuário


Gerenciamento de conteúdos de estudo
📊 Dashboard
GET /dashboard

Retorna dados agregados do sistema
🧪 Testes
mvn test
🚀 Como Executar o Projeto
🔧 Pré-requisitos
Java 21
Maven
PostgreSQL
▶️ Passos
# Clonar repositório
git clone https://github.com/celsovssilva/estuda-mais.git

# Entrar na pasta
cd estuda-mais
cd backend/backend
cd frontend

# Rodar aplicação
mvn spring-boot:run - backend
npm run dev - frontend

📦 Build
mvn clean package
📈 Boas Práticas Aplicadas
✔️ Arquitetura em camadas
✔️ Separação entre DTO e entidade
✔️ Código limpo
✔️ Segurança com JWT
✔️ Escalabilidade

👨‍💻 Autor

Celso Vinícius Souza Silva

Desenvolvedor Fullstack

💡 Diferenciais do Projeto
🔥 Estrutura profissional pronta para produção
🔐 Segurança implementada
📚 Domínio educacional real
⚡ Código escalável e organizado


Sistema SaaS educacional
Plataforma de cursos
Ambiente de preparação para provas
