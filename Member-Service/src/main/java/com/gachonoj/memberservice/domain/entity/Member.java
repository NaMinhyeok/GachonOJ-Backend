package com.gachonoj.memberservice.domain.entity;

import com.gachonoj.memberservice.domain.constant.Role;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;
    @Column(nullable = false)
    private String memberEmail;
    @Column(nullable = false)
    private String memberPassword;
    @Column(nullable = false)
    private String memberName;
    private String memberNumber;
    @Enumerated(EnumType.STRING)
    private Role memberRole;
    private String memberImg;
    private String memberIntroduce;
    @Column(nullable = false)
    private String memberNickname;
    @ColumnDefault("1000")
    private Integer memberRank;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime memberCreatedDate;
    private String memberLang;
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks;
}
