package io.marcusvoltolim.dynamodbenhanced.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.marcusvoltolim.dynamodbenhanced.models.User
import io.marcusvoltolim.dynamodbenhanced.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@RequestMapping('/user')
@RestController
class UserController {

    private final UserService service
    private final ObjectMapper objectMapper

    UserController(UserService service, ObjectMapper objectMapper) {
        this.service = service
        this.objectMapper = objectMapper
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    ResponseEntity<User> getById(@RequestParam String id, @RequestParam(required = false) Boolean primaryAccount) {
        ResponseEntity.of(service.getById(id, primaryAccount))
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ResponseEntity create(@RequestBody String json) {
        service.create(objectMapper.readValue(json, User))
        ResponseEntity.created(null).build()
    }

    @PutMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<User> update(@RequestBody String json) {
        ResponseEntity.ok(service.update(objectMapper.readValue(json, User)))
    }

    @PatchMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<User> updatePartial(@RequestBody String json) {
        ResponseEntity.ok(service.updatePartial(objectMapper.readValue(json, User)))
    }

    @DeleteMapping(produces = APPLICATION_JSON_VALUE)
    ResponseEntity<User> delete(@RequestParam String id, @RequestParam Boolean primaryAccount) {
        ResponseEntity.ok(service.delete(id, primaryAccount))
    }

}
