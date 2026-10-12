package cl.partnercars.auth.service;

import cl.partnercars.auth.dto.LoginRequest;
import cl.partnercars.auth.dto.LoginResponse;
import cl.partnercars.auth.model.Usuario;
import cl.partnercars.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String TIPO_TOKEN = "Bearer";

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        Authentication autenticacion = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        Usuario usuario = (Usuario) autenticacion.getPrincipal();
        String token = jwtService.generarToken(usuario);
        log.info("Login exitoso de {} con rol {}", usuario.getUsername(), usuario.getRol().getNombre());
        return LoginResponse.builder()
                .token(token)
                .tipo(TIPO_TOKEN)
                .expiraEnSegundos(jwtService.getExpiracionSegundos())
                .username(usuario.getUsername())
                .rol(usuario.getRol().getNombre())
                .build();
    }
}
