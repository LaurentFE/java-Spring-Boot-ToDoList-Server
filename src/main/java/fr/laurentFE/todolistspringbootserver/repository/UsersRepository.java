package fr.laurentFE.todolistspringbootserver.repository;

import fr.laurentFE.todolistspringbootserver.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UsersRepository extends CrudRepository<User, Integer> {
}