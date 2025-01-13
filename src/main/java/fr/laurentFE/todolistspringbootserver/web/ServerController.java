package fr.laurentFE.todolistspringbootserver.web;

import fr.laurentFE.todolistspringbootserver.model.Item;
import fr.laurentFE.todolistspringbootserver.model.RItem;
import fr.laurentFE.todolistspringbootserver.model.ToDoList;
import fr.laurentFE.todolistspringbootserver.model.User;
import fr.laurentFE.todolistspringbootserver.model.exceptions.MissingParameterException;
import fr.laurentFE.todolistspringbootserver.model.exceptions.OverSizedStringProvidedException;
import fr.laurentFE.todolistspringbootserver.model.exceptions.UnexpectedParameterException;
import fr.laurentFE.todolistspringbootserver.service.ServerService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/* This class handles the REST API calls and their validation */
@RestController
public class ServerController {

    @Autowired
    private ServerService serverService;

    Logger logger = LoggerFactory.getLogger(ServerService.class);

    @GetMapping("/")
    public String displayDocumentation() {
        ClassLoader cl = this.getClass().getClassLoader();
        InputStream inputStream = cl.getResourceAsStream("classpath:/static/docs/index.html");
        String basicMessage = "Welcome to the Java - Spring Boot - Todo list server. You should find all the API " +
                "documentation in the folder target/generated-docs/index.html if you built this application from the " +
                "sources, or in BOOT-INF\\classes\\static\\docs\\index.html inside of the JAR file.";
        if (inputStream != null) {
            try {
                String str = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                inputStream.close();
                return str;
            } catch (IOException e) {
                logger.error("IOException during readAllBytes() from inputStream : classpath:/static/docs/index.html");
                logger.error(e.getMessage());
                return basicMessage;
            }
        }
        return basicMessage;
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
    @ResponseStatus(code= HttpStatus.CREATED)
    public User createUser(@RequestBody @Valid User user) {
        if (user.getUserId() != null) {
            throw new UnexpectedParameterException("userId");
        }
        if (user.getUserName().length() > 45) {
            throw new OverSizedStringProvidedException("userName [max:45]");
        }
        return serverService.createUser(user);
    }

    @PatchMapping("/rest/users/{id}")
    public User updateUser(@RequestBody @Valid User user, @PathVariable Integer id) {
        if (user.getUserId() != null) {
            throw new UnexpectedParameterException("userId");
        }
        if (user.getUserName().length() > 45) {
            throw new OverSizedStringProvidedException("userName [max:45]");
        }
        return serverService.updateUser(user, id);
    }

    /*
     * ToDoList endpoints
     */
    @GetMapping("/rest/toDoLists")
    public Iterable<ToDoList> getToDoLists(@RequestParam("userId") Integer userId) {
        return serverService.findAllToDoLists(userId);
    }

    @GetMapping("/rest/toDoLists/{id}")
    public ToDoList getToDoList(@PathVariable Integer id) {
        return serverService.findSpecificToDoList(id);
    }

    @PostMapping("/rest/toDoLists")
    @ResponseStatus(code= HttpStatus.CREATED)
    public ToDoList createToDoList(@RequestBody @Valid ToDoList toDoList) {
        if (toDoList.getListId() != null) {
            throw new UnexpectedParameterException("listId");
        }
        if (toDoList.getLabel().length() > 45) {
            throw new OverSizedStringProvidedException("label [max:45]");
        }
        if(!toDoList.getItems().isEmpty()) {
            for (Item item : toDoList.getItems()) {
                if (item.getItemId() != null) {
                    throw new UnexpectedParameterException("items[itemId]");
                }
                if (item.getLabel() == null) {
                    throw new MissingParameterException("items[label]");
                }
                if (item.getLabel().length() > 45) {
                    throw new OverSizedStringProvidedException("items[label] [max:45]");
                }
                if (item.isChecked() == null) {
                    throw new MissingParameterException("items[checked]");
                }
            }
        }
        return serverService.createToDoList(toDoList);
    }

    @PatchMapping("/rest/toDoLists/{id}")
    public ToDoList updateToDoList(@RequestBody @Valid ToDoList toDoList, @PathVariable Integer id) {
        if (toDoList.getListId() != null) {
            throw new UnexpectedParameterException("listId");
        }
        if (!toDoList.getItems().isEmpty()) {
            throw new UnexpectedParameterException("items");
        }
        if (toDoList.getLabel().length() > 45) {
            throw new OverSizedStringProvidedException("label [max:45]");
        }
        return serverService.updateToDoList(toDoList, id);
    }

    /*
     * Item endpoints
     */
    @PostMapping("/rest/items")
    @ResponseStatus(code= HttpStatus.CREATED)
    public Item createItem(@RequestBody @Valid RItem rItem) {
        if (rItem.getLabel().length() > 45) {
            throw new OverSizedStringProvidedException("label [max:45]");
        }
        return serverService.createItem(rItem);
    }

    @PatchMapping("/rest/items/{id}")
    public Item updateItem(@RequestBody @Valid Item item, @PathVariable Integer id) {
        if (item.getItemId() != null) {
            throw new UnexpectedParameterException("itemId");
        }
        if (item.getLabel().length() > 45) {
            throw new OverSizedStringProvidedException("label [max:45]");
        }
        return serverService.updateItem(item, id);
    }
}
