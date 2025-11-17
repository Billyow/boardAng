package com.billyow.app.boardang.board.service;

import com.billyow.app.boardang.board.DTO.BoardResponse;
import com.billyow.app.boardang.board.DTO.BoardSummaryResponse;
import com.billyow.app.boardang.board.DTO.CreateBoardRequest;
import com.billyow.app.boardang.board.model.Board;

import java.util.List;

public interface IBoardService {
    BoardResponse createBoard(CreateBoardRequest request);
    List<BoardSummaryResponse> getCurrentUserBoards();
    BoardResponse getBoard(Long boardId);
    void deleteBoard(Long boardId);
}
