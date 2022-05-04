package io.marcusvoltolim.dynamodbenhanced.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.marcusvoltolim.dynamodbenhanced.UserCreationDto
import io.marcusvoltolim.dynamodbenhanced.UserUpdateDto
import io.marcusvoltolim.dynamodbenhanced.exceptions.UserAbsentException
import io.marcusvoltolim.dynamodbenhanced.exceptions.UserAlreadyExistsException
import io.marcusvoltolim.dynamodbenhanced.models.AuthorityType
import io.marcusvoltolim.dynamodbenhanced.models.User
import io.marcusvoltolim.dynamodbenhanced.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@RequestMapping('/user')
@RestController
class UserController {

    private final UserService service
    private final ObjectMapper mapper

    UserController(UserService service, ObjectMapper mapper) {
        this.service = service
        this.mapper = mapper
    }

    @GetMapping(path = '{id}', produces = APPLICATION_JSON_VALUE)
    ResponseEntity getById(@PathVariable String id, @RequestParam AuthorityType authority) {
        service.getById(id, authority).with { ResponseEntity.of(it) }
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<User>> getAll() {
        service.all.with { ResponseEntity.of(it) }
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ResponseEntity create(@RequestBody User user) {
        try {
            service.create(user).with { convertUserResponse(it, UserCreationDto) }
        } catch (UserAlreadyExistsException ignored) {
            ResponseEntity.badRequest().body([
                error_message: 'User already created! Try update it...',
                user_details : [id: user.id, authority: user.authority]])
        }
    }

    @PatchMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ResponseEntity updatePartial(@RequestBody User user) {
        try {
            service.update(user).with { convertUserResponse(it, UserUpdateDto) }
        } catch (UserAbsentException ignored) {
            ResponseEntity.badRequest().body([
                error_message: 'User absent! Try creating it first...',
                user_details : [id: user.id, authority: user.authority]])
        }
    }

    @DeleteMapping(path = '{id}', produces = APPLICATION_JSON_VALUE)
    ResponseEntity delete(@PathVariable String id, @RequestParam AuthorityType authority) {
        service.delete(id, authority).with { ResponseEntity.of(it) }
    }

    private ResponseEntity<UserCreationDto> convertUserResponse(User user, Class<UserCreationDto> type) {
        ResponseEntity.ok(mapper.convertValue(user, type))
    }

}
