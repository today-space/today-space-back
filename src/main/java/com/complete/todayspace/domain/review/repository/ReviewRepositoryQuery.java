package com.complete.todayspace.domain.review.repository;

import com.complete.todayspace.domain.review.dto.ReviewResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepositoryQuery {

    Page<ReviewResponseDto> findMyReviewList(Long userId, Pageable pageable);

    Page<ReviewResponseDto> findReviewListByUsername(String username, Pageable pageable);

}
