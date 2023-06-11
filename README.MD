
# <strong> API com a finalidade de propor a criação de cálculos fictícios de salários e impostos a serem pagos a funcionários Unisoma <strong>

# <strong>Tecnologias Utilizadas: </strong>
- Java 8
- Maven (Build)
- Hibernate (Implementação JPA)
- Postgres (Data Base)
- Utilizar o Padrão de Projetos DTO (Data transfer object).
- JUnit e Mockito (Testes Unitários)
- OpenAPI (Documentação)
- Docker (Aplicação dockerizada)
- Collection adicional para testar no Postman


# <strong> Instruções para execução do projeto </strong>

- Necessário ter o Docker instalado e configurado
- No diretório da raiz do projeto, execute sudo docker-compose up -d (LINUX) ou docker-compose up -d (WINDOWS)

# Tópicos comtemplados na API

- Cadastro de funcionários
- Listagem de funcionários
- Calculo de novo salário
- Calculo de imposto para salário
- Saída formatada de acordo com o padrão no documento

# Observações

- Para consultar a documentação, acesse o link: http://localhost:8080/swagger-ui/index.html#/
- Para gerar o build com os testes, necessário executar o docker-compose up -d
e em seguida mvn clean install 

# Instruções adicionais para geração de nova versão via Dockerfile

- mvn clean install -DskipTests
- docker build -t 1.x.x/api-salario-funcionario
- Alterar versão no docker-compose.yml para a nova versão gerada