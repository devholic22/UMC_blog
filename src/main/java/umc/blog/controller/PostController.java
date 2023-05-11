package umc.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.blog.dto.PostDto;
import umc.blog.entity.Post;
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
    ResponseEntity<Post> createPost(@RequestBody PostDto postDto) {
        return ResponseEntity.ok(postService.write(postDto));
    }
}
