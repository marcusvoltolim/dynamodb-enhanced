package io.marcusvoltolim.dynamodbenhanced.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@DynamoDbBean
public class User {

    private String id;
    private AuthorityType authority;
    private String name;
    private String email;
    private List<Address> addresses;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant regDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant updatedDate;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("authorityLevel")
    public AuthorityType getAuthority() {
        return authority;
    }

}
