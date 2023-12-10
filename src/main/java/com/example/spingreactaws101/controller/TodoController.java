package com.example.spingreactaws101.controller;

import com.example.spingreactaws101.dto.ResponseDTO;
import com.example.spingreactaws101.dto.TodoDTO;
import com.example.spingreactaws101.model.TodoEntity;
import com.example.spingreactaws101.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService service;

    @GetMapping("/test")
    public ResponseEntity<?> testTodo() {
        String str = service.testService();
        ArrayList<String> list = new ArrayList<>();
        list.add(str);
        ResponseDTO<String> response = ResponseDTO.<String>builder().data(list).build();
        return ResponseEntity.ok().body(response);
    }

    /**
     * @AuthenticationPrincipal - SecurityContextHolder에서 SecurityContext::Authentication
     * 즉, UsernamePasswordAuthenticationToken 오브젝트를 가져온다.
     * 이 오브젝트에서 AuthenticationPrincipal을 가져와 컨트롤러 메서드에 넘겨준다
     * JwtAuthenticationFilter클래스에서 AuthenticationPrincipal을 String형의 오브젝트로 지정했기 때문에
     * @AuthenticationPrincipal의 형으로 String을 사용해야 한다는 걸 알 수 있다
    */
    
    @PostMapping
    public ResponseEntity<?> createTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto) {

        try {
            // (1) todoEntity로 변환
            TodoEntity entity = TodoDTO.toEntity(dto);

            // (2) id를 null로 초기화한다. 생성 당시에는 id가 없어야 하기 때문
            entity.setId(null);

            //(3)Authentication Bearer Token을 통해 받은 userId를 넘긴다.
            entity.setUserId(userId);

            // (4) 엔티티 생성
            List<TodoEntity> entities = service.create(entity);

            // (5) 자바 스트림을 이용해 리턴된 엔티티 리스트를  TodoDTO 리스트로 변환
            //entities.stream().map((item) -> new TodoDTO(item)).collect(Collectors.toList());
            List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

            // (6) 변환된 TodoDTO 리스트를 이용해 ResponseDTO를 초기화한다.
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

            // (7) ResponseDTO를 리턴
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            // (8) 혹시 예외가 나는 경우 dto 대신 error 메시지 넣어 리턴.
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping
    public ResponseEntity<?> retrieveTodoList(@AuthenticationPrincipal String userId) {
        List<TodoEntity> entities = service.retrieve(userId);

        List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

        ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

        return ResponseEntity.ok().body(response);
    }

    @PutMapping
    public ResponseEntity<?> updateTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto) {
        TodoEntity entity = TodoDTO.toEntity(dto);
        entity.setUserId(userId);

        List<TodoEntity> entities = service.update(entity);
        List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

        ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto) {
        try {
            TodoEntity entity = TodoDTO.toEntity(dto);
            entity.setUserId(userId);

            List<TodoEntity> entities = service.delete(entity);
            List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(response);
        }
    }
}
