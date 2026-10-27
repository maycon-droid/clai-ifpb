
# CLAI — Sistema de Apoio ao Atendimento de Alunos

Projeto interdisciplinar desenvolvido no IFPB, período 2026.2.

## Integrantes

- (Gerente) Pedro Dantas Estrela Lisboa(BECA)
- Francisco Maycon Santana Silva
- Danylo Rodrigues da Silva
- João Kyldery Catanão da Silva
- Cicero Arthur Otaviano Feitosa
- Samuel Videres do Nascimento

## Tecnologias utilizadas

- Java 21
- Spring Boot 4
- Spring Data MongoDB
- MongoDB
- Thymeleaf
- Docker e Docker Compose
- Maven

## Pré-requisitos

Para executar utilizando Docker, é necessário ter:

- Git
- Docker Desktop com Docker Compose

## Como executar o projeto

### 1. Clonar o repositório

```bash
git clone https://github.com/maycon-droid/clai-ifpb.git
cd clai-ifpb
```

### 2. Iniciar o ambiente de desenvolvimento

Na raiz do projeto, execute:

```bash
docker compose -f docker-compose-dev.yml up --build -d
```

O Docker Compose construirá a imagem do backend e iniciará o MongoDB.

### 3. Verificar os contêineres

```bash
docker compose -f docker-compose-dev.yml ps
```

### 4. Acessar o sistema

Com os serviços iniciados, abra:

http://localhost:8081/alunos

A porta padrão do backend no host é 8081, podendo ser alterada pela variável APP_PORT.

### 5. Consultar os logs

```bash
docker compose -f docker-compose-dev.yml logs -f backend
```

Pressione Ctrl+C para sair da visualização dos logs.

### 6. Encerrar o ambiente

```bash
docker compose -f docker-compose-dev.yml down
```

O comando encerra os contêineres sem remover os volumes de dados.

## Estrutura do repositório

- apps/backend: aplicação Spring Boot
- apps/backend/src/main/java: código Java
- apps/backend/src/main/resources/templates: páginas Thymeleaf
- apps/backend/src/main/resources/static: CSS, JavaScript e ícones
- apps/backend/src/test: testes automatizados
- docker-compose-dev.yml: ambiente de desenvolvimento
- docker-compose-prod.yml: ambiente de produção
- gateway: diretório reservado ao gateway

## Observações

A configuração de conexão com o banco de dados e os perfis de execução serão padronizados nas próximas correções da Issue #45.

Utilize somente dados fictícios para testes do sistema.

## Decisões Arquiteturais

### 1. Arquitetura em Camadas (Layered Architecture)

Foi escolhida a Arquitetura em Camadas para organizar a aplicação em diferentes responsabilidades, facilitando a manutenção, a compreensão e a evolução do sistema. Essa abordagem permite separar as responsabilidades da aplicação, como a apresentação, as regras de negócio e o acesso aos dados.

A escolha também contribui para uma organização mais clara do código, tornando mais simples identificar onde cada funcionalidade deve ser implementada.

### 2. Organização de Pacotes por Funcionalidade (Package by Feature)

Foi adotada a organização de pacotes por funcionalidade (Package by Feature) para agrupar os componentes relacionados a cada funcionalidade do sistema. Dessa forma, classes como controllers, services e modelos podem ser organizadas de acordo com o recurso ao qual pertencem.

Essa abordagem facilita a localização dos arquivos, melhora a organização do projeto e favorece a manutenção e a evolução de cada funcionalidade.

### 3. Modelo de Frontend Acoplado

Foi escolhido o modelo de frontend acoplado, no qual a interface e o backend são desenvolvidos de forma integrada, em vez de serem mantidos como aplicações completamente independentes.

Essa decisão busca aproveitar as tecnologias e os conhecimentos já adquiridos pela equipe, reduzindo a necessidade de aprender novas ferramentas e tecnologias neste momento. Assim, o grupo pode concentrar seus esforços na implementação das funcionalidades e no cumprimento dos objetivos do projeto.

### 4. SGBD MongoDB

O MongoDB foi escolhido por solicitação do professor responsável pela disciplina de Banco de Dados, que definiu sua utilização no projeto.

Por ser um banco de dados NoSQL orientado a documentos, o MongoDB armazena os dados em documentos BSON, permitindo trabalhar com estruturas de dados flexíveis. Sua utilização também proporciona à equipe a oportunidade de aplicar os conceitos estudados na disciplina em um projeto prático.