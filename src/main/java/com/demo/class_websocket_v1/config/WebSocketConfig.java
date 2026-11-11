package com.demo.class_websocket_v1.config;


import com.demo.class_websocket_v1.websocket.ChatWebsocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@RequiredArgsConstructor
@Slf4j
@EnableWebSocket // 반드시 필수
@Configuration
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatWebsocketHandler chatWebsocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 허용 출처를 지정하지 않으면 같은 출처(같은 주소와 포트에서 열린 페이지)의 연결만 받는다
        registry.addHandler(chatWebsocketHandler, "/ws-chat");
//                .setAllowedOrigins("*")
//                .setAllowedOrigins("http://localhost:5173")
        // 허용 출처
    }
}
