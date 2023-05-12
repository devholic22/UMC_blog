package umc.blog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Transactional;
import umc.blog.dto.PostDto;
import umc.blog.dto.PostEditDto;
import umc.blog.entity.Post;
import umc.blog.exception.InputValidateException;
import umc.blog.exception.TargetNotFoundException;
import umc.blog.repository.PostRepository;
import umc.blog.service.PostService;

import javax.sql.DataSource;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class PostTest {
    private final DataSource dataSource;
    private final PostRepository postRepository;
    private final PostService postService;

    @Autowired
    public PostTest(DataSource dataSource, PostRepository postRepository, PostService postService) {
        this.dataSource = dataSource;
        this.postRepository = postRepository;
        this.postService = postService;
    }

    @Test
    @DisplayName("글 생성 및 저장 테스트")
    void save() {
        // given
        Post newPost1 = Post.builder().writer("익명").title("첫 번째 글").content("첫 번째 글 내용").build();
        Post newPost2 = Post.builder().writer("익명").title("두 번째 글").content("두 번째 글 내용").build();
        Post newPost3 = Post.builder().writer("익명").title("세 번째 글").content("세 번째 글 내용").build();

        // when
        postRepository.save(newPost1);
        postRepository.save(newPost2);
        postRepository.save(newPost3);
        List<Post> result = postRepository.findAll();

        // then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.contains(newPost1)).isTrue();
        assertThat(result.contains(newPost2)).isTrue();
        assertThat(result.contains(newPost3)).isTrue();
    }

    @Test
    @DisplayName("글 생성 및 저장 테스트 - Service를 사용")
    void saveUsingService() {
        // given
        PostDto postDto = new PostDto();
        postDto.setTitle("첫 번째 글");
        postDto.setContent("첫 번째 글 내용");

        // when
        Post wrotePost = postService.write(postDto);
        Post targetPost = postRepository.findById(wrotePost.getId()).orElseThrow(
                () -> new TargetNotFoundException("target not found")
        );

        // then
        assertThat(wrotePost.getId()).isEqualTo(1);
        assertThat(targetPost).isEqualTo(wrotePost);
    }

    @Test
    @DisplayName("글 생성 및 저장 테스트 - 실패 케이스 (Service 사용)")
    void saveFail() {
        // given
        PostDto postDto = new PostDto();
        postDto.setTitle(null);
        postDto.setContent("첫 번째 글 내용");

        // when & then
        assertThrows(InputValidateException.class, () -> {
            postService.write(postDto);
        });
        assertThrows(NullPointerException.class, () -> {
            postService.write(null);
        });
    }

    @Test
    @DisplayName("글 수정 테스트")
    void edit() {
        // given
        Post newPost1 = Post.builder().writer("익명").title("첫 번째 글").content("첫 번째 글 내용").build();

        // when
        postRepository.save(newPost1);
        newPost1.setTitle("첫 번째 글 (수정)");
        Post targetPost = postRepository.findById(newPost1.getId()).orElseThrow(
                () -> new TargetNotFoundException("target not found"));

        // then
        assertThat(targetPost.getTitle()).isEqualTo("첫 번째 글 (수정)");
    }

    @Test
    @DisplayName("글 수정 테스트 - Service를 사용")
    void editUsingService() {
        // given
        PostDto postDto = new PostDto();
        postDto.setTitle("첫 번째 글");
        postDto.setContent("첫 번째 글 내용");

        // when
        Post targetPost = postService.write(postDto);
        PostEditDto editDto = new PostEditDto();
        editDto.setTitle("첫 번째 글 (수정)");
        editDto.setContent("첫 번째 글 내용");
        postService.edit(targetPost.getId(), editDto);

        // then
        assertThat(targetPost.getTitle()).isEqualTo("첫 번째 글 (수정)");
    }

    @Test
    @DisplayName("글 수정 테스트 - 실패 케이스 (Service 사용)")
    void editFail() {
        // given
        PostDto postDto = new PostDto();
        postDto.setTitle("첫 번째 글");
        postDto.setContent("첫 번째 글 내용");

        // when
        Post targetPost = postService.write(postDto);
        PostEditDto editDto = new PostEditDto();
        editDto.setTitle(null);
        editDto.setContent("첫 번째 글 내용");

        PostEditDto successEditDto = new PostEditDto();
        successEditDto.setContent("첫 번째 글 (수정)");
        successEditDto.setTitle("첫 번째 글 내용");

        // then
        assertThrows(InputValidateException.class, () -> {
            postService.edit(targetPost.getId(), editDto);
        });
        assertThrows(InputValidateException.class, () -> {
            postService.edit(10000L, editDto);
        });
        assertThrows(TargetNotFoundException.class, () -> {
            postService.edit(10000L, successEditDto);
        });
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            postService.edit(null, successEditDto);
        });
        assertThrows(NullPointerException.class, () -> {
            postService.edit(targetPost.getId(), null);
        });
    }

    @Test
    @DisplayName("글 삭제 테스트")
    void delete() {
        // given
        Post newPost1 = Post.builder().writer("익명").title("첫 번째 글").content("첫 번째 글 내용").build();
        Post newPost2 = Post.builder().writer("익명").title("두 번째 글").content("두 번째 글 내용").build();
        Post newPost3 = Post.builder().writer("익명").title("세 번째 글").content("세 번째 글 내용").build();

        // when
        postRepository.save(newPost1);
        postRepository.save(newPost2);
        postRepository.save(newPost3);
        postRepository.deleteById(newPost1.getId());
        List<Post> result = postRepository.findAll();

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.contains(newPost1)).isFalse();
        assertThat(result.contains(newPost2)).isTrue();
        assertThat(result.contains(newPost3)).isTrue();
    }
}
