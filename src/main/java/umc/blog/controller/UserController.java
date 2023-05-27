package umc.blog.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.blog.dto.TokenDto;
import umc.blog.dto.UserDto;
import umc.blog.exception.ExceptionResponse;
import umc.blog.exception.InputValidateException;
import umc.blog.exception.UserAlreadyExistException;
import umc.blog.jwt.JwtFilter;
import umc.blog.service.UserService;

@RestController
@RequestMapping("/")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> join(@RequestBody UserDto userDto) {
        try {
            return ResponseEntity.ok(userService.join(userDto));
        } catch (InputValidateException | UserAlreadyExistException e) {
            return errorMessage(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDto userDto) {
        try {
            String jwt = userService.login(userDto);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

            return new ResponseEntity<>(new TokenDto(jwt), httpHeaders, HttpStatus.OK);
        } catch (InputValidateException e) {
            return errorMessage(e.getMessage());
        }
    }

    private static ResponseEntity<ExceptionResponse> errorMessage(String e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(exceptionResponse);
    }
}
