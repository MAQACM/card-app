package com.app.cards.security;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.app.cards.models.entities.Users;
import com.app.cards.repositories.UsersRepository;

import reactor.core.publisher.Mono;

@Service
public class UserDetailsServiceImpl implements ReactiveUserDetailsService{
    private final UsersRepository usersRepository;

    public UserDetailsServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String email) {
        return usersRepository.findByEmail(email)
                .flatMap(user->Mono.just(build(user)));
    }
    public static UserDetails build(Users user) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole()));

        return new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                user.getEmail(),
                user.getPassword(),
                authorities);
    }
}