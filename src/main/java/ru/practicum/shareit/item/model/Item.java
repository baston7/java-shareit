package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name= "items", schema = "public")
public class Item {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String description;
    @Column(name="is_available")
    private Boolean available;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id")
    private User owner;
    @Transient
    private ItemRequest request;

    public Item(long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }

    public Item(long id, String name, Boolean available) {
        this.id = id;
        this.name = name;
        this.available = available;
    }
}
