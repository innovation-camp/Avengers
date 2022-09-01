package com.sparta.avengers.controller;

import com.sparta.avengers.dto.request.BoardRequestDto;
import com.sparta.avengers.dto.response.ResponseDto;
import com.sparta.avengers.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
public class BoardController {

    private final BoardService boardService;

    //Board
    @RequestMapping(value = "/api/auth/board", method = RequestMethod.POST)
    public ResponseDto<?> createBoard(@RequestPart BoardRequestDto requestDto, @RequestPart MultipartFile multipartFile,
                                      HttpServletRequest request) {
        return boardService.createBoard(requestDto, multipartFile, request);
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
        return boardService.updateBoard(id, BoardRequestDto, request);
    }

    //Delete id
    @RequestMapping(value = "/api/auth/Board/{id}", method = RequestMethod.DELETE)
    public ResponseDto<?> deleteBoard(@PathVariable Long id,
                                     HttpServletRequest request) {
        return boardService.deleteBoard(id, request);
    }

}
