package cl.partnercars.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Pattern(regexp = "ROLE_[A-Z_]+", message = "El nombre del rol debe tener el formato ROLE_NOMBRE en mayúsculas")
    @Size(max = 30, message = "El nombre del rol no puede superar los 30 caracteres")
    @Column(name = "nombre", nullable = false, unique = true, length = 30)
    private String nombre;

    @Size(max = 150, message = "La descripción no puede superar los 150 caracteres")
    @Column(name = "descripcion", length = 150)
    private String descripcion;

    @JsonIgnore
    @OneToMany(mappedBy = "rol")
    private List<Usuario> usuarios;
}
