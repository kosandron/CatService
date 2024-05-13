package org.kosandron.entities;

import jakarta.persistence.*;
import lombok.*;
import org.kosandron.enums.UserRole;

import java.util.Set;

import static java.util.Collections.emptySet;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "users")
public class User {
    @Id
    @SequenceGenerator(name = "users_seq", sequenceName = "users_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "users_seq", strategy = GenerationType.SEQUENCE)
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Include
    private Long id;
    @Column(unique = true)
    private String login;
    private String password;
    @Column(name = "catownerid")
    private Long catOwnerId;

    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_role")
    @Column(name = "role")
    private Set<UserRole> roles = emptySet();

    public User(String login, String password, Long catOwnerId, Set<UserRole> roles) {
        this.login = login;
        this.password = password;
        this.catOwnerId = catOwnerId;
        this.roles = roles;
    }
}
