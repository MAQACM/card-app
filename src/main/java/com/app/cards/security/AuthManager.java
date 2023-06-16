package com.app.cards.security;

import com.app.cards.utils.TokenUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.stream.Collectors;

import static com.app.cards.utils.TokenUtils.getClaim;

@Component
@AllArgsConstructor
public class AuthManager implements ReactiveAuthenticationManager {
    private final TokenUtils tokenUtils;
    private final UserDetailsServiceImpl userDetailsService;
    /*
    * authenticates the spring context
    * */
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
            String authToken = authentication.getCredentials().toString();
            String username = getClaim(authToken).getSubject();
           return this.userDetailsService.findByUsername(username)
                   .flatMap(user-> Mono.just(tokenUtils.validateToken(authToken,user))
                           .filter(valid -> valid)
                           .switchIfEmpty(Mono.empty())
                           .map(valid -> {
                               List<GrantedAuthority> roles=tokenUtils.getRoles(authToken).stream()
                                       .map(role-> new SimpleGrantedAuthority(role.get("authority"))).collect(Collectors.toList());
                               return new UsernamePasswordAuthenticationToken(user, null, roles);
                           }));


    }
    /*
     * Authenticate user on db
     * */
    public Mono<UserDetails>authenticateUser(String email, String pass){
        return this.userDetailsService.findByUsername(email)
                .flatMap(user->{
                    if(user.getPassword().equals(pass)){
                        return Mono.just(user);}
                    return Mono.error(new BadCredentialsException("Invalid credentials"));
                        }).switchIfEmpty(Mono.defer(()->Mono.error(new UsernameNotFoundException("user does not exist"))))
                .onErrorResume(error->Mono.error(new AuthenticationException(error.getMessage())));
    }
}
