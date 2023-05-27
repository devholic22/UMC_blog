package umc.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class PostDto {
    private String title;
    private String writer;
    private String content;
}
