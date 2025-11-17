package com.billyow.app.boardang.board.service;

import com.billyow.app.boardang.auth.service.AuthService;
import com.billyow.app.boardang.board.DTO.BoardResponse;
import com.billyow.app.boardang.board.DTO.BoardSummaryResponse;
import com.billyow.app.boardang.board.DTO.CreateBoardRequest;
import com.billyow.app.boardang.board.mapper.BoardMapper;
import com.billyow.app.boardang.board.model.Board;
import com.billyow.app.boardang.board.repository.IBoardRepository;
import com.billyow.app.boardang.boardColumn.mapper.BoardColumnMapper;
import com.billyow.app.boardang.boardColumn.repository.IBoardColumnRepository;
import com.billyow.app.boardang.task.repository.ITaskRepository;
import com.billyow.app.boardang.user.mapper.UserMapper;
import com.billyow.app.boardang.user.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;


@RequiredArgsConstructor
@Service
public class BoardServiceImpl implements IBoardService {
    private final IBoardRepository boardRepository;
    private final AuthService authService;
    private final ITaskRepository taskRepository;
    private final IBoardColumnRepository columnRepository;
    private final IUserRepository userRepository;
    private final BoardMapper boardMapper;
    private final UserMapper userMapper;
    private final BoardColumnMapper boardColumnMapper;

    @Transactional(readOnly = true)
    @Override
    public List<BoardSummaryResponse> getCurrentUserBoards() {
        var currentUser = authService.getCurrentUserId();
        var boards = boardRepository.findAllBoardsFromUser_Id(currentUser);
        return boards.stream()
                .map(boardMapper::toSummaryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public BoardResponse getBoard(Long boardId) {
        return null;
    }

    @Transactional
    @Override
    public BoardResponse createBoard(CreateBoardRequest request) {
        //create the board entity
        var newBoard = new Board();
        newBoard.setTitle(request.title());
        newBoard.setDescription(request.description());
        var currentUserId = authService.getCurrentUserId();
        var owner = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        newBoard.setOwner(owner);
        newBoard.getMembers().add(owner);
        boardRepository.save(newBoard);
        var ownerResponse = userMapper.toSimpleUserDTOResponse(owner);
        //use the mappers to convert the entity into response
        return boardMapper.toResponse(newBoard,
                ownerResponse,
                Set.of(ownerResponse),
                List.of()
                );
    }

    @Transactional
    @Override
    public void deleteBoard(Long boardId) {
        Long currentUserId = authService.getCurrentUserId();
        var affected = boardRepository.deleteByIdAndOwnerId(boardId,currentUserId);
        if(affected==1){
            try{
                columnRepository.deleteByBoard_Id(boardId);
                taskRepository.deleteTaskByBoardId(boardId);
            }catch (Exception e){
                throw new RuntimeException("Error deleting tasks or columns",e);
            }
            return;
        }
        // differentiate 403 from 404
        if(boardRepository.existsById(boardId)){
            throw new RuntimeException("User is not the owner of this board");
        }
        throw new RuntimeException("Board not found");
    }


}
