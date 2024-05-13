package org.kosandron.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "owner")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Owner {
    @Id
    @SequenceGenerator(name = "owner_seq", sequenceName = "owner_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "owner_seq", strategy = GenerationType.SEQUENCE)
    @Column(name = "id", unique = true, nullable = false)
    @EqualsAndHashCode.Include
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "birthday")
    private LocalDate birthday;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="owner_cat", joinColumns = @JoinColumn(name = "owner_id"))
    @Column(name = "cat")
    private Set<Long> cats;

    public Owner(String name, LocalDate birthday) {
        this.name = name;
        this.birthday = birthday;
        this.cats = new HashSet<>();
    }

    public void addCat(Long id) {
        if (cats.contains(id)) {
            return;
        }

        cats.add(id);
    }

    public void removeCat(Long id) {
        cats.remove(id);
    }
}
