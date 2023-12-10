package com.example.spingreactaws101.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "username")})
public class UserEntity {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String id;
    @Column(nullable = false)
    private String username;
    
    //db에 비번을 반드시 입력하도록 규제하면 이후 SSO 구현에 문제가 생김
    //대신 회원가입을 구현하는 컨트롤러에서 비번을 반드시 입력하도록 하는 방법이 있음
    private String password;
    private String role;
    private String authProvider;
    
}
