package com.example.spingreactaws101.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    //OncePerRequestFilter는 한 요청당 한 번만 실행된다.

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            //요청에서 토큰 가져오기
            String token = parseBearerToken(request);
            log.info("filter is running..");

            //토큰 검사하기. jwt이므로 인가 서버에 요청하지 않고도 검증 가능
            if (token != null && !token.equalsIgnoreCase("null")) {
                //userId가져오기. 위조된 경우 예외 처리한다
                String userId = tokenProvider.validateAndGetUserId(token);
                log.info("Authenticated user id={}", userId);

                //인증 완료. SecurityContextHolder에 등록해야 인증된 사용자라고 생각한다
                //왜 서버가 이와 같은 정보를 가지고 있어야 할까?
                //요청을 처리하는 과정에서 사용자가 인증됐는지 여부나 인증된 사용자가 누군지 알아야 할 때가 있기 때문이다
                AbstractAuthenticationToken authentication
                    = new UsernamePasswordAuthenticationToken(
                            userId, //인증된 사용자의 정보. 문자열이 아니어도 아무것이나 넣을 수 있다.
                                    //보통 UserDetails라는 오브젝트를 넣는다.
                                    //AuthenticationPrincipal 혹은 principal
                        null, AuthorityUtils.NO_AUTHORITIES);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                securityContext.setAuthentication(authentication);
                SecurityContextHolder.setContext(securityContext);
                //SecurityContextHolder는 ThreadLocal에 저장된다.
                //각 스레드마다 하나의 컨텍스트를 관리할 수 있으며 같은 스레드 내라면 어디서든 접근할 수 있다
            }
        } catch (Exception e) {
            log.error("could not set user authentication in security context", e);
        }
        filterChain.doFilter(request, response);
    }

    private String parseBearerToken(HttpServletRequest request) {
        //http 요청의 헤더를 파싱해 bearer 토큰을 리턴한다.
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
