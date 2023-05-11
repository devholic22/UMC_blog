package umc.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.blog.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
