package com.demo.class_websocket_v1.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    public List<Chat> findAll(){
        Sort asc = Sort.by(Sort.Direction.ASC, "id");
        return chatRepository.findAll(asc);
    }

    @Transactional
    public Chat save(String msg) {
        Chat chat = Chat.builder()
                .msg(msg)
                .build();
        return chatRepository.save(chat);
    }
}
