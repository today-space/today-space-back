package com.complete.todayspace.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class CreatePostRequestDto {

    @NotBlank
    @Size(max = 600)
    private String content;

    private List<MultipartFile> files;

    private List<String> hashtags;
}
