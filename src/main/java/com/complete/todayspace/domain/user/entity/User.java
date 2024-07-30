package com.complete.todayspace.domain.user.entity;

import com.complete.todayspace.global.entity.AllTimestamp;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "table_user")
@Getter
@NoArgsConstructor
public class User extends AllTimestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column
    private String password;

    @Column
    private String profileImage;

    @Column
    private String refreshToken;

    @Column
    private String oAuthId;

    @Column
    @Enumerated(value = EnumType.STRING)
    private UserRole role;

    @Column
    @Enumerated(value = EnumType.STRING)
    private UserState state;

    public User(String username, String password, String profileImage, UserRole role, UserState state) {
        this.username = username;
        this.password = password;
        this.profileImage = profileImage;
        this.role = role;
        this.state = state;
    }

    public User(String username, String password, String profileImage, UserRole role, UserState state, String oAuthId) {
        this.username = username;
        this.password = password;
        this.profileImage = profileImage;
        this.role = role;
        this.state = state;
        this.oAuthId = oAuthId;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void withdrawal() {
        this.state = UserState.LEAVE;
    }

    public void modifyPassword(String password) {
        this.password = password;
    }

}
