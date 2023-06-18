package ru.practicum.shareit.booking.model;

import lombok.*;
import org.hibernate.annotations.Type;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_date")
    private LocalDateTime start;
    @Column(name = "end_date")
    private LocalDateTime end;
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Item item;
    @ManyToOne(fetch = FetchType.LAZY)
    private User booker;
    @Column(name = "booker_id", insertable = false, updatable = false)
    private Long bookerId;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @Type(type = "ru.practicum.shareit.booking.EnumTypePostgreSql")
    private BookingStatus status;
}
