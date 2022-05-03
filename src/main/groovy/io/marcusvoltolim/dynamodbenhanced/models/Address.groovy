package io.marcusvoltolim.dynamodbenhanced.models

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbIgnore

@DynamoDbBean
class Address {

    AddressType type
    String street
    String number
    String zipCode
    String federativeUnit
    String city
    String country

    @DynamoDbIgnore
    @Override
    MetaClass getMetaClass() {
        return super.getMetaClass()
    }

}
