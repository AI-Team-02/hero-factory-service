package ai.herofactoryservice.login.controller;


import ai.herofactoryservice.login.dto.KakaoUserInfoResponseDto;
import ai.herofactoryservice.login.service.KakaoLoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequiredArgsConstructor
@RequestMapping("/api/kakao")
@Slf4j
public class KakaoLoginController {
    private final KakaoLoginService kakaoLoginService;

    @GetMapping("/login")
    public String kakaoLogin() {
        log.info("Kakao login initiated");
        String kakaoAuthUrl = kakaoLoginService.getKakaoLoginUrl();
        return "redirect:" + kakaoAuthUrl;
    }

    @GetMapping("/callback")
    public ResponseEntity<KakaoUserInfoResponseDto> kakaoCallback(@RequestParam String code) {
        log.info("Kakao callback received with code");
        log.debug("code = {}", code);
        return ResponseEntity.ok(kakaoLoginService.processKakaoLogin(code));
    }
}