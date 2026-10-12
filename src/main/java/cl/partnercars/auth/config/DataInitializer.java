package cl.partnercars.auth.config;

import cl.partnercars.auth.model.Rol;
import cl.partnercars.auth.model.Usuario;
import cl.partnercars.auth.repository.RolRepository;
import cl.partnercars.auth.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        Rol rolAdmin = crearRolSiNoExiste("ROLE_ADMIN", "Administra el catálogo, el contenido y las solicitudes");
        crearRolSiNoExiste("ROLE_USER", "Cliente registrado en el sitio");
        if (!usuarioRepository.existsByUsername(adminUsername)) {
            usuarioRepository.save(Usuario.builder()
                    .username(adminUsername)
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .activo(true)
                    .rol(rolAdmin)
                    .build());
            log.info("Usuario administrador {} creado", adminUsername);
        }
    }

    private Rol crearRolSiNoExiste(String nombre, String descripcion) {
        return rolRepository.findByNombre(nombre).orElseGet(() -> {
            Rol rol = rolRepository.save(Rol.builder().nombre(nombre).descripcion(descripcion).build());
            log.info("Rol {} creado", nombre);
            return rol;
        });
    }
}
