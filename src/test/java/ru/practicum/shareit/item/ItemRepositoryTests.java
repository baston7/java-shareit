package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.util.List;

@DataJpaTest
public class ItemRepositoryTests {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository itemRepository;
    private Item item;
    private Item item2;
    private Item item3;
    private Item item4;
    private  User owner;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }
    @BeforeEach
    void createItems(){
        owner = new User("Вася", "b@mail.ru");
        item = new Item( "Ручка", "Гелевая", true, null);
        item2 = new Item( "перо", "красивое", true, null);
        item3 = new Item("машинка", "пишущая", true, null);
        item4 = new Item( "мышка", "компьютерная", true, null);
    }
    @Test
    public void testSearchItems(){
        EntityManager entityManager=em.getEntityManager();
        entityManager.persist(owner);
        Assertions.assertEquals(1,owner.getId());
        item.setOwner(owner);
        item2.setOwner(owner);
        item3.setOwner(owner);
        item4.setOwner(owner);
        entityManager.persist(item);
        entityManager.persist(item2);
        entityManager.persist(item3);
        entityManager.persist(item4);
        Assertions.assertEquals(1,item.getId());
        Assertions.assertEquals(2,item2.getId());
        Assertions.assertEquals(3,item3.getId());
        Assertions.assertEquals(4,item4.getId());

        List<Item> items=itemRepository.search("МыШка", PageRequest.of(0,10));
        Assertions.assertEquals(1,items.size());
        Assertions.assertEquals(4,items.get(0).getId());
    }
}
