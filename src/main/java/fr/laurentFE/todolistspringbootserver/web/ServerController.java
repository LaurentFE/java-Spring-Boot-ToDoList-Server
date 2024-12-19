package fr.laurentFE.todolistspringbootserver.web;

import fr.laurentFE.todolistspringbootserver.model.User;
import fr.laurentFE.todolistspringbootserver.repository.UsersRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServerController {

    private final UsersRepository usersRepository;

    public ServerController(UsersRepository userRepository) {
        this.usersRepository = userRepository;
    }

    @GetMapping("/")
    public String displayDocumentation() {
        return "list available endpoints and usage";
    }

    @GetMapping("/rest/Users/")
    public Iterable<User> getUsers() {
        return usersRepository.findAll();
    }
}
