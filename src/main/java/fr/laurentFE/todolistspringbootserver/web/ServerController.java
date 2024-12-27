package fr.laurentFE.todolistspringbootserver.web;

import fr.laurentFE.todolistspringbootserver.model.User;
import fr.laurentFE.todolistspringbootserver.model.exceptions.DataDuplicateException;
import fr.laurentFE.todolistspringbootserver.model.exceptions.DataNotFoundException;
import fr.laurentFE.todolistspringbootserver.model.exceptions.UnexpectedParameterException;
import fr.laurentFE.todolistspringbootserver.service.ServerService;
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
    public User createUser(@RequestBody User user) {
        User new_user = serverService.createUser(user);
        return switch (new_user.getUser_id()) {
            case -400 -> throw new UnexpectedParameterException(new_user.getUser_name());
            case -409 -> throw new DataDuplicateException(new_user.getUser_name());
            default -> new_user;
        };
    }

    @PutMapping("/rest/Users/{id}")
    public User updateUser(@RequestBody User user, @PathVariable Integer id) {
        User updated_user = serverService.updateUser(user, id);
        return switch (updated_user.getUser_id()) {
            case -400 -> throw new UnexpectedParameterException(updated_user.getUser_name());
            case -404 -> throw new DataNotFoundException(updated_user.getUser_name());
            default -> updated_user;
        };
    }
}
