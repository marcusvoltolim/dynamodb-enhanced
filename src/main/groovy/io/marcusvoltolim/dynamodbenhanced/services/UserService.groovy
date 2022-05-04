package io.marcusvoltolim.dynamodbenhanced.services

import io.marcusvoltolim.dynamodbenhanced.models.AuthorityType
import io.marcusvoltolim.dynamodbenhanced.models.User
import io.marcusvoltolim.dynamodbenhanced.repositories.UserRepository
import org.springframework.stereotype.Service
import software.amazon.awssdk.enhanced.dynamodb.Key

@Service
class UserService {

    private final UserRepository repository

    UserService(UserRepository repository) {
        this.repository = repository
    }

    Optional<User> getById(String id, AuthorityType authority) {
        repository.getById(buildKey(id, authority))
    }

    Optional<List<User>> getAll() {
        repository.all
    }

    User create(User user) {
        repository.create(user)
    }

    User update(User user) {
        repository.update(user)
    }

    Optional<User> delete(String id, AuthorityType authority) {
        repository.delete(buildKey(id, authority))
    }

    private static Key buildKey(String id, AuthorityType authority) {
        Key.builder().partitionValue(id).sortValue(authority.name()).build()
    }

}
