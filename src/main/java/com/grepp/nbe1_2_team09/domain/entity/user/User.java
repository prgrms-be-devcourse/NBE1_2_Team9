package com.grepp.nbe1_2_team09.domain.entity.user;

import com.grepp.nbe1_2_team09.domain.entity.group.GroupMembership;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_tb")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // 소셜 로그인 제공자
    @Column(nullable = true, length = 50)
    private OAuthProvider provider;

    // 소셜 제공자에서 유저 식별 ID
    @Column(nullable = true, length = 100)
    private String providerId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime signUpDate;

    @UpdateTimestamp
    private LocalDateTime lastLoginDate;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<GroupMembership> groupMemberships = new ArrayList<>();

    //비즈니스 메서드 기본
    @Builder
    public User(String username, String email, String password, Role role, OAuthProvider provider, String providerId) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
    }

    // 외부에 보이는 정보들을 업데이트
    public void updateProfile(String username, String email){
        this.username = username;
        this.email = email;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    //마지막 로그인 시간 업데이트
    public void updateLastLoginDate() {
        this.lastLoginDate = LocalDateTime.now();
    }

}
