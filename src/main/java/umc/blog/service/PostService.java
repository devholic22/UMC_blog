package umc.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.blog.dto.PostDto;
import umc.blog.entity.Post;
import umc.blog.repository.PostRepository;

@Service
public class PostService {
    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // 글 생성
    @Transactional
    public Post write(PostDto postDto) {
        Post newPost = Post.builder().
                writer("익명"). // 원래 FK이기 때문에 숫자를 정의하는 게 좋지만 익명임을 나타내기 위해 이렇게 설정
                        title(postDto.getTitle()).
                content(postDto.getContent()).
                build();
        postRepository.save(newPost);
        return newPost;
    }
}