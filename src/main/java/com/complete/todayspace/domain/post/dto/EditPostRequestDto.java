package com.complete.todayspace.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class EditPostRequestDto {
    @NotBlank
    @Length(max = 600)
    private String content;
    private List<String> deleteImageUrls;
    private List<String> newImages;
    private List<String> hashtags;
    private List<String> deleteHashtags;

    public EditPostRequestDto(String content, List<String> deleteImageUrls, List<String> newImages, List<String> hashtags, List<String> deleteHashtags) {
        this.content = content;
        this.deleteImageUrls = deleteImageUrls;
        this.newImages = newImages;
        this.hashtags = hashtags;
        this.deleteHashtags = deleteHashtags;
    }
}
