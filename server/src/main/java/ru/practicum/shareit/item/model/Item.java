package ru.practicum.shareit.item.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
@EqualsAndHashCode
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;
    @Column(nullable = false)
    private String name;
    private String description;
    @Column(name = "is_available", nullable = false)
    private Boolean available;
    @JoinColumn(name = "owner_id", nullable = false)
    private Long ownerId;
    @JoinColumn(name = "request_id")
    private Long requestId;

    public Item(Long id, String name, String description, Boolean available, Long itemRequestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = itemRequestId;
    }
}
