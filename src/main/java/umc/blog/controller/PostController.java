package umc.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.blog.dto.PostDto;
import umc.blog.dto.PostEditDto;
import umc.blog.entity.Post;
import umc.blog.exception.InputValidateException;
import umc.blog.exception.ExceptionResponse;
import umc.blog.exception.TargetNotFoundException;
import umc.blog.service.PostService;

@RestController
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    // 글 생성
    @PostMapping
    ResponseEntity<?> createPost(@RequestBody PostDto postDto) {
        try {
            return ResponseEntity.ok(postService.write(postDto));
        } catch (InputValidateException e) {
            return errorMessage(e.getMessage());
        }
    }

    // 글 수정
    @PutMapping("/{id}")
    ResponseEntity<?> editPost(@PathVariable Long id, @RequestBody PostEditDto editDto) {
        try {
            return ResponseEntity.ok(postService.edit(id, editDto));
        } catch (InputValidateException | TargetNotFoundException e) {
            return errorMessage(e.getMessage());
        }
    }

    // 글 삭제
    @DeleteMapping("/{id}")
    ResponseEntity<?> deletePost(@PathVariable Long id) {
        try {
            postService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).body("글 삭제 완료");
        } catch (TargetNotFoundException e) {
            return errorMessage(e.getMessage());
        }
    }

    private static ResponseEntity<ExceptionResponse> errorMessage(String e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(exceptionResponse);
    }
}
