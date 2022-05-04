## Motivação

Projeto SpringBoot Web com Groovy/Java exemplificando como mapear tabelas do DynamoDb em entidades usando anotações similar ao SpringJPA.

## Tecnologias

* SpringBoot 2.6.7
* AWS 2.17.182
* Java 11
* Groovy 3.0.10
* Gradle 7.4.1

## Explicação

Necessário conhecimento prévio de Spring, injeção de dependência, aws-sdk-java-v2, NoSQL...
*Vamos usar LocalStack para emular os serviços da AWS localmente!
Nesse [repositório](https://github.com/marcusvoltolim/localstack-aws) está explicando como LocalStack e dynamodb-admin.*

Para quem conhece o *SpringJPA* já está familiarizado com o uso de anotações para mapear tabela(s) e coluna(s) em objeto/entidade e campos/atributos.
A implementção está seguindo o padrão MVC, estão temos Controller, Service, Repository, Model.

**As classes mapeadas precisam ser Java (.java), pois devido às particularidades do Groovy (leia-se MetaClass) não funciona.**

Iremos usar o cliente **DynamoDbEnhancedClient** *(em vez do tradicional DynamoDbClient)* e suas anotações:

* `@DynamoDbBean` *[obrigatória] [classe]*: Identifica a classe anotada como sendo uma entidade mapeável do DynamoDb;
* `@DynamoDbPartitionKey` *[obrigatória] [método]*: Indica que o atributo anotado é a chave de partição primária (hashKey) da tabela DynamoDb.
* `@DynamoDbSortKey` *[opcional] [método]*: Indica que o atributo anotado é a chave de classificação primária (sortKey) opcional da tabela DynamoDb;
* `@DynamoDbAttribute(String value)` *[opcional] [método]*: Especifica um nome diferente para o atributo do que o mapeador inferiria automaticamente usando uma estratégia de
  nomenclatura;
  * Por exemplo: se temos uma tabela com o campo `dtNasc` e queremos mapear na entidade para `dataNascimento` anotamos com `@DynamoDbAttribute('dtNasc')`;'
* `@DynamoDbConvertedBy` *[opcional] [método]*: Usada para associar um *AttributeConverter* personalizado ao atributo;
* `@DynamoDbIgnore` *[opcional] [método]*: Indica que o atributo será ignorado pelo mapeador, ou seja, não participa do schema da tabela;
* As anotações de nível de método não podem ser usadas no atributo/campo diretamente, sendo assim,
  mesmo que você esteja usando Groovy ou Java com Lombok precisará ter um getter ou setter do campo para ser anotado;
* O mapeamento não funciona com tipos primitivos: `int, long, boolean`, então use o tipo equivalente: `Integer, Long, Boolean);
* As anotações: `@DynamoDbPartitionKey, @DynamoDbSortKey`  só podem ser usadas em atributos com um tipo escalar do DynamoDb (string, número ou binário);
* Existem outras anotações que podem ser vistas no pacote `software.amazon.awssdk.enhanced.dynamodb.mapper.annotations`.

## Executando

### LocalStack - docker-compose

* Necessário ter docker e docker-compose instalados;
* Execute o seguinte comando na raiz do projeto: `docker-compose -up`;
* Após iniciar, os recursos do LocalStack estarão disponíveis na porta: `4566` e região: `sa-east-1`
* As configurações podem ser alteradas no arquivo [docker-compose.yaml](docker-compose.yaml), além de (des)ativar os serviços desejados.

### Aplicação

* Existem diversas maneiras de iniciar uma aplicação SpringBoot;
* Pode usar sua IDE de preferência (eu uso IntelliJ);
* Ou executar o seguinte comando na raiz do projeto: `./gradlew bootRun`
* Independente de como executar, em caso de sucesso terá o log: *Tomcat started on port(s): 8080 (http) with context path ''*,
  informando que a aplicação (endpoints) está sendo exposta na porta: *8080*.

### Endpoints

#### UserController

No [UserController](src/main/groovy/io/marcusvoltolim/dynamodbenhanced/controllers/UserController.groovy) temos os seguintes endpoints:

* Criar novo usuário:
  ```
  curl --location --request POST 'http://localhost:8080/user' \
  --header 'Content-Type: application/json' \
  --data-raw '{
      "id": 1,
      "authority": "USER",
      "name": "Marcus Voltolim",
      "email": "email@gmail.com",
      "addresses": [
          {
              "type": "PERSONAL",
              "street": "Rua 1",
              "number": "123",
              "zipCode": "10000-000",
              "state": "SP",
              "city": "São Paulo",
              "country": "Brasil"
          },
          {
              "type": "BUSINESS",
              "street": "Rua 2",
              "number": "1050",
              "zipCode": "99999-999",
              "state": "AC",
              "city": "Rio Branco",
              "country": "Brasil"
          }
      ]
  }'
  ```
  ![img.png](docs/create-user-user.png)

* Criar usuário com id e authority já existentes retorna erro (pode repetir a mesma chamada anterior):
  ![img.png](docs/create-user-duplicate.png)

* Criar usuário com id existente porém outra authority:
  ```
  curl --location --request POST 'http://localhost:8080/user' \
  --header 'Content-Type: application/json' \
  --data-raw '{
      "id": 1,
      "authority": "ADMIN",
      "name": "Marcus Voltolim",
      "email": "admin@gmail.com"
  }'
  ```
  ![img.png](docs/create-user-admin.png)

* Listar todos usuários:
  ```
  curl --location --request GET 'http://localhost:8080/user'
  ```
  ![img.png](docs/user-all.png)

* Buscando usuário por id e authority [retorna NotFound (404) se usuário não existir]:
  ```
  curl --location --request GET 'http://localhost:8080/user/1?authority=ADMIN'
  ```
  ![img.png](docs/user-find-admin.png)

  ```
  curl --location --request GET 'http://localhost:8080/user/1?authority=USER'
  ```
  ![img_1.png](docs/user-find-user.png)
  ![img_1.png](docs/user-find-absent.png)

* Atualizar usuário inexistente
  ```
  curl --location --request PUT 'http://localhost:8080/user' \
  --header 'Content-Type: application/json' \
  --data-raw '{
      "id": 11,
      "authority": "ADMIN",
      "name": "Marcus Voltolim",
      "email": "teste-admin@gmail.com"
  }'
  ```
  ![img.png](docs/user-update-absent.png)

* Atualizar usuário (atualiza apenas os campos informados, ignorando os demais)
  ```
  curl --location --request PATCH 'http://localhost:8080/user' \
  --header 'Content-Type: application/json' \
  --data-raw '{
      "id": 1,
      "authority": "USER",
      "name": "Marcus Vinícius",
      "email": "teste-user@gmail.com"
  }'
  ```
  ![img.png](docs/user-update-partial.png)
  ![img.png](docs/user-update-partial-details.png)

* Deletar usuário [retorna NotFound (404) se usuário não existir]:
  ```
  curl --location --request DELETE 'http://localhost:8080/user/1?authority=ADMIN'
  ```
  ![img.png](docs/user-delete.png)
  ![img.png](docs/user-delete-absent.png)
*

### Validando - [dynamodb-admin](https://github.com/aaronshaf/dynamodb-admin)

* Listagem:

  ![img.png](docs/dynamodb-admin-all.png)

* Detalhes do usuário:

  ![img.png](docs/dynamodb-admin-user-1.png)
