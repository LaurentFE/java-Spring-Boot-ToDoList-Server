package fr.laurentFE.todolistspringbootserver.repository;

import fr.laurentFE.todolistspringbootserver.model.ListName;
import org.springframework.data.repository.CrudRepository;

public interface ListNameRepository extends CrudRepository<ListName, Integer> {
}
