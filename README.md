# Teste Tecnico - Microservico CNAE

Projeto Spring Boot criado para avaliacao de candidatos a vagas de Lider Tecnico e Analista.

## Stack

- Java 25
- Spring Boot 4.1.0
- Spring Web
- Spring Data JPA
- H2 Database
- Lombok
- Maven

## Como executar

```bash
mvn spring-boot:run
```

H2 Console:

```text
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:cnaedb
User: sa
Password:
```

## Endpoints

```http
GET /api/cnaes
GET /api/cnaes/buscar?termo=programas
GET /api/cnaes/codigo?codigo=6201-5/01
GET /api/cadastros-secundarios
GET /api/cadastros-secundarios/validar-cnae?codigoCnae=6201-5/01
POST /api/cadastros-secundarios
```

Comportamento esperado:

- `GET /api/cnaes` deve retornar todas as atividades cadastradas.
- `GET /api/cnaes/buscar?termo={texto}` deve buscar CNAEs que contenham o texto informado em qualquer parte da descricao, ignorando maiusculas e minusculas.
- `GET /api/cnaes/codigo?codigo={codigo}` deve retornar o CNAE do codigo informado.
- Codigos CNAE inexistentes devem retornar uma resposta HTTP adequada para recurso nao encontrado.
- `POST /api/cadastros-secundarios` deve criar um cadastro vinculado a um CNAE existente.
- `GET /api/cadastros-secundarios/validar-cnae?codigoCnae={codigo}` deve validar se o CNAE informado pode ser usado no cadastro.
- Cadastros secundários com CNAE inexistente nao devem ser criados.

Exemplo:

```bash
curl http://localhost:8080/api/cnaes
curl "http://localhost:8080/api/cnaes/buscar?termo=programas"
curl "http://localhost:8080/api/cnaes/codigo?codigo=6201-5/01"
curl -X POST http://localhost:8080/api/cadastros-secundarios \
  -H "Content-Type: application/json" \
  -d '{"nomeFantasia":"Tech Porto","documento":"12345678000199","codigoCnae":"6201-5/01"}'
```

## Desafio para o candidato

Objetivo:

1. Fazer a aplicacao subir corretamente.
2. Validar os endpoints disponiveis.
3. Identificar e corrigir problemas encontrados durante a execucao.
4. Explicar as causas dos problemas e as decisoes tomadas.
5. Adicionar ou ajustar testes, quando fizer sentido.

## Entrega esperada

- Codigo corrigido em um branch ou pull request.
- Breve explicacao tecnica das alteracoes.
- Evidencias de execucao, como comandos usados, respostas dos endpoints ou testes.

## Relatório Técnico & Diagnóstico de Falhas

### 1. Diagnóstico e Causa Raiz dos Problemas Identificados

* **Busca de CNAE por Termo Incompleta (`GET /api/cnaes/buscar`):**
    * **Causa Raiz:** A consulta `@Query` no `AtividadeEconomicaCnaeRepository` utilizava a concatenação `concat(:termo, '%')`, limitando as buscas apenas a textos que *iniciassem* com a palavra pesquisada.
    * **Solução:** Refatoração da query JPQL para utilizar `concat('%', :termo, '%')`, garantindo a busca contendo o termo (*contains*) em qualquer parte da descrição de forma *case-insensitive*.

* **Bypass de Regra e HTTP 200 Indevido (`GET /api/cnaes/codigo` e `/validar-cnae`):**
    * **Causa Raiz:** Utilização do método de fallback `.orElseGet(() -> repository.findAll().getFirst())` nas classes de serviço. Ao buscar um código inexistente, a aplicação retornava o primeiro CNAE cadastrado no banco de dados com status `200 OK`.
    * **Solução:** Substituição do fallback por `.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ...))` para sinalizar adequadamente a ausência do recurso.

* **Criação de Cadastro Secundário com CNAE Inválido (`POST /api/cadastros-secundarios`):**
    * **Causa Raiz:** O método de cadastro utilizava o mesmo fallback silencioso, permitindo associar o CNAE de ID 1 mesmo quando o usuário enviava um código inexistente.
    * **Solução:** Ajustada a lógica do serviço para lançar `ResponseStatusException(HttpStatus.BAD_REQUEST)` quando o código do CNAE não existir na base de dados, interrompendo a persistência.

---

### Execução de Testes

* **Testes Unitários:** Adicionadas suítes de testes unitários com **JUnit 5** e **Mockito** em `src/test/java/com/porto/testecnae/service/` cobrindo cenários de sucesso e exceções para os serviços de CNAE e Cadastro Secundário.
* **Execução dos testes via terminal:**
  ```bash
  ./mvnw test