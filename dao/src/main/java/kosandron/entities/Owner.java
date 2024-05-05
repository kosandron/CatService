package kosandron.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "owner", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<Cat> cats;

    public Owner(String name, LocalDate birthday) {
        this.name = name;
        this.birthday = birthday;
        this.cats = new ArrayList<>();
    }
}
