package umc.blog.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import umc.blog.dto.UserDto;
import umc.blog.entity.User;
import umc.blog.exception.InputValidateException;
import umc.blog.exception.TargetNotFoundException;
import umc.blog.exception.UserAlreadyExistException;
import umc.blog.jwt.TokenProvider;
import umc.blog.repository.UserRepository;

import java.util.Collections;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository, TokenProvider tokenProvider, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public User join(@RequestBody UserDto userDto) {
        validateUserDtoInput(userDto);

        userRepository.findByUsername(userDto.getUsername()).ifPresent(existingUser -> {
            throw new UserAlreadyExistException("이미 가입되어 있는 유저입니다.");
        });

        User user = new User(userDto.getUsername(), passwordEncoder.encode(userDto.getPassword()));

        return userRepository.save(user);
    }

    public String login(@RequestBody UserDto userDto) {
        validateUserDtoInput(userDto);

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_USER");

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDto.getUsername(),userDto.getPassword(),
                Collections.singleton(simpleGrantedAuthority));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(userDto.getUsername()).orElseThrow(
                () -> new TargetNotFoundException("없는 유저입니다.")
        );

        return tokenProvider.createToken(authentication, user);
    }

    public void validateUserDtoInput(UserDto userDto) {
        if (userDto.getUsername() == null || userDto.getPassword() == null)
            throw new InputValidateException("validation error");
    }
}
