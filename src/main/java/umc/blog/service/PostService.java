package umc.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.blog.dto.PostDto;
import umc.blog.dto.PostEditDto;
import umc.blog.entity.Post;
import umc.blog.entity.User;
import umc.blog.exception.InputValidateException;
import umc.blog.exception.PermissionException;
import umc.blog.exception.TargetNotFoundException;
import umc.blog.repository.PostRepository;

@Service
public class PostService {
    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // 특정 글 조회 (id)
    public PostDto findOne(Long id) {
        Post targetPost = postRepository.findById(id).orElseThrow(() -> new TargetNotFoundException("target not found"));
        return new PostDto(targetPost.getTitle(), targetPost.getWriter().getUsername(), targetPost.getContent());
    }

    // 글 생성
    @Transactional
    public PostDto write(PostDto postDto, User user) {
        validatePostDtoInput(postDto);

        Post newPost = Post.builder().
                        writer(user).
                        title(postDto.getTitle()).
                        content(postDto.getContent()).
                        build();

        postRepository.save(newPost);

        return new PostDto(postDto.getTitle(), user.getUsername(), postDto.getContent());
    }

    // 글 수정
    @Transactional
    public PostDto edit(Long id, PostEditDto editDto, User user) {
        validatePostEditDtoInput(editDto);

        Post targetPost = postRepository.findById(id).orElseThrow(
                () -> new TargetNotFoundException("target not found")
        );

        validatePostAuth(targetPost, user);

        targetPost.setTitle(editDto.getTitle());
        targetPost.setContent(editDto.getContent());

        return new PostDto(targetPost.getTitle(), user.getUsername(), targetPost.getContent());
    }

    // 글 삭제
    @Transactional
    public void delete(Long id, User user) {
        Post targetPost = postRepository.findById(id).orElseThrow(() -> new TargetNotFoundException("target not found"));

        validatePostAuth(targetPost, user);

        postRepository.deleteById(id);
    }

    private void validatePostEditDtoInput(PostEditDto editDto) {
        if (editDto.getTitle() == null || editDto.getContent() == null)
            throw new InputValidateException("validation error");
    }

    private void validatePostDtoInput(PostDto postDto) {
        if (postDto.getTitle() == null || postDto.getContent() == null)
            throw new InputValidateException("validation error");
    }

    private void validatePostAuth(Post post, User user) {
        if (!post.getWriter().equals(user))
            throw new PermissionException("권한이 없습니다.");
    }
}