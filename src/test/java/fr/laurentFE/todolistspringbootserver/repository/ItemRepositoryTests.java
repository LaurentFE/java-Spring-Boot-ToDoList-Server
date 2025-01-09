package fr.laurentFE.todolistspringbootserver.repository;

import fr.laurentFE.todolistspringbootserver.model.Item;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

@SpringBootTest
public class ItemRepositoryTests {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    public void tearDown() {
        jdbcTemplate.execute("DELETE FROM `list_names`;");
        jdbcTemplate.execute("DELETE FROM `list_items`;");
        jdbcTemplate.execute("DELETE FROM `items`;");
        jdbcTemplate.execute("DELETE FROM `lists`;");
        jdbcTemplate.execute("DELETE FROM `users`;");
    }

    @Test
    public void ItemRepository_Save_ReturnSavedItem() {
        // Arrange
        String label = "Chocolate";
        boolean checked = true;
        Item item = new Item(label, checked);

        // Act
        Item savedItem = itemRepository.save(item);

        // Assert
        Assert.notNull(savedItem, "savedItem must not be null");
        Assert.isTrue(savedItem.getItemId() > 0, "saveItem.itemId must be positive");
        Assert.isTrue(savedItem.getLabel().equals(label), "savedItem.label must be identical to '"+label+"'");
        Assert.isTrue(savedItem.isChecked(), "savedItem.isChecked must be true");
    }

    @Test
    public void ItemRepository_FindById_ReturnFoundItem() {
        // Arrange
        String label = "Chocolate";
        boolean checked = true;
        Item item = new Item(label, checked);
        Item savedItem = itemRepository.save(item);
        Integer searchedId = savedItem.getItemId();

        // Act
        Item foundItem = itemRepository.findById(searchedId).orElse(null);

        // Assert
        Assert.notNull(foundItem, "foundItem must not be null");
        Assert.isTrue(foundItem.getItemId().equals(searchedId), "foundItem.getItemId() must be the same as the searchedId");
        Assert.isTrue(foundItem.getLabel().equals(label), "foundItem.label must be identical to '"+label+"'");
        Assert.isTrue(foundItem.isChecked(), "foundItem.isChecked must be true");
    }

    @Test
    public void ItemRepository_ExistsById_ReturnTrue() {
        // Arrange
        Item item = new Item("Chocolate",true);
        Item savedItem = itemRepository.save(item);
        Integer searchedId = savedItem.getItemId();

        // Act
        boolean exists = itemRepository.existsById(searchedId);

        // Assert
        Assert.isTrue(exists, "exists should be true");
    }
}
