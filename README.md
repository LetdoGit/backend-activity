# Authserver — Módulos Categorias e Livros

API REST em Kotlin + Spring Boot que estende um authserver com autenticação JWT para gerenciar **Categorias** e **Livros**, incluindo favoritos por usuário.

Projeto da atividade de backend (PUC-PR).

## Pilha

- Kotlin + Spring Boot 4
- Spring Web, Spring Data JPA, Spring Security
- JWT (jjwt)
- H2 (banco em memória)
- Springdoc OpenAPI (Swagger UI)
- Gradle (wrapper incluso)

## Como rodar

```bash
./gradlew bootRun
```

A aplicação sobe em `http://localhost:8080` com context-path `/api`.

| Recurso | URL |
|---|---|
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| H2 Console | `http://localhost:8080/h2-console` (JDBC: `jdbc:h2:mem:db`, user/pass: `sa`/`sa`) |
| OpenAPI spec | `http://localhost:8080/v3/api-docs` |

## Seed inicial

Ao subir, o `Bootstrapper` popula:
- **Roles:** `ADMIN`, `PREMIUM`
- **Admin:** `admin@authserver.com` / `admin`
- **Categorias:** Ficção, Romance, Tecnologia
- **Livros:** 1984, Clean Code, Kotlin in Action

## Endpoints

### Autenticação
| Método | Endpoint | Acesso |
|---|---|---|
| POST | `/api/users` | público (cadastro) |
| POST | `/api/users/login` | público |

A senha deve ter **mínimo 8 caracteres, letra, dígito e caractere especial** (`@$!%*#?&`).

### Categorias
| Método | Endpoint | Acesso |
|---|---|---|
| GET | `/api/categorias` | público |
| GET | `/api/categorias/{id}` | público |
| POST | `/api/categorias` | ADMIN |
| PUT | `/api/categorias/{id}` | ADMIN |
| DELETE | `/api/categorias/{id}` | ADMIN (bloqueia se houver livros vinculados) |

### Livros
| Método | Endpoint | Acesso |
|---|---|---|
| GET | `/api/livros` | público (filtros + ordenação via query) |
| GET | `/api/livros/{id}` | público |
| POST | `/api/livros` | autenticado |
| PUT | `/api/livros/{id}` | ADMIN |
| DELETE | `/api/livros/{id}` | ADMIN |
| PUT | `/api/livros/{id}/favoritos/{userId}` | dono ou ADMIN |
| DELETE | `/api/livros/{id}/favoritos/{userId}` | dono ou ADMIN |

### Filtros e ordenação em `GET /livros`

Todos os parâmetros são opcionais e combináveis:

| Parâmetro | Tipo | Descrição |
|---|---|---|
| `categoriaId` | Long | filtra pela categoria |
| `autor` | String | busca parcial, case-insensitive |
| `anoMin` | Int | ano mínimo (inclusivo) |
| `anoMax` | Int | ano máximo (inclusivo) |
| `sortBy` | String | `titulo`, `ano` ou `autor` (default: `titulo`) |
| `sortDir` | String | `ASC` ou `DESC` (default: `ASC`) |

Exemplo: `GET /api/livros?categoriaId=2&autor=martin&sortBy=ano&sortDir=DESC`

## Como testar

Pelo Swagger UI:
1. Acesse `http://localhost:8080/swagger-ui.html`.
2. Faça login via `POST /users/login` com as credenciais do admin.
3. Clique em **Authorize** no topo e cole **apenas o token** (sem o prefixo `Bearer`).
4. Os endpoints protegidos passam a aceitar suas chamadas.

Pela linha de comando, há exemplos em curl ao longo do README do projeto e nos commits.

## Estrutura

```
src/main/kotlin/br/pucpr/authserver/
├── categorias/          ← módulo Categorias (CRUD)
├── livros/              ← módulo Livros (CRUD + favoritos)
├── users/               ← usuários e autenticação
├── roles/               ← papéis (ADMIN, PREMIUM)
├── security/            ← JWT, filter e configuração do Spring Security
├── exception/           ← exceções customizadas
└── Bootstrapper.kt      ← seed inicial
```

Cada módulo segue o mesmo padrão em camadas: **Entity → Repository → Service → Controller**, com DTOs separados em `requests/` e `responses/`.
