package com.hieutt.blogRESTapi.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"email"})
        }
)

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String displayedName;
    private String username;
    private String email;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(
            mappedBy = "author",
            cascade = CascadeType.ALL
    )
    @ToString.Exclude
    private List<Post> posts = new ArrayList<>();

    @OneToMany(
            mappedBy = "author",
            cascade = CascadeType.ALL
    )
    @ToString.Exclude
    private List<Comment> comments = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "user_bookmarks",
            joinColumns = @JoinColumn(
                    name = "user_id",
                    referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "post_id",
                    referencedColumnName = "id"
            )
    )
    private List<Post> bookmarks = new ArrayList<>();


    @ManyToMany
    @JoinTable(
            name = "user_followers",
            joinColumns = @JoinColumn(
                    name = "user_id",
                    referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "follower_id",
                    referencedColumnName = "id"
            )
    )
    private List<User> followers = new ArrayList<>();


    @ManyToMany(mappedBy = "followers")
    private List<User> followings = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "user")
    private List<Token> tokens;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
