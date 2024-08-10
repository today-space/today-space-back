package com.complete.todayspace.domain.chat.service;

import com.complete.todayspace.domain.chat.dto.ChatRoomRequestDto;
import com.complete.todayspace.domain.chat.entity.ChatRoom;
import com.complete.todayspace.domain.chat.repository.ChatRoomRepository;
import com.complete.todayspace.domain.product.entity.Product;
import com.complete.todayspace.domain.product.service.ProductService;
import com.complete.todayspace.domain.user.entity.User;
import com.complete.todayspace.domain.user.service.UserService;
import com.complete.todayspace.global.exception.CustomException;
import com.complete.todayspace.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ProductService productService;
    private final UserService userService;

    @Transactional
    public void enterChatRoom(Long id, ChatRoomRequestDto requestDto) {

        Long productId = requestDto.getProductId();
        Long seller = requestDto.getSeller();

        Product product = productService.findByProduct(productId);
        Long productUserId = product.getUser().getId();

        if (!productUserId.equals(seller)) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_OWNER);
        }

        User user = userService.findById(id);
        Long userId = user.getId();
        String username = user.getUsername();

        if (seller.equals(userId)) {
            throw new CustomException(ErrorCode.NO_CHAT_MYSELF);
        }

        String roomId = productId + username;

        if (!chatRoomRepository.existsByRoomId(roomId)) {

            ChatRoom chatRoom = new ChatRoom(roomId, seller, userId);
            chatRoomRepository.save(chatRoom);

        }

    }

}
