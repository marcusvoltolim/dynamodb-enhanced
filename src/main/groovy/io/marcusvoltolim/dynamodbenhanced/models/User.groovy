package io.marcusvoltolim.dynamodbenhanced.models

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbIgnore
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey

import java.time.Instant

@DynamoDbBean
class User {

    String id
    Boolean primaryAccount
    String name
    String email
    Instant regDate
    List<Address> addresses

    @DynamoDbPartitionKey
    String getId() {
        id
    }

    @DynamoDbSortKey
    @DynamoDbAttribute('primary')
    String getPrimaryAccount() {
        primaryAccount
    }

    Boolean isPrimaryAccount() {
        primaryAccount?.toBoolean()
    }

    @DynamoDbIgnore
    @Override
    MetaClass getMetaClass() {
        return super.getMetaClass()
    }

}
