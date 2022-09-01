package com.sparta.avengers.task;

import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


@RequiredArgsConstructor // final 멤버 변수를 자동으로 생성합니다.
@Component // 스프링이 필요 시 자동으로 생성하는 클래스 목록에 추가합니다.
public class Scheduler {

    private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
//    private final BoardRepository boardRepository;
//
//    // 초, 분, 시, 일, 월, 주 순서
//    @Scheduled(cron = "0 0 1 * * *")
//    public void updatePrice() throws InterruptedException {
//        System.out.println("댓글 0인 게시글 전체 삭제 실행");
//        // 댓글이 0인 게시글 조회
////        List<Board> boardList = boardRepository.findAll();
//        for (int i = 0; i < boardList.size(); i++) {
//
//            // 댓글이 0인 게시글 삭제
//            logger.info("게시글<" + boardList[i].name + ">이 삭제되었습니다.")
//            if (boardList[i].comments.length == 0) {
//              boardList[i].delete();
//          }
//        }
//    }
}