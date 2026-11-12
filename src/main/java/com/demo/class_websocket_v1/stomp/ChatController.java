package com.demo.class_websocket_v1.stomp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@Controller
@Slf4j
public class ChatController {

    //서버 어디서든지 특정 주소(/sub/...)을 구독한브라우저에게 메시지를 보낼수있게해주는 도구
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;


    //첫 화면: 자동으로 1번방으로 보냄
    @GetMapping("/")
    public String home() {
        return "redirect:/rooms/1";
    }


    @GetMapping("/rooms/{roomId}")
    public String roomForm(@PathVariable("roomId") Integer roomId, Model model) {
        model.addAttribute("roomId", roomId);
        model.addAttribute("chats", chatService.findByRoomId(roomId));
        return "chat/index";
    }

    //채팅 메시지 수신
    //이 메서드는 url로 불리지않는다
    // 브라우저는 ws://localhost:8080/ws-stomp 로 연결을 한번 열어두고,
    // 그 연결 안으로 destination:/pub/chat/{1}이 적힌 SEND 프레임을 보낸다
    // 스프링이 /pub을 떼고 남은 chat/1을 보고 메서드를 찾는다

    @MessageMapping("chat/{roomId}")
    public void sendMessage(@DestinationVariable("roomId") Integer roomId,
                            ChatMessageDto request) {
        log.info("수신: {}번방, {}", roomId, request);

        //빈메세지를 거른다 (브라우저검사는 우회할수있으므로 서버에서 확인)
        if (request.msg() == null || request.msg().isBlank()) {
            return;
        }
        //이름이 비어있으면 "익명"으로 둔다
        String sender = (request.sender() == null || request.sender().isBlank()) ? "익명" : request.sender().trim();

        //DB에 저장
        Chat chat = chatService.save(roomId, sender, request.msg().trim());
        //중요 메세지 브로커에게 전달
        // 3편에서 만들었던 직접 작성한 broadcast 반복문이 이 한줄로 끝난다
        messagingTemplate.convertAndSend("/sub/chat/" + roomId,
                new ChatMessageDto(chat.getSender(), chat.getMsg()));
    }


}
