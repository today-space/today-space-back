package com.complete.todayspace.domain.hashtag.controller;

import com.complete.todayspace.domain.hashtag.service.HashtagService;
import com.complete.todayspace.global.dto.DataResponseDto;
import com.complete.todayspace.global.entity.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/hashtags")
@RequiredArgsConstructor
public class HashtagController {

    private final HashtagService hashtagService;

    @GetMapping
    public ResponseEntity<DataResponseDto<List<String>>> getTop10Hashtags() {
        List<String> topHashtags = hashtagService.getTop10Hashtags();
        DataResponseDto<List<String>> responseDto = new DataResponseDto<>(SuccessCode.HASHTAGS_TOP10, topHashtags);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
