package cl.duoc.huertoescolar.huertoapi.config;

import cl.duoc.huertoescolar.huertoapi.model.Cultivo;
import cl.duoc.huertoescolar.huertoapi.model.EstadoCultivo;
import cl.duoc.huertoescolar.huertoapi.model.EstadoSector;
import cl.duoc.huertoescolar.huertoapi.model.Rol;
import cl.duoc.huertoescolar.huertoapi.model.Sector;
import cl.duoc.huertoescolar.huertoapi.model.Usuario;
import cl.duoc.huertoescolar.huertoapi.repository.CultivoRepository;
import cl.duoc.huertoescolar.huertoapi.repository.SectorRepository;
import cl.duoc.huertoescolar.huertoapi.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Datos de ejemplo la primera vez que arranca el servidor con la base vacía.
 * Los usuarios quedan con id 1 a 4, igual que las cuentas de prueba de la app.
 */
@Component
public class DatosIniciales implements CommandLineRunner {

    private final UsuarioRepository usuarios;
    private final SectorRepository sectores;
    private final CultivoRepository cultivos;

    public DatosIniciales(UsuarioRepository usuarios, SectorRepository sectores, CultivoRepository cultivos) {
        this.usuarios = usuarios;
        this.sectores = sectores;
        this.cultivos = cultivos;
    }

    @Override
    public void run(String... args) {
        if (usuarios.count() == 0) {
            usuarios.save(usuario("Profesora Laura", Rol.ADMINISTRADOR, ""));
            usuarios.save(usuario("Sofía Martínez", Rol.ESTUDIANTE, "Taller Huerto A"));
            usuarios.save(usuario("Tomás González", Rol.ESTUDIANTE, "Taller Huerto A"));
            usuarios.save(usuario("Valentina Rojas", Rol.ESTUDIANTE, "Taller Huerto B"));
        }
        if (sectores.count() == 0) {
            Sector sector = new Sector();
            sector.setNombre("Bancal 1");
            sector.setDescripcion("Sector de hortalizas");
            sector.setUbicacion("Zona norte");
            sector.setEstado(EstadoSector.EN_USO);
            sector.setResponsable(usuarios.findAll().stream().findFirst().orElse(null));
            sector = sectores.save(sector);

            Cultivo cultivo = new Cultivo();
            cultivo.setNombre("Lechugas");
            cultivo.setDescripcion("Cultivo de prueba");
            cultivo.setSector(sector);
            cultivo.setFechaSiembra("01/10/2026");
            cultivo.setFechaCosechaEstimada("30/11/2026");
            cultivo.setFrecuenciaRiegoDias(2);
            cultivo.setEstado(EstadoCultivo.EN_CRECIMIENTO);
            cultivo.setObservaciones("Crecimiento normal");
            cultivos.save(cultivo);
        }
    }

    private Usuario usuario(String nombre, Rol rol, String grupo) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setRol(rol);
        u.setGrupo(grupo);
        return u;
    }
}
