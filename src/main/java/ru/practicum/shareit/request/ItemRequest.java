package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final long id;
    @Column(nullable = false)
    private final String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id", nullable = false)
    private final User requestor;

    private final LocalDateTime created = LocalDateTime.now();

}
