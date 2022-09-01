package com.sparta.avengers.jwt.util;

import com.sparta.avengers.jwt.dto.TokenDto;
import com.sparta.avengers.model.RefreshToken;
import com.sparta.avengers.repository.RefreshTokenRepository;
import com.sparta.avengers.security.user.UserDetailsServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final UserDetailsServiceImpl userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    public static final String ACCESS_TOKEN = "Access-Token";
    public static final String REFRESH_TOKEN = "Refresh-Token";
    private static final long ACCESS_TOKEN_TIME  = 10 * 1000L;
    private static final long REFRESH_TOKEN_TIME  = 100 * 1000L;

    @Value("${jwt.secret.key}")
    private String secretKey;
    Key key;
    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // 토큰 생성
    public TokenDto createAllToken(String userId) {
        return new TokenDto(createToken(userId, "Access"), createToken(userId, "Refresh"));
    }

    public String createToken(String userId, String type) {
        Date date = new Date();

        long time = type.equals("Access") ? ACCESS_TOKEN_TIME : REFRESH_TOKEN_TIME;

        return Jwts.builder()
                .setSubject(userId)
                .setExpiration(new Date(date.getTime() + time))
                .setIssuedAt(date)
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    // header 토큰 가져오는 기능
    public String getTokenFromHeader(HttpServletRequest request, String type) {
        return type.equals("Access") ? request.getHeader(ACCESS_TOKEN) : request.getHeader(REFRESH_TOKEN);
    }

    // 토큰 검증
    public Boolean tokenValidation(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e){
            log.error(e.getMessage());
            return false;
        }
    }

    // refreshToken 검증
    public Boolean refreshTokenValidation(String token) {
        // token 검증, table에 존재하는지 확인, 받아온 refresh token이랑 DB에 있는 토큰이랑 match
        if(!tokenValidation(token)) return false;

        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByAccountUserId(getUserIdFromToken(token));

        return refreshToken.isPresent() && token.equals(refreshToken.get().getRefreshToken());
    }

    // 인증 객체 생성
    public Authentication getAuthentication(String userId) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // userId 가져오는 기능
    public String getUserIdFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }


}
