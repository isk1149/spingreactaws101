package com.example.spingreactaws101.service;

import com.example.spingreactaws101.model.TodoEntity;
import com.example.spingreactaws101.persistence.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository repository;

    public String testService() {
        TodoEntity entity = TodoEntity.builder().title("my first todo item").build();
        repository.save(entity);
        TodoEntity savedEntity = repository.findById(entity.getId()).get();

        return savedEntity.getTitle();
    }

    public List<TodoEntity> create(final TodoEntity entity) {
        //validation
        validate(entity);

        repository.save(entity);
        log.debug("entity id={} is saved.", entity.getId());

        return retrieve(entity.getUserId());
    }

    public List<TodoEntity> retrieve(final String userId) {
        log.debug("userId={}", userId);
        return repository.findByUserId(userId);
    }

    public List<TodoEntity> update(final TodoEntity entity) {
        //validation
        validate(entity);

        // 넘겨받은 엔티티id를 이용해 TodoEntitiy를 가져온다. 존재하지 않는 엔티티는 업데이트를 할 수 없기 때문이다.
        final Optional<TodoEntity> original = repository.findById(entity.getId());

        original.ifPresent(todo -> {
            // 반환된 TodoEntitiy가 존재하면 값을 새 엔티티의 값으로 덮어 씌운다.
            todo.setTitle(entity.getTitle());
            todo.setDone(entity.isDone());

            repository.save(todo);
        });

        return retrieve(entity.getUserId());
    }

    public List<TodoEntity> delete(final TodoEntity entity) {
        validate(entity);

        try {
            repository.delete(entity);
        } catch (Exception e) {
            log.error("error deleting entity", entity.getId(), e);
            throw new RuntimeException("error deleting entity " + entity.getId());
        }
        return retrieve(entity.getUserId());
    }

    private static void validate(TodoEntity entity) {
        if (entity == null) {
            log.warn("Entity is null");
            throw new RuntimeException("Entity cannot be null");
        }

        if (entity.getUserId() == null) {
            log.warn("unknown user");
            throw new RuntimeException("Unknown user");
        }
    }
}
