package ru.practicum.shareit.booking.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDtoForOwner;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@SqlResultSetMapping(
        name = "BookingDtoForOwner",
        classes = {
                @ConstructorResult(
                        targetClass = BookingDtoForOwner.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "itemId ", type = Long.class),
                                @ColumnResult(name = "bookerId", type = Long.class),
                                @ColumnResult(name = "startDate ", type = LocalDateTime.class),
                                @ColumnResult(name = "endDate ", type = LocalDateTime.class)})})
@NamedNativeQuery(name = "lastBooking",
        query = "select distinct first_value(id) over win_lb as id, first_value(item_id) over win_lb as itemId, " +
                "first_value(booker_id) over win_lb as bookerId, " +
                "first_value(start_date) over win_lb as startDate, first_value(end_date) over win_lb as endDate " +
                "from booking where item_id in :itemId and start_date<=:now " +
                "WINDOW win_lb AS (Partition By item_id ORDER BY start_date DESC)", resultSetMapping = "BookingDtoForOwner")
@NamedNativeQuery(name = "nextBooking",
        query = "select distinct first_value(id) over win_nb as id, first_value(item_id) over win_nb as itemId, " +
                "first_value(booker_id) over win_nb as bookerId, " +
                "first_value(start_date) over win_nb as startDate, first_value(end_date) over win_nb as endDate " +
                "from booking where item_id in :itemId and booking.start_date>:now and status in ('APPROVED', 'WAITING') " +
                "WINDOW win_nb AS (Partition By item_id ORDER BY start_date)", resultSetMapping = "BookingDtoForOwner")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_date", nullable = false)
    @JsonProperty(value = "start")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime startDate;
    @Column(name = "end_date", nullable = false)
    @JsonProperty(value = "end")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime endDate;
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    @ToString.Exclude
    private Item item;
    @ManyToOne
    @JoinColumn(name = "booker_id", nullable = false)
    @ToString.Exclude
    private User booker;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

}
