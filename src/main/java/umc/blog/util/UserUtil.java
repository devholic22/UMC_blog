package umc.blog.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import umc.blog.entity.User;
import umc.blog.exception.TargetNotFoundException;
import umc.blog.repository.UserRepository;

@Component
public class UserUtil {

    private final UserRepository userRepository;

    public UserUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username).orElseThrow(
                () -> new TargetNotFoundException("유저가 식별되지 않았습니다.")
        );
    }
}