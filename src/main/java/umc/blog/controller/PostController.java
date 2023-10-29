package umc.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.blog.dto.PostDto;
import umc.blog.dto.PostEditDto;
import umc.blog.exception.InputValidateException;
import umc.blog.exception.ExceptionResponse;
import umc.blog.exception.PermissionException;
import umc.blog.exception.TargetNotFoundException;
import umc.blog.service.PostService;
import umc.blog.util.UserUtil;

@RestController
@RequestMapping("/post")
public class PostController {
    private final PostService postService;
    private final UserUtil userUtil;

    @Autowired
    public PostController(PostService postService, UserUtil userUtil) {
        this.postService = postService;
        this.userUtil = userUtil;
    }

    // 특정 글 조회 (id)
    @GetMapping("/{id}")
    ResponseEntity<?> findPostById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(postService.findOne(id));
        } catch (TargetNotFoundException e) {
            return errorMessage(e.getMessage());
        }
    }

    // 글 생성
    @PostMapping
    ResponseEntity<?> createPost(@RequestBody PostDto postDto) {
        try {
            return ResponseEntity.ok(postService.write(postDto, userUtil.getLoggedInUser()));
        } catch (InputValidateException | TargetNotFoundException e) {
            return errorMessage(e.getMessage());
        }
    }

    // 글 수정
    @PutMapping("/{id}")
    ResponseEntity<?> editPost(@PathVariable Long id, @RequestBody PostEditDto editDto) {
        try {
            return ResponseEntity.ok(postService.edit(id, editDto, userUtil.getLoggedInUser()));
        } catch (InputValidateException | TargetNotFoundException | PermissionException e) {
            return errorMessage(e.getMessage());
        }
    }

    // 글 삭제
    @DeleteMapping("/{id}")
    ResponseEntity<?> deletePost(@PathVariable Long id) {
        try {
            postService.delete(id, userUtil.getLoggedInUser());
            return ResponseEntity.status(HttpStatus.OK).body("글 삭제 완료");
        } catch (TargetNotFoundException | PermissionException e) {
            return errorMessage(e.getMessage());
        }
    }

    private static ResponseEntity<ExceptionResponse> errorMessage(String e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(exceptionResponse);
    }
}
