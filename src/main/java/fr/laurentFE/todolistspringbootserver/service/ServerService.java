package fr.laurentFE.todolistspringbootserver.service;

import fr.laurentFE.todolistspringbootserver.model.User;
import fr.laurentFE.todolistspringbootserver.repository.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

/* This class handles the business logic behind the API endpoints */
@Service
public class ServerService {

    @Autowired
    private UsersRepository usersRepository;

    public Iterable<User> findAllUsers() {
        return usersRepository.findAll();
    }
}
