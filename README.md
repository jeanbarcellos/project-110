# Project 110

Projeto de estudo de cache com Spring Boot, mostrando abordagens diferentes no mesmo sistema.

## Objetivo

Demonstrar na prática:

- Cache com anotações do Spring (`@Cacheable`, `@CachePut`, `@CacheEvict`, `@Caching`)
- Cache manual via Java puro (`CacheManager` + classe de infraestrutura)
- Estratégias de cache com comportamentos diferentes por domínio

## Stack

- Java 17
- Spring Boot 3
- Spring Cache
- Redis
- PostgreSQL
- Flyway
- Swagger/OpenAPI

## Estratégias implementadas no projeto

### 1) CategoryService: Write-Through com anotações

Arquivo: `service-api/src/main/java/com/jeanbarcellos/project110/service/CategoryService.java`

- Leitura:
  - `getAll` e `getById` usam `@Cacheable`
- Escrita:
  - `create` e `update` usam `@CachePut` para atualizar o item no cache imediatamente
  - também invalidam a lista `all` com `@CacheEvict`
  - `delete` remove item e lista (`@Caching`)

Resumo: a escrita atualiza cache e banco na mesma operação de serviço.

### 2) ProductService: Cache-Aside (Lazy Loading) com anotações

Arquivo: `service-api/src/main/java/com/jeanbarcellos/project110/service/ProductService.java`

- Leitura:
  - `getAll` e `getById` usam `@Cacheable`
  - cache só é preenchido quando ocorre a primeira leitura (cache miss)
- Escrita:
  - `create`, `update`, `delete` invalidam as chaves afetadas
  - próxima leitura recarrega o cache a partir do banco

Resumo: padrão clássico Cache-Aside, com carregamento sob demanda.

### 3) PersonService: implementação manual

Arquivos:

- `service-api/src/main/java/com/jeanbarcellos/project110/service/PersonService.java`
- `service-api/src/main/java/com/jeanbarcellos/project110/cache/PersonCache.java`

Características:

- `PersonService` mantém regra de negócio
- `PersonCache` encapsula infraestrutura de cache
- Operações de cache não lançam exceção para fora (falha de cache não quebra fluxo principal)
- Cache armazena `PersonResponse` (não entidade)

Resumo: abordagem manual útil para estudo de fluxo explícito, controle fino e tratamento resiliente.

## Conceitos de cache usados no projeto

- Cache Hit: dado encontrado no cache, evita consulta ao banco
- Cache Miss: dado não encontrado, busca no banco e popula cache
- Chaves de cache: por ID (`#id`) e chave agregada (`all`)
- TTL (Time To Live): tempo de expiração das entradas
- Invalidação: remoção de chaves após operações de escrita para evitar dado obsoleto
- Consistência eventual: após escrita, leituras podem depender da estratégia de invalidação/atualização

## Configuração de TTL e nomes de cache

### application.yml

As configurações ficam em `app-config`:

```yml
app-config:
  cache:
    default:
      ttl: 1h
    categories:
      name: "categories"
      ttl: 24h
    products:
      name: "products"
      ttl: 16h
    persons:
      name: "persons"
      ttl: 10m
```

### Classe de propriedades tipada

Arquivo:

- `service-api/src/main/java/com/jeanbarcellos/project110/properties/AppConfigProperties.java`

Objetivo:

- abstrair acesso às propriedades
- evitar uso de strings `"app-config...."` espalhadas nas classes
- manter valores default centralizados na própria classe

### Aplicação dos TTLs no CacheManager

Arquivo:

- `service-api/src/main/java/com/jeanbarcellos/project110/config/CacheConfig.java`

Objetivo:

- configurar o `RedisCacheManager`
- aplicar os TTLs por cache (`categories`, `products`, `persons`) e default global

## Como executar

### 1) Subir recursos (Postgres + Redis)

```bash
docker compose -f docker-compose_only-resources.yml up -d
```

### 2) Rodar a API

```bash
cd service-api
./mvnw spring-boot:run
```

Swagger:

- http://localhost:8080/swagger

## Endpoints principais

- `GET /api/v1/categories`
- `GET /api/v1/products`
- `GET /api/v1/persons`
- CRUD completo para as três rotas (`POST`, `PUT`, `DELETE`, `GET por id`)

## Como observar o efeito do cache

- Faça duas chamadas seguidas no mesmo endpoint de leitura
- A primeira tende a ser mais lenta (consulta ao banco)
- A segunda tende a ser mais rápida (cache hit)
- O projeto usa `ThreadUtils.delay(...)` em leituras para facilitar visualização do ganho
