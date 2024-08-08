package com.complete.todayspace.domain.oauth.service;

import com.complete.todayspace.domain.oauth.dto.OAuthDto;
import com.complete.todayspace.domain.user.entity.User;
import com.complete.todayspace.domain.user.entity.UserRole;
import com.complete.todayspace.domain.user.entity.UserState;
import com.complete.todayspace.domain.user.repository.UserRepository;
import com.complete.todayspace.global.jwt.JwtProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.SecureRandom;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final JwtProvider jwtProvider;

    @Value("${oauth.rest.api.key.kakao}")
    private String KAKAO_CLIENT_ID;

    @Value("${oauth.rest.api.key.naver}")
    private String NAVER_CLIENT_ID;

    @Value("${oauth.rest.api.secret.key.naver}")
    private String NAVER_CLIENT_SECRET;

    @Value("${oauth.rest.api.key.google}")
    private String GOOGLE_CLIENT_ID;

    @Value("${oauth.rest.api.secret.key.google}")
    private String GOOGLE_CLIENT_SECRET;

    @Transactional
    public HttpHeaders kakao(String code) throws JsonProcessingException {

        String token = getKakaoToken(code, KAKAO_CLIENT_ID);
        OAuthDto oAuthDto = getKakaoUserInfo(token);
        User kakaoUser = registerOAuthUserIfNeeded(oAuthDto);

        String accessToken = jwtProvider.generateAccessToken(kakaoUser.getUsername(), kakaoUser.getRole().toString());
        String refreshToken = jwtProvider.generateRefreshToken(kakaoUser.getUsername(), kakaoUser.getRole().toString());
        ResponseCookie responseCookie = jwtProvider.createRefreshTokenCookie(refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.add(HttpHeaders.SET_COOKIE, responseCookie.toString());

        kakaoUser.updateRefreshToken(refreshToken);
        userRepository.save(kakaoUser);

        return headers;
    }

    @Transactional
    public HttpHeaders naver(String code) throws JsonProcessingException {

        String token = getNaverToken(code, NAVER_CLIENT_ID, NAVER_CLIENT_SECRET);
        OAuthDto oAuthDto = getNaverUserInfo(token);
        User naverUser = registerOAuthUserIfNeeded(oAuthDto);

        String accessToken = jwtProvider.generateAccessToken(naverUser.getUsername(), naverUser.getRole().toString());
        String refreshToken = jwtProvider.generateRefreshToken(naverUser.getUsername(), naverUser.getRole().toString());
        ResponseCookie responseCookie = jwtProvider.createRefreshTokenCookie(refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.add(HttpHeaders.SET_COOKIE, responseCookie.toString());

        naverUser.updateRefreshToken(refreshToken);
        userRepository.save(naverUser);

        return headers;
    }

    @Transactional
    public HttpHeaders google(String code) throws JsonProcessingException {

        String token = getGoogleToken(code, GOOGLE_CLIENT_ID, GOOGLE_CLIENT_SECRET);
        OAuthDto oAuthDto = getGoogleUserInfo(token);
        User googleUser = registerOAuthUserIfNeeded(oAuthDto);

        String accessToken = jwtProvider.generateAccessToken(googleUser.getUsername(), googleUser.getRole().toString());
        String refreshToken = jwtProvider.generateRefreshToken(googleUser.getUsername(), googleUser.getRole().toString());
        ResponseCookie responseCookie = jwtProvider.createRefreshTokenCookie(refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.add(HttpHeaders.SET_COOKIE, responseCookie.toString());

        googleUser.updateRefreshToken(refreshToken);
        userRepository.save(googleUser);

        return headers;
    }

    private String getKakaoToken(String code, String CLIENT_ID) throws JsonProcessingException {

        URI uri = UriComponentsBuilder.fromUriString("https://kauth.kakao.com")
                .path("/oauth/token")
                .encode()
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", CLIENT_ID);
        body.add("redirect_uri", "https://today-space.com/oauth/kakao");
        body.add("code", code);

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity.post(uri)
                .headers(headers)
                .body(body);
        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());

        return jsonNode.get("access_token").asText();
    }

    private String getNaverToken(String code, String NAVER_CLIENT_ID, String NAVER_CLIENT_SECRET) throws JsonProcessingException {

        URI uri = UriComponentsBuilder.fromUriString("https://nid.naver.com/oauth2.0/token")
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", NAVER_CLIENT_ID)
                .queryParam("client_secret", NAVER_CLIENT_SECRET)
                .queryParam("redirect_uri", "http://localhost:3000/oauth/naver")
                .queryParam("code", code)
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RequestEntity<Void> requestEntity = RequestEntity.post(uri)
                .headers(headers)
                .build();
        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());

        return jsonNode.get("access_token").asText();
    }

    private String getGoogleToken(String code, String GOOGLE_CLIENT_ID, String GOOGLE_CLIENT_SECRET) throws JsonProcessingException {

        URI uri = UriComponentsBuilder.fromUriString("https://oauth2.googleapis.com/token")
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", GOOGLE_CLIENT_ID)
                .queryParam("client_secret", GOOGLE_CLIENT_SECRET)
                .queryParam("redirect_uri", "https://today-space.com/oauth/google") // 클라이언트 리디렉션 URI
                .queryParam("code", code)
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded");

        RequestEntity<Void> requestEntity = RequestEntity.post(uri)
                .headers(headers)
                .build();
        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());

        return jsonNode.get("access_token").asText();
    }

    private OAuthDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {

        URI uri = UriComponentsBuilder.fromUriString("https://kapi.kakao.com")
                .path("/v2/user/me")
                .encode()
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity.post(uri)
                .headers(headers)
                .body(new LinkedMultiValueMap<>());
        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        String id = Long.toString(jsonNode.get("id").asLong());
        String nickname = jsonNode.get("properties").get("nickname").asText();

        return new OAuthDto(id, nickname);
    }

    private OAuthDto getNaverUserInfo(String accessToken) throws JsonProcessingException {

        URI uri = UriComponentsBuilder.fromUriString("https://openapi.naver.com/v1/nid/me")
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        RequestEntity<Void> requestEntity = RequestEntity.get(uri)
                .headers(headers)
                .build();
        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        String id = jsonNode.get("response").get("id").asText();
        String nickname = jsonNode.get("response").get("name").asText();

        return new OAuthDto(id, nickname);
    }

    private OAuthDto getGoogleUserInfo(String accessToken) throws JsonProcessingException {

        URI uri = UriComponentsBuilder.fromUriString("https://www.googleapis.com/oauth2/v2/userinfo")
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        RequestEntity<Void> requestEntity = RequestEntity.get(uri)
                .headers(headers)
                .build();
        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        String id = jsonNode.get("id").asText();
        String name = jsonNode.get("name").asText();

        return new OAuthDto(id, name);
    }

    private User registerOAuthUserIfNeeded(OAuthDto oAuthDto) {

        String oAuthId = oAuthDto.getNickname() + oAuthDto.getId();
        User oAuthUser = userRepository.findByoAuthId(oAuthId).orElse(null);

        if (oAuthUser == null) {

            String username = generateRandomUsername();
            String password = UUID.randomUUID().toString();
            String encryptedPassword = passwordEncoder.encode(password);
            oAuthUser = new User(username, encryptedPassword, null, UserRole.USER, UserState.ACTIVE, oAuthId);

        }

        return oAuthUser;
    }

    private String generateRandomUsername() {

        String usernameCharacters = "abcdefghijklmnopqrstuvwxyz0123456789";
        int usernameLength = 20;
        SecureRandom random = new SecureRandom();
        StringBuilder username = new StringBuilder(usernameLength);

        for (int i = 0; i < usernameLength; i++) {

            int index = random.nextInt(usernameCharacters.length());

            username.append(usernameCharacters.charAt(index));

        }

        return username.toString();
    }

}
