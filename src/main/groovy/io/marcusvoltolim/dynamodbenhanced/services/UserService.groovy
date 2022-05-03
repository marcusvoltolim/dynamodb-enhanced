package io.marcusvoltolim.dynamodbenhanced.services

import io.marcusvoltolim.dynamodbenhanced.models.User
import org.springframework.stereotype.Service
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest

@Service
class UserService {

    private final DynamoDbTable<User> table

    UserService(DynamoDbEnhancedClient client) {
        table = client.table(User.simpleName, TableSchema.fromBean(User))
    }

    Optional<User> getById(String id, Boolean primaryAccount) {
        Optional.ofNullable(table.getItem(buildKey(id, primaryAccount)))
    }

    private static Key buildKey(String id, Boolean primaryAccount) {
        Key.builder().with {
            if (primaryAccount != null) {
                sortValue(primaryAccount.toString())
            }
            partitionValue(id).build()
        }
    }

    User getById2(String id, Boolean primaryAccount) { //alternative
        table.getItem(new User(id: id, primaryAccount: primaryAccount))
    }

    List<User> getAll(Map<String, String> filters) {
        table.scan {
            if (filters) {
                it.filterExpression(buildExpression(filters))
            }
        }.items().asList()
    }

    private static Expression buildExpression(Map<String, String> filters) {
        Expression.Builder builder = Expression.builder()
        String where = ''
        filters.each {
            builder.putExpressionName(it.key, it.value)
            builder.putExpressionName(it.key, it.value)
            where += "${it.key} = ${it.value}"
        }
        builder.build()
    }

    void create(User user) {
        table.putItem(user)
    }

    User update(User user) {
        table.updateItem(user)
    }

    User update2(User user) {//alternative
        table.updateItem(UpdateItemEnhancedRequest.builder().item(user).build())
    }

    User updatePartial(User user) {
        table.updateItem { it.item(user).ignoreNulls(true) }
    }

    User delete(String id, Boolean primaryAccount) {
        table.deleteItem(buildKey(id, primaryAccount))
    }

    User delete2(String id, Boolean primaryAccount) {//alternative
        table.deleteItem(new User(id: id, primaryAccount: primaryAccount))
    }

}
