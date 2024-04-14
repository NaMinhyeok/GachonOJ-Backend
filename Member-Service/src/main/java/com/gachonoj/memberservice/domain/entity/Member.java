package com.gachonoj.memberservice.domain.entity;

import com.gachonoj.memberservice.domain.constant.MemberLang;
import com.gachonoj.memberservice.domain.constant.Role;
import com.gachonoj.memberservice.domain.dto.request.MemberLangRequestDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Entity
@Table
@Getter
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
    private Integer memberRank;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime memberCreatedDate;
    @Enumerated(EnumType.STRING)
    private MemberLang memberLang;
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks;

    @Builder
    public Member(String memberEmail, String memberPassword, String memberName, String memberNumber, String memberNickname) {
        this.memberEmail = memberEmail;
        this.memberPassword = memberPassword;
        this.memberName = memberName;
        this.memberNumber = memberNumber;
        this.memberRole = Role.ROLE_STUDENT;
        this.memberNickname = memberNickname;
        this.memberRank = 1000;
        this.memberCreatedDate = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
        this.memberLang = MemberLang.C;
    }
    public void updateMemberLang(MemberLangRequestDto memberLangRequestDto) {
        this.memberLang = memberLangRequestDto.getMemberLang();
    }

}
