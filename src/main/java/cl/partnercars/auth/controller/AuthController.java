package cl.partnercars.auth.controller;

import cl.partnercars.auth.dto.LoginRequest;
import cl.partnercars.auth.dto.LoginResponse;
import cl.partnercars.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.debug("POST /login de {}", request.getUsername());
        return ResponseEntity.ok(authService.login(request));
    }
}
