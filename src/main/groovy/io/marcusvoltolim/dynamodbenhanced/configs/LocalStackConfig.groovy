package io.marcusvoltolim.dynamodbenhanced.configs

import groovy.util.logging.Slf4j
import io.marcusvoltolim.dynamodbenhanced.models.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition
import software.amazon.awssdk.services.dynamodb.model.BillingMode
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement
import software.amazon.awssdk.services.dynamodb.model.KeyType
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType

import javax.annotation.PostConstruct

//@ConditionalOnProperty(value = 'application.localstack', havingValue = 'true')
@Slf4j
@Configuration
class LocalStackConfig {

    final DynamoDbClient dynamoDbClient
    final DynamoDbEnhancedClient dynamoEnhancedDbClient
    private final List<String> queuesNames
    private final List<String> tablesNames

    LocalStackConfig(DynamoDbClient dynamoDbClient,
                     DynamoDbEnhancedClient dynamoEnhancedDbClient,
                     @Value('${application.dynamo.tables.names:}') List<String> tablesNames) {
        this.dynamoDbClient = dynamoDbClient
        this.dynamoEnhancedDbClient = dynamoEnhancedDbClient
        this.queuesNames = queuesNames
        this.tablesNames = tablesNames
    }

    @PostConstruct
    void init() {
        log.info('Starting LocalStack resources creation')
//        initDynamo()
        initDynamoEnhanced()
        log.info('Finished LocalStack resources creation')
    }

    void initDynamo() {
        tablesNames.each { name ->
            try {
                dynamoDbClient.createTable {
                    it.tableName(name)
                        .billingMode(BillingMode.PAY_PER_REQUEST)
                        .attributeDefinitions(AttributeDefinition.builder().attributeName("Id").attributeType(ScalarAttributeType.S).build())
                        .keySchema(KeySchemaElement.builder().attributeName("Id").keyType(KeyType.HASH).build())
                }
            } catch (ResourceInUseException ignored) {
                //tabela ja existe, ignorar excecão pra simplificar.
            }
        }
    }

    void initDynamoEnhanced() {
        [User].each { Class clazz ->
            try {
                dynamoEnhancedDbClient.table(clazz.simpleName, TableSchema.fromBean(clazz)).createTable()
            } catch (ResourceInUseException ignored) {
                ignored
                //tabela ja existe, ignorar excecão pra simplificar.
            }
        }

    }

}
