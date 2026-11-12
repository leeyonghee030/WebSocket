package com.demo.class_websocket_v1.stomp;

// 브라우저와 주고받는 메세지 한건 (Json)
// {"sender": "홍길동", "message": "안녕"}
// record : 가로안에 필드를 선언하면 생성자, 접근자, equals, toString 자동으로 만들어주는 불변객체
public record ChatMessageDto(String sender, String msg) {}

