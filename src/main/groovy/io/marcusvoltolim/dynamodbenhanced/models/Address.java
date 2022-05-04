package io.marcusvoltolim.dynamodbenhanced.models;

import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Getter
@Setter
@DynamoDbBean
public class Address {

    private AddressType type;
    private String street;
    private String number;
    private String zipCode;
    private String state;
    private String city;
    private String country;

}
