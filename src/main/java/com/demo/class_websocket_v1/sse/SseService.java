package com.demo.class_websocket_v1.sse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


// SSE : 브라우저가 한번 열어두면, 서버가 그 연결로 메세지를 계속 밀어넣는다(단방향)
// 방향 서버 -> 브라우저 한쪽이다.
// 브라우저가 별도 HTTP 요청을 보낼떄는 새로운 요청을 만들어야한다
@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {
    //멀티 스레드에서는 arrayList는 안쓰는게 좋다
    // 연결된 탭마다  SseEmitter 하나 맵핑 됨
    private final List<SseEmitter> emitterList = new CopyOnWriteArrayList<>();
    //연결 유지시간 (10분)
    private static final Long TIMEOUT = 10 * 60 * 1000L;

    //브라우저가 연결을 요청하면 SseEmitter를 생성해서 List에 보관하고 돌려준다.
    public SseEmitter connect() {
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        emitterList.add(emitter);

        // 연결이 정상 종료되면 목록에서 뺸다.
        emitter.onCompletion(() -> emitterList.remove(emitter));
        //시간이 지나면 목록에서 뺴고 연결을 마무리한다
        emitter.onTimeout(() -> {
            emitterList.remove(emitter);
            emitter.complete();
        });
        //네트워크 오류등으로 끊기면 목록에서뺸다
        emitter.onError(e -> emitterList.remove(emitter));

        // 연결 확인 이벤트를 바로 브라우저 측으로 한번 보낸다.
        // 첫 데이터가 나갈떄 응답 헤더도 함께 전송되어 브라우저의 onopen 이벤트가 실행된다
        // 아무것고 보내지 않은 채  TIMEOUT이 지나면 스프링이 503 응답으로 끝내므로, 한건은 먼저 보내둔다

        try {
            emitter.send(SseEmitter.event().name("connect").data("연결 성공"));

        } catch (IOException e) {
            emitterList.remove(emitter);
        }

        log.info("SSE 연결추가 , 현재 연결수 : {}", emitterList.size());

        return emitter; //왜 브라우저의 자바스크립트 측 코드로 던짐
    }

    //연결된 모든 브라우저에 "chat" 이벤트로 메시지를 보낸다

    public void send(String msg) {
        for (SseEmitter emitter : emitterList) {
            try {
                emitter.send(SseEmitter.event().name("chat").data(msg));
            } catch (IOException e) {
                //이미 끓긴 연결은 보내기에 실패하므로 목록에서뺀다
                emitterList.remove(emitter);
            }
        }
    }


}
