package com.gachonoj.memberservice.jwt;

import com.gachonoj.memberservice.domain.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    private final Member member;
    public CustomUserDetails(Member member) {
        this.member = member;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority(){
            @Override
            public String getAuthority() {
                return member.getMemberRole().toString();
            }
        });
        return collection;
    }
    @Override
    public String getPassword() {
        return member.getMemberPassword();
    }
    @Override
    public String getUsername() {
        return member.getMemberEmail();
    }
    public Long getMemberId() {
        return member.getMemberId();
    }
    public String getMemberImg() {
        return member.getMemberImg();
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
