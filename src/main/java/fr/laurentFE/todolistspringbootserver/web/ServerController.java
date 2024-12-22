package fr.laurentFE.todolistspringbootserver.web;

import fr.laurentFE.todolistspringbootserver.model.User;
import fr.laurentFE.todolistspringbootserver.service.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    @GetMapping("/rest/Users")
    public Iterable<User> getUsers() {
        return serverService.findAllUsers();
    }

    @GetMapping("/rest/Users/{id}")
    public User getUser(@PathVariable Integer id) {
        User user = serverService.findUser(id);
        if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return user;
    }

    @PostMapping("/rest/Users")
    public User createUser(@RequestBody User user) {
        User new_user = serverService.createUser(user);
        if (new_user.getUser_id() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, user.getUser_name());
        }
        return new_user;
    }

    @PutMapping("/rest/Users/{id}")
    public User updateUser(@RequestBody User user, @PathVariable Integer id) {
        System.out.println(id);
        User updated_user = serverService.updateUser(user, id);
        if (updated_user.getUser_id() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, user.getUser_name());
        }
        return updated_user;
    }
}
