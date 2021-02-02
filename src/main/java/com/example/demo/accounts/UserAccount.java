package com.example.demo.accounts;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class UserAccount extends User {
    private Account account;

    public UserAccount(Account account) {
        super(account.getEmail(), account.getPassword(),authorities(account.getRoles()));
        this.account = account;
    }

    private static Collection<? extends GrantedAuthority> authorities(Set<AccountRole> roles) {
        return roles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r.name())).collect(
                Collectors.toSet());
    }
}

