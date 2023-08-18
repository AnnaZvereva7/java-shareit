package ru.practicum.shareit.item.repositiry;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.users.UserRepository;
import ru.practicum.shareit.users.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentsRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CommentsRepository repository;

    @Autowired
    private UserRepository userRepository;

    LocalDateTime now=LocalDateTime.now();
    private User user1 = new User(null, "name", "email@mail.ru");
    private Item item1 = new Item(null, "name", "Description", true, 1L, null);
    private Item item2 = new Item(null, "name2", "description2", false, 1L, null);
    private Comment comment1= new Comment(null, "text1", item1, user1, now.minusDays(2));
    private Comment comment2= new Comment(null, "text2", item2, user1, now.minusDays(4));
    private Comment comment3= new Comment(null, "text3", item1, user1, now.minusDays(1));

    @BeforeEach
    void setup() {
        user1=userRepository.save(user1);
        item1.setOwnerId(user1.getId());
        item2.setOwnerId(user1.getId());
        item1=itemRepository.save(item1);
        item2=itemRepository.save(item2);
        comment1.setItem(item1);
        comment2.setItem(item2);
        comment3.setItem(item1);
        comment1.setAuthor(user1);
        comment2.setAuthor(user1);
        comment3.setAuthor(user1);
    }
    @AfterEach
    void delete() {
        repository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllCommentsByItemsId() {
        List<CommentDto> comments = repository.findAllCommentsByItemsId(List.of(item1.getId()));
        assertEquals(0, comments.size());

        repository.save(comment1);
        repository.save(comment2);
        repository.save(comment3);

        comments = repository.findAllCommentsByItemsId(List.of(item1.getId()));
        assertEquals(2, comments.size());
        assertEquals(comment1.getId(), comments.get(0).getId());
        assertEquals(comment1.getItem().getId(), comments.get(0).getItemId());
        assertEquals(comment1.getAuthor().getName(), comments.get(0).getAuthorName());
        assertEquals(comment1.getText(), comments.get(0).getText());
        assertEquals(comment1.getCreated(), comments.get(0).getCreated());

        assertEquals(comment3.getId(), comments.get(1).getId());
        assertEquals(comment3.getItem().getId(), comments.get(1).getItemId());
        assertEquals(comment3.getAuthor().getName(), comments.get(1).getAuthorName());
        assertEquals(comment3.getText(), comments.get(1).getText());
        assertEquals(comment3.getCreated(), comments.get(1).getCreated());

        comments = repository.findAllCommentsByItemsId(List.of(item1.getId(), item2.getId()));
        assertEquals(3, comments.size());
        assertEquals(comment2.getId(), comments.get(0).getId());
        assertEquals(comment1.getId(), comments.get(1).getId());
        assertEquals(comment3.getId(), comments.get(2).getId());


    }
}