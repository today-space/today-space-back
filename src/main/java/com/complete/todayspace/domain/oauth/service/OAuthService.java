package com.complete.todayspace.domain.oauth.service;

import com.complete.todayspace.domain.oauth.dto.OAuthDto;
import com.complete.todayspace.domain.oauth.dto.OAuthResponseDto;
import com.complete.todayspace.domain.user.entity.User;
import com.complete.todayspace.domain.user.entity.UserRole;
import com.complete.todayspace.domain.user.entity.UserState;
import com.complete.todayspace.domain.user.service.CommonService;
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

    private final CommonService userCommonService;
    private final PasswordEncoder passwordEncoder;
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

    @Value("${front.url}")
    private String frontURL;

    private static final String BEARER = "Bearer ";

    @Transactional
    public User kakao(String code) throws JsonProcessingException {

        String token = getKakaoToken(code, KAKAO_CLIENT_ID);

        OAuthDto oAuthDto = getKakaoUserInfo(token);

        User user = registerOAuthUserIfNeeded(oAuthDto);

        userCommonService.saveUser(user);

        return user;
    }

    @Transactional
    public User naver(String code) throws JsonProcessingException {

        String token = getNaverToken(code, NAVER_CLIENT_ID, NAVER_CLIENT_SECRET);

        OAuthDto oAuthDto = getNaverUserInfo(token);

        User user = registerOAuthUserIfNeeded(oAuthDto);

        userCommonService.saveUser(user);

        return user;
    }

    @Transactional
    public User google(String code) throws JsonProcessingException {

        String token = getGoogleToken(code, GOOGLE_CLIENT_ID, GOOGLE_CLIENT_SECRET);

        OAuthDto oAuthDto = getGoogleUserInfo(token);

        User user = registerOAuthUserIfNeeded(oAuthDto);

        userCommonService.saveUser(user);

        return user;
    }

    public OAuthResponseDto responseDto(User user) {

        String username = user.getUsername();
        String accessToken = BEARER + jwtProvider.generateAccessToken(user.getUsername(), user.getRole().toString(), user.getId());

        return new OAuthResponseDto(username, accessToken);
    }

    @Transactional
    public HttpHeaders setCookie(User user) {

        String refreshToken = jwtProvider.generateRefreshToken(user.getUsername(), user.getRole().toString(), user.getId());
        Long expiration = jwtProvider.getExpirationLong(refreshToken);
        ResponseCookie responseCookie = jwtProvider.createRefreshTokenCookie(refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, responseCookie.toString());

        userCommonService.saveRefreshToken(user.getId(), refreshToken, expiration);

        return headers;
    }

    private String getKakaoToken(
            String code,
            String CLIENT_ID
    ) throws JsonProcessingException {

        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", CLIENT_ID);
        params.add("redirect_uri", frontURL + "/oauth/kakao");
        params.add("code", code);

        return getToken(tokenUrl, params);
    }

    private String getNaverToken(
            String code,
            String NAVER_CLIENT_ID,
            String NAVER_CLIENT_SECRET
    ) throws JsonProcessingException {

        String tokenUrl = "https://nid.naver.com/oauth2.0/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", NAVER_CLIENT_ID);
        params.add("client_secret", NAVER_CLIENT_SECRET);
        params.add("redirect_uri", frontURL + "/oauth/naver");
        params.add("code", code);

        return getToken(tokenUrl, params);
    }

    private String getGoogleToken(
            String code,
            String GOOGLE_CLIENT_ID,
            String GOOGLE_CLIENT_SECRET
    ) throws JsonProcessingException {

        String tokenUrl = "https://oauth2.googleapis.com/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", GOOGLE_CLIENT_ID);
        params.add("client_secret", GOOGLE_CLIENT_SECRET);
        params.add("redirect_uri", frontURL + "/oauth/google");
        params.add("code", code);

        return getToken(tokenUrl, params);
    }

    private String getToken(String tokenUrl, MultiValueMap<String, String> params) throws JsonProcessingException {

        URI uri = UriComponentsBuilder.fromUriString(tokenUrl)
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity.post(uri)
                .headers(headers)
                .body(params);

        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());

        return jsonNode.get("access_token").asText();
    }

    private OAuthDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {

        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        JsonNode jsonNode = getUserInfo(userInfoUrl, accessToken);

        String id = Long.toString(jsonNode.get("id").asLong());
        String nickname = jsonNode.get("properties").get("nickname").asText();

        return new OAuthDto(id, nickname);
    }

    private OAuthDto getNaverUserInfo(String accessToken) throws JsonProcessingException {

        String userInfoUrl = "https://openapi.naver.com/v1/nid/me";

        JsonNode jsonNode = getUserInfo(userInfoUrl, accessToken);

        String id = jsonNode.get("response").get("id").asText();
        String nickname = jsonNode.get("response").get("name").asText();

        return new OAuthDto(id, nickname);
    }

    private OAuthDto getGoogleUserInfo(String accessToken) throws JsonProcessingException {

        String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";

        JsonNode jsonNode = getUserInfo(userInfoUrl, accessToken);

        String id = jsonNode.get("id").asText();
        String nickname = jsonNode.get("name").asText();

        return new OAuthDto(id, nickname);
    }

    private JsonNode getUserInfo(String userInfoUrl, String accessToken) throws JsonProcessingException {

        URI uri = UriComponentsBuilder.fromUriString(userInfoUrl)
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RequestEntity<Void> requestEntity = RequestEntity.get(uri)
                .headers(headers)
                .build();

        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

        return new ObjectMapper().readTree(response.getBody());
    }

    private User registerOAuthUserIfNeeded(OAuthDto oAuthDto) {

        String oAuthId = oAuthDto.getNickname() + oAuthDto.getId();
        User oAuthUser = userCommonService.findByoAuthId(oAuthId);

        if (oAuthUser == null) {

            String username = generateRandomUsername();
            String password = UUID.randomUUID().toString();
            String encryptedPassword = passwordEncoder.encode(password);
            oAuthUser = new User(
                    username,
                    encryptedPassword,
                    "https://today-space.s3.ap-northeast-2.amazonaws.com/profile/defaultProfileImg.png",
                    UserRole.USER,
                    UserState.ACTIVE,
                    oAuthId
            );

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
