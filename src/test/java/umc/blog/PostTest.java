package umc.blog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import umc.blog.entity.Post;
import umc.blog.exception.TargetNotFoundException;
import umc.blog.repository.PostRepository;

import javax.sql.DataSource;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class PostTest {
    private final DataSource dataSource;
    private final PostRepository postRepository;

    @Autowired
    public PostTest(DataSource dataSource, PostRepository postRepository) {
        this.dataSource = dataSource;
        this.postRepository = postRepository;
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
}
