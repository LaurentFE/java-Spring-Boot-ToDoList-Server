package fr.laurentFE.todolistspringbootserver.web;

import fr.laurentFE.todolistspringbootserver.model.ToDoList;
import fr.laurentFE.todolistspringbootserver.model.User;
import fr.laurentFE.todolistspringbootserver.service.ServerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/* This class handles the REST APIs */
@RestController
public class ServerController {

    @Autowired
    private ServerService serverService;

    @GetMapping("/")
    public String displayDocumentation() {
        return "list available endpoints and usage";
    }


    /*
     * User endpoints
     */
    @GetMapping("/rest/users")
    public Iterable<User> getUsers() {
        return serverService.findAllUsers();
    }

    @GetMapping("/rest/users/{id}")
    public User getUser(@PathVariable Integer id) {
        return serverService.findUser(id);
    }

    @PostMapping("/rest/users")
    public User createUser(@RequestBody @Valid User user) {
        return serverService.createUser(user);
    }

    @PutMapping("/rest/users/{id}")
    public User updateUser(@RequestBody @Valid User user, @PathVariable Integer id) {
        return serverService.updateUser(user, id);
    }

    /*
     * ToDoList endpoints
     */
    @GetMapping("/rest/toDoLists")
    public Iterable<ToDoList> getToDoLists(@RequestBody @Valid User user) {
        return serverService.findAllToDoLists(user);
    }

    @GetMapping("/rest/toDoLists/{id}")
    public ToDoList getToDoList(@RequestBody @Valid User user, @PathVariable Integer id) {
        return serverService.findSpecificToDoList(user, id);
    }

    @PostMapping("/rest/toDoLists")
    public ToDoList createToDoList(@RequestBody @Valid ToDoList toDoList) {
        return serverService.createToDoList(toDoList);
    }

    @PutMapping("/rest/toDoLists/{id}")
    public ToDoList updateToDoList(@RequestBody @Valid ToDoList toDoList, @PathVariable Integer id) {
        return serverService.updateToDoList(toDoList, id);
    }
}
