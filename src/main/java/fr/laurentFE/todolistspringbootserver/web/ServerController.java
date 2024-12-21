package fr.laurentFE.todolistspringbootserver.web;

import fr.laurentFE.todolistspringbootserver.model.User;
import fr.laurentFE.todolistspringbootserver.service.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/* This class handles the REST APIs */
@RestController
public class ServerController {

    @Autowired
    private ServerService serverService;

    @GetMapping("/")
    public String displayDocumentation() {
        return "list available endpoints and usage";
    }

    @GetMapping("/rest/Users")
    public Iterable<User> getUsers() {
        return serverService.findAllUsers();
    }
}
