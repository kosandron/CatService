package org.kosandron.entities;

import jakarta.persistence.*;
import lombok.*;
import org.kosandron.enums.Color;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "cat")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cat {
    @Id
    @SequenceGenerator(name = "cat_seq", sequenceName = "cat_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "cat_seq", strategy = GenerationType.SEQUENCE)
    @Column(name = "id", unique = true, nullable = false)
    @EqualsAndHashCode.Include
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "birthday")
    private LocalDate birthday;
    @Column(name = "breed")
    private String breed;
    @Column(name = "color")
    @Enumerated(EnumType.STRING)
    private Color color;

    @ManyToMany(targetEntity = Cat.class, fetch = FetchType.EAGER)
    @JoinTable(
            name = "catfriends",
            joinColumns = @JoinColumn(name = "catid"),
            inverseJoinColumns = @JoinColumn(name = "friendid")
    )
    @EqualsAndHashCode.Exclude
    private List<Cat> friends;
    private Long owner;

    public Cat(String name, LocalDate birthday, String breed, Color color, Long ownerId) {
        this.name = name;
        this.birthday = birthday;
        this.breed = breed;
        this.color = color;
        this.owner = ownerId;
        friends = new ArrayList<>();
    }

    public void addFriend(Cat cat) {
        if (friends.contains(cat)) {
            return;
        }

        friends.add(cat);
        cat.addFriend(this);
    }

    public void removeFriend(Cat cat) {
        /*Cat friend = friends.get(0);
        boolean a = cat.id.equals(friend.id);
        boolean n = cat.name.equals(friend.name);
        boolean b = cat.birthday.equals(friend.birthday);
        boolean f = cat.friends.equals(friend.friends);
        boolean lo = cat.owner.equals(friend.owner);
        boolean bra = cat.breed.equals(friend.breed);
        boolean c = cat.color.equals(friend.color);*/
        // boolean a = cat.equals(friends.get(0));
        if (!friends.contains(cat)) {
            return;
        }

        friends.remove(cat);
        cat.removeFriend(this);
    }
}
