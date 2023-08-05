package ru.practicum.shareit.item.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    @Size(max = 50)
    private String name;
    @Size(max = 256)
    private String description;
    @Column(name = "is_available", nullable = false)
    private Boolean available;
    @JoinColumn(name = "owner_id", nullable = false)
    private Long ownerId;
    @JoinColumn(name = "request_id")
    private Long requestId;

    public Item(long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
