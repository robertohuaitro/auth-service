package cl.partnercars.auth.service;

import cl.partnercars.auth.exception.ConflictoException;
import cl.partnercars.auth.exception.RecursoNoEncontradoException;
import cl.partnercars.auth.model.Rol;
import cl.partnercars.auth.repository.RolRepository;
import cl.partnercars.auth.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<Rol> listar() {
        return rolRepository.findAll(Sort.by("id"));
    }

    @Transactional(readOnly = true)
    public Rol obtener(Long id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el rol con id " + id));
    }

    @Transactional
    public Rol crear(Rol rol) {
        if (rolRepository.existsByNombre(rol.getNombre())) {
            throw new ConflictoException("Ya existe el rol " + rol.getNombre());
        }
        rol.setId(null);
        Rol creado = rolRepository.save(rol);
        log.info("Rol creado: {} (id {})", creado.getNombre(), creado.getId());
        return creado;
    }

    @Transactional
    public Rol actualizar(Long id, Rol datos) {
        Rol rol = obtener(id);
        if (rolRepository.existsByNombreAndIdNot(datos.getNombre(), id)) {
            throw new ConflictoException("Ya existe otro rol con el nombre " + datos.getNombre());
        }
        rol.setNombre(datos.getNombre());
        rol.setDescripcion(datos.getDescripcion());
        Rol actualizado = rolRepository.save(rol);
        log.info("Rol actualizado: {} (id {})", actualizado.getNombre(), actualizado.getId());
        return actualizado;
    }

    @Transactional
    public void eliminar(Long id) {
        Rol rol = obtener(id);
        if (usuarioRepository.existsByRolId(id)) {
            throw new ConflictoException("No se puede eliminar el rol " + rol.getNombre() + " porque tiene usuarios asignados");
        }
        rolRepository.delete(rol);
        log.info("Rol eliminado: {} (id {})", rol.getNombre(), id);
    }
}
