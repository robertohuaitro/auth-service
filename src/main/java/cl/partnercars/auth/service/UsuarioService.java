package cl.partnercars.auth.service;

import cl.partnercars.auth.dto.ActualizarUsuarioRequest;
import cl.partnercars.auth.dto.RegistroUsuarioRequest;
import cl.partnercars.auth.exception.ConflictoException;
import cl.partnercars.auth.exception.RecursoNoEncontradoException;
import cl.partnercars.auth.model.Rol;
import cl.partnercars.auth.model.Usuario;
import cl.partnercars.auth.repository.RolRepository;
import cl.partnercars.auth.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class UsuarioService {

    public static final String ROL_POR_DEFECTO = "ROLE_USER";

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarioRepository.findAll(Sort.by("id"));
    }

    @Transactional(readOnly = true)
    public Usuario obtener(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario con id " + id));
    }

    @Transactional
    public Usuario registrar(RegistroUsuarioRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new ConflictoException("El username " + request.getUsername() + " ya está registrado");
        }
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new ConflictoException("El email " + request.getEmail() + " ya está registrado");
        }
        Rol rol = rolRepository.findByNombre(ROL_POR_DEFECTO)
                .orElseThrow(() -> new IllegalStateException("No existe el rol por defecto " + ROL_POR_DEFECTO));
        Usuario usuario = Usuario.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .activo(true)
                .rol(rol)
                .build();
        Usuario creado = usuarioRepository.save(usuario);
        log.info("Usuario registrado: {} (id {})", creado.getUsername(), creado.getId());
        return creado;
    }

    @Transactional
    public Usuario actualizar(Long id, ActualizarUsuarioRequest request) {
        Usuario usuario = obtener(id);
        if (usuarioRepository.existsByUsernameAndIdNot(request.getUsername(), id)) {
            throw new ConflictoException("El username " + request.getUsername() + " ya está registrado");
        }
        if (usuarioRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new ConflictoException("El email " + request.getEmail() + " ya está registrado");
        }
        Rol rol = rolRepository.findById(request.getRolId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el rol con id " + request.getRolId()));
        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        usuario.setRol(rol);
        usuario.setActivo(request.getActivo());
        if (request.getPassword() != null) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        Usuario actualizado = usuarioRepository.save(usuario);
        log.info("Usuario actualizado: {} (id {})", actualizado.getUsername(), actualizado.getId());
        return actualizado;
    }

    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = obtener(id);
        usuarioRepository.delete(usuario);
        log.info("Usuario eliminado: {} (id {})", usuario.getUsername(), id);
    }
}
