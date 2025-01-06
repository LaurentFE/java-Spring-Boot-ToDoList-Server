package fr.laurentFE.todolistspringbootserver.repository;

import fr.laurentFE.todolistspringbootserver.model.UserList;
import org.springframework.data.repository.CrudRepository;

public interface UserListRepository extends CrudRepository<UserList, Integer> {
    java.util.Optional<Iterable<UserList>> findAllByUserId(Integer userId);
}
