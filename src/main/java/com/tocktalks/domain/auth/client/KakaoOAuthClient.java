package com.tocktalks.domain.auth.client;

import com.tocktalks.domain.auth.dto.KakaoTokenResponse;
import com.tocktalks.domain.auth.dto.KakaoUserInfoResponse;
import com.tocktalks.global.config.KakaoOAuthProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class KakaoOAuthClient {

    private static final String AUTHORIZE_URI = "https://kauth.kakao.com/oauth/authorize";
    private static final String TOKEN_URI = "https://kauth.kakao.com/oauth/token";
    private static final String USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";

    private final KakaoOAuthProperties kakaoOAuthProperties;
    private final RestClient restClient = RestClient.create();

    public String buildAuthorizeUrl() {
        return UriComponentsBuilder.fromUriString(AUTHORIZE_URI)
                .queryParam("client_id", kakaoOAuthProperties.getClientId())
                .queryParam("redirect_uri", kakaoOAuthProperties.getRedirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", "profile_nickname,account_email")
                .build()
                .toUriString();
    }

    public KakaoTokenResponse getToken(String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoOAuthProperties.getClientId());
        body.add("client_secret", kakaoOAuthProperties.getClientSecret());
        body.add("redirect_uri", kakaoOAuthProperties.getRedirectUri());
        body.add("code", code);

        return restClient.post()
                .uri(TOKEN_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(KakaoTokenResponse.class);
    }

    public KakaoUserInfoResponse getUserInfo(String kakaoAccessToken) {
        return restClient.get()
                .uri(USER_INFO_URI)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + kakaoAccessToken)
                .retrieve()
                .body(KakaoUserInfoResponse.class);
    }
}
