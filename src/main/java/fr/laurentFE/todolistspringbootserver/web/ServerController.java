package fr.laurentFE.todolistspringbootserver.web;

import fr.laurentFE.todolistspringbootserver.model.ToDoList;
import fr.laurentFE.todolistspringbootserver.model.User;
import fr.laurentFE.todolistspringbootserver.model.exceptions.*;
import fr.laurentFE.todolistspringbootserver.service.ServerService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/* This class handles the REST APIs */
@RestController
public class ServerController {

    Logger logger = LoggerFactory.getLogger(ServerController.class);

    @Autowired
    private ServerService serverService;

    @GetMapping("/")
    public String displayDocumentation() {
        return "list available endpoints and usage";
    }


    /*
     * User endpoints
     */
    @GetMapping("/rest/Users")
    public Iterable<User> getUsers() {
        return serverService.findAllUsers();
    }

    @GetMapping("/rest/Users/{id}")
    public User getUser(@PathVariable Integer id) {
        User user = serverService.findUser(id);
        if (user == null) throw new DataNotFoundException("user_id");
        return user;
    }

    @PostMapping("/rest/Users")
    public User createUser(@RequestBody @Valid User user) {
        User newUser = serverService.createUser(user);
        return switch (newUser.getUserId()) {
            case -400 -> throw new UnexpectedParameterException(newUser.getUserName());
            case -409 -> throw new DataDuplicateException(newUser.getUserName());
            default -> newUser;
        };
    }

    @PutMapping("/rest/Users/{id}")
    public User updateUser(@RequestBody @Valid User user, @PathVariable Integer id) {
        User updatedUser = serverService.updateUser(user, id);
        return switch (updatedUser.getUserId()) {
            case -400 -> throw new UnexpectedParameterException(updatedUser.getUserName());
            case -404 -> throw new DataNotFoundException(updatedUser.getUserName());
            default -> updatedUser;
        };
    }

    /*
     * ToDoList endpoints
     */
    @GetMapping("/rest/ToDoLists")
    public Iterable<ToDoList> getToDoLists(@RequestBody @Valid User user) {
        Iterable<ToDoList> userToDoLists = serverService.findAllToDoLists(user);
        if (userToDoLists != null) {
            ArrayList<ToDoList> tdl = (ArrayList<ToDoList>) userToDoLists;
            return switch (tdl.getFirst().getListId()) {
                case -404 -> throw new DataNotFoundException(tdl.getFirst().getLabel());
                // Errors that cause a 500 are already logged in findAllToDoLists() and its calls
                case -500 -> throw new IncompleteDataSetException(tdl.getFirst().getLabel());
                default -> userToDoLists;
            };
        } else {
            return null;
        }
    }
}
