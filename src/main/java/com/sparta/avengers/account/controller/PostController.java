package com.sparta.avengers.account.controller;

import com.example.intermediate.controller.request.boardRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.service.BoardService;
import javax.servlet.http.HttpServletRequest;

import com.sparta.avengers.account.controller.request.BoardRequestDto;
import com.sparta.avengers.account.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BoardController<ResponseDto> {

    private final BoardService boardService;

    //Board
    @RequestMapping(value = "/api/auth/board", method = RequestMethod.Board)
    public ResponseDto<?> createBoard(@RequestBody BoardRequestDto requestDto,
                                     HttpServletRequest request) {
        return boardService.createBoard(requestDto, request);
    }

    //Get id
    @RequestMapping(value = "/api/board/{id}", method = RequestMethod.GET)
    public ResponseDto<?> getBoard(@PathVariable Long id) {
        return boardService.getBoard(id);
    }

    //Get
    @RequestMapping(value = "/api/board", method = RequestMethod.GET)
    public ResponseDto<?> getAllBoards() {
        return boardService.getAllBoard();
    }

    //Put id
    @RequestMapping(value = "/api/auth/Board/{id}", method = RequestMethod.PUT)
    public ResponseDto<?> updateBoard(@PathVariable Long id, @RequestBody BoardRequestDto BoardRequestDto,
                                     HttpServletRequest request) {
        return BoardService.updateBoard(id, BoardRequestDto, request);
    }

    //Delete id
    @RequestMapping(value = "/api/auth/Board/{id}", method = RequestMethod.DELETE)
    public ResponseDto<?> deleteBoard(@PathVariable Long id,
                                     HttpServletRequest request) {
        return BoardService.deleteBoard(id, request);
    }

}
