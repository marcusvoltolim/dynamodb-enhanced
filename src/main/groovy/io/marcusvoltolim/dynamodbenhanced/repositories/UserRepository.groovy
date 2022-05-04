package io.marcusvoltolim.dynamodbenhanced.repositories

import groovy.transform.CompileStatic
import io.marcusvoltolim.dynamodbenhanced.exceptions.UserAbsentException
import io.marcusvoltolim.dynamodbenhanced.exceptions.UserAlreadyExistsException
import io.marcusvoltolim.dynamodbenhanced.models.AuthorityType
import io.marcusvoltolim.dynamodbenhanced.models.User
import org.springframework.stereotype.Service
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException

import java.time.Instant

@CompileStatic
@Service
class UserRepository {

    private static final Expression CREATE_CONDITION = Expression.builder().expression('attribute_not_exists(regDate)').build()
    private static final Expression UPDATE_CONDITION = Expression.builder().expression('attribute_exists(regDate)').build()

    final DynamoDbTable<User> table

    UserRepository(DynamoDbEnhancedClient client) {
        table = client.table(User.simpleName, TableSchema.fromBean(User))
    }

    Optional<User> getById(Key key) {
        Optional.ofNullable(table.getItem(key))
    }

    Optional<List<User>> getAll() {
        table.scan().items().asList()
            .with {
                Optional.ofNullable(it ?: null)
            }
    }

    User create(User user) {
        try {
            table.updateItem() {
                user.regDate = Instant.now()
                it.item(user).conditionExpression(CREATE_CONDITION)
            }
        } catch (ConditionalCheckFailedException ignored) {
            throw new UserAlreadyExistsException()
        }
    }

    User update(User user) {
        try {
            table.updateItem {
                user.updatedDate = Instant.now()
                it.item(user).conditionExpression(UPDATE_CONDITION).ignoreNulls(true)
            }
        } catch (ConditionalCheckFailedException ignored) {
            throw new UserAbsentException()
        }
    }

    Optional<User> delete(Key key) {
        Optional.ofNullable(table.deleteItem(key))
    }

    User delete2(String id, AuthorityType authority) {//alternative
        table.deleteItem(new User(id: id, authority: authority))
    }

}
