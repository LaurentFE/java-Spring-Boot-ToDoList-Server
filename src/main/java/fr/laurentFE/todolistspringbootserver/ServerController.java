package fr.laurentFE.todolistspringbootserver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServerController {

    @GetMapping("/")
    public String displayDocumentation() {
        return "list available endpoints and usage";
    }
}
