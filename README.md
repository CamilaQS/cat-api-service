# 🐱 Cat API Service

Este projeto é uma aplicação Spring Boot desenvolvida para consumir a The Cat API (https://thecatapi.com/), armazenar informações sobre raças e imagens de gatos em um banco de dados PostgreSQL, e expor APIs RESTful para consulta desses dados.

## 🚀 Funcionalidades

- **Coleta de Dados**: Coleta informações de raças e URLs de imagens da The Cat API.
- **Persistência**: Armazena dados em um banco de dados PostgreSQL.
- **APIs RESTful**: Quatro endpoints principais para consulta de raças, com processamento paralelo.
- **Logging**: Implementação de logging com SLF4J/Logback para monitoramento.
- **Testes Unitários**: Cobertura de testes para as camadas de serviço, controller, repositório e cliente de API.

- ## 🏗️ Arquitetura do Projeto

A aplicação segue uma arquitetura em camadas, promovendo a separação de responsabilidades e a manutenibilidade:

```
src/main/java/com/example/catapi/
├── CatApiServiceApplication.java
├── config/                 # Configurações gerais (WebClient, etc.)
├── controller/             # Endpoints RESTful (BreedController, ImageController)
├── service/                # Lógica de negócio (BreedService, ImageService, CatApiClientService)
├── repository/             # Acesso a dados (BreedRepository, ImageRepository)
├── model/                  # Entidades JPA e DTOs
└── exception/              # Tratamento de exceções

```
###  Configurar a The Cat API Key

A The Cat API requer uma chave de API para acesso completo. Você pode obter uma gratuitamente em [The Cat API](https://thecatapi.com/signup).



Alternativamente, você pode definir a chave diretamente no `src/main/resources/application.properties` (Seguindo essa alternativa devido estar em ambiente de dev):

```properties
catapi.api-key=SUA_CHAVE_DA_API_AQUI
```


###  Configurar o Banco de Dados PostgreSQL

Certifique-se de que o PostgreSQL esteja instalado e em execução. Crie um banco de dados para o projeto:

```sql
CREATE DATABASE catapi_db;
CREATE USER catapi_user WITH PASSWORD 'catapi_password';
GRANT ALL PRIVILEGES ON DATABASE catapi_db TO catapi_user;
```

###  Configuração do `application.properties`

O arquivo `src/main/resources/application.properties` contém as configurações da aplicação. Verifique se as configurações do banco de dados e da API estão corretas:

```properties
# Configuração da aplicação
server.port=8080
spring.application.name=cat-api-service

# Configuração do banco de dados PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/catapi_db
spring.datasource.username=catapi_user
spring.datasource.password=catapi_password
spring.datasource.driver-class-name=org.postgresql.Driver

# Configuração do JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update # Use 'validate' ou 'none' para produção
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

# Configuração do The Cat API
catapi.base-url=https://api.thecatapi.com/v1
catapi.api-key=${CAT_API_KEY} # Lê da variável de ambiente CAT_API_KEY

# Configuração de logging
logging.level.com.example.catapi=INFO
logging.level.org.springframework.web=INFO
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
```


###  Compilar e Executar a Aplicação

Navegue até a raiz do projeto e execute:

```bash
mvn clean install
mvn spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`.

## 🧪 Testes Unitários

Para executar os testes unitários do projeto, utilize o Maven:

```bash
mvn test
```

## 📞 Endpoints da API

### Coleta de Dados

- `POST /api/breeds/collect`: Coleta todas as raças da The Cat API e as salva no banco de dados.
- `POST /api/images/collect/breeds`: Coleta 3 imagens para cada raça salva no banco de dados.
- `POST /api/images/collect/categories`: Coleta 3 imagens de gatos com chapéu e 3 imagens de gatos com óculos.

### APIs de Consulta de Raças

- `GET /api/breeds`: Lista todas as raças de gatos salvas.
- `GET /api/breeds/{id}`: Retorna as informações de uma raça específica pelo seu ID.
- `GET /api/breeds/by-temperament/{temperament}`: Lista raças que possuem um determinado temperamento (com processamento paralelo).
- `GET /api/breeds/by-origin/{origin}`: Lista raças que são de uma determinada origem (com processamento paralelo).

### APIs de Consulta de Imagens

- `GET /api/images/hats`: Lista imagens de gatos com chapéu.
- `GET /api/images/sunglasses`: Lista imagens de gatos com óculos.

## 📬 Coleções Insomnia

Para facilitar o consumo e teste das APIs, coleções para Postman e Insomnia foram fornecidas. Você pode importá-las em seus respectivos clientes:

- `insomnia_collection.json`

Essas coleções contêm exemplos de requisições para todos os endpoints da API.

#### Insomnia
1. Abra o Insomnia
2. Clique em "Import From" > "File"
3. Selecione `insomnia_collection.json`

### Ordem de Execução Recomendada

1. **Coleta de Dados** (primeira execução):
   - POST `/api/breeds/collect`
   - POST `/api/images/collect/breeds`
   - POST `/api/images/collect/categories`

2. **Testes das APIs**:
   - GET `/api/breeds`
   - GET `/api/breeds/{id}`
   - GET `/api/breeds/by-temperament/{temperament}`
   - GET `/api/breeds/by-origin/{origin}`
   - GET `/api/images/hats`
   - GET `/api/images/sunglasses`
  
   
## 📈 Logging e Monitoramento

O projeto utiliza SLF4J com Logback para logging. As configurações de logging podem ser ajustadas no `application.properties`.

Além disso, o **Spring Boot Actuator** está configurado para fornecer endpoints de monitoramento:

- `/actuator/health`: Verifica a saúde da aplicação.
- `/actuator/metrics`: Fornece métricas de desempenho.


