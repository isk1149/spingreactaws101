package com.example.spingreactaws101.service;

import com.example.spingreactaws101.model.UserEntity;
import com.example.spingreactaws101.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserEntity create(final UserEntity userEntity) {
        if (userEntity == null || userEntity.getUsername() == null) {
            throw new RuntimeException("Invalid arguments");
        }

        final String username = userEntity.getUsername();
        if (userRepository.existsByUsername(username)) {
            log.warn("username already exists {}",username);
            throw new RuntimeException("username already exists");
        }
        return userRepository.save(userEntity);
    }

    public UserEntity getByCredentials(final String username, final String password, final PasswordEncoder encoder) {
        final UserEntity originalUser = userRepository.findByUsername(username);

        //matches 메소드를 이용해 패스워드가 같은지 확인
        if (originalUser != null && encoder.matches(password, originalUser.getPassword())) {
            return originalUser;
        }
        return null;
    }
}
