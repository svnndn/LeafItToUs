package ru.litu.main_service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ru.litu.main_service.exception.UserAuthException;

@Component
@RequiredArgsConstructor
public class AuthenticationProvider implements org.springframework.security.authentication.AuthenticationProvider {
    private final UserDetailsService userService;

    UserDetails isUserValid(String username, String password) {
        UserDetails user = userService.loadUserByUsername(username);
        if (username.equalsIgnoreCase(user.getUsername())
                && (new BCryptPasswordEncoder().matches(password, user.getPassword()))) {
            UserDetails userDetails = User
                    .withUsername(username)
                    .password("NOT_DISCLOSED")
                    .authorities(((ru.litu.main_service.user.model.User) user).getRoles()) //- todo: убрать когда будут authorities
                    .build();
            return userDetails;
        }
        return null;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserDetails userDetails = isUserValid(username, password);

        if (userDetails != null) {
            return new UsernamePasswordAuthenticationToken(
                    username,
                    password,
                    userDetails.getAuthorities());
        } else {
            throw new UserAuthException("Incorrect user credentials!");
        }
    }

    @Override
    public boolean supports(Class<?> authenticationType) {
        return authenticationType
                .equals(UsernamePasswordAuthenticationToken.class);
    }
}