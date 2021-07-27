package com.example.demo.src.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
// 컨트롤러 클래스를 지정해주기 위한 어노테이션을 말한다.
// @Controller에 @ResponseBody가 결합된 어노테이션이다.
// 따라서 @RestController를 붙이면, 컨트롤러 클래스의 각 메서드마다 @ResponseBody를 추가할 필요가 없다.
@RequestMapping("/test")
// 클라이언트가 URL로 요청을 전송하고 할때,
// 해당 요청 URL을 어떤 메서드가 처리할지 Mapping하는 것을 명시해주는 어노테이션이다.
public class TestController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    // getClass()메소드 : 현재 참조하고 있는 클래스를 확인할 수 있는 메소드이다.

    @Autowired
    // 의존성 주입을 위한 것이다. 즉 객체 생성을 자동으로 해주는 역할을 한다.
    // 단일 생성자의 경우에는 @Autowired 어노테이션이 생략 가능하다.
    // 생성자가 2개 이상인 겨웅에는 어노테이션을 붙여주어야 한다.
    public TestController() {}

    /**
     * 로그 테스트 API
     * [GET] /test/log
     * @return String
     */
    @ResponseBody
    // @ResponseBody : 자바 객체를 HTTP객체로 변환하여, 클라이언트에게 전송한다.
    // @RequestBody : HTTP 객체를 자바 객체로 변환하여, 서버에서 이용한다.
    @GetMapping("/log")
    // HTTP GET 요청을 처리하는 메소드를 Mapping하는 어노테이션이다.
    public String getAll() {
        System.out.println("테스트");
//        trace, debug 레벨은 Console X, 파일 로깅 X
//        logger.trace("TRACE Level 테스트");
//        logger.debug("DEBUG Level 테스트");

//        info 레벨은 Console 로깅 O, 파일 로깅 X
        logger.info("INFO Level 테스트");
//        warn 레벨은 Console 로깅 O, 파일 로깅 O
        logger.warn("Warn Level 테스트");
//        error 레벨은 Console 로깅 O, 파일 로깅 O (app.log 뿐만 아니라 error.log 에도 로깅 됨)
//        app.log 와 error.log 는 날짜가 바뀌면 자동으로 *.gz 으로 압축 백업됨
        logger.error("ERROR Level 테스트");

        return "Success Test";
    }
}
