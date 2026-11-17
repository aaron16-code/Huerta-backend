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
            Usuario responsable = usuarios.findAll().stream().findFirst().orElse(null);
            // Mismos ids que usan los datos de ejemplo de la app: sectores 1 a 3, cultivos 1 a 4
            Sector norte = guardarSector("Bancal 1", "Sector de hortalizas", "Zona norte", EstadoSector.EN_USO, responsable);
            Sector sur = guardarSector("Bancal 2", "Sector de tomates y zanahorias", "Zona sur", EstadoSector.EN_USO, responsable);
            Sector invernadero = guardarSector("Invernadero", "Sector protegido para hierbas", "Patio central", EstadoSector.EN_USO, responsable);

            guardarCultivo("Lechuga", "Lechuga de hoja", norte, "01/10/2026", "30/11/2026", 2, EstadoCultivo.EN_CRECIMIENTO);
            guardarCultivo("Tomate", "Tomate cherry", sur, "15/09/2026", "15/12/2026", 2, EstadoCultivo.EN_CRECIMIENTO);
            guardarCultivo("Zanahoria", "Zanahoria en surcos", norte, "05/10/2026", "10/01/2027", 3, EstadoCultivo.SEMBRADO);
            guardarCultivo("Albahaca", "Albahaca en macetas", invernadero, "20/10/2026", "20/12/2026", 1, EstadoCultivo.SEMBRADO);
        }
    }

    private Sector guardarSector(String nombre, String descripcion, String ubicacion, EstadoSector estado, Usuario responsable) {
        Sector sector = new Sector();
        sector.setNombre(nombre);
        sector.setDescripcion(descripcion);
        sector.setUbicacion(ubicacion);
        sector.setEstado(estado);
        sector.setResponsable(responsable);
        return sectores.save(sector);
    }

    private void guardarCultivo(String nombre, String descripcion, Sector sector, String siembra,
                                String cosecha, int riegoCadaDias, EstadoCultivo estado) {
        Cultivo cultivo = new Cultivo();
        cultivo.setNombre(nombre);
        cultivo.setDescripcion(descripcion);
        cultivo.setSector(sector);
        cultivo.setFechaSiembra(siembra);
        cultivo.setFechaCosechaEstimada(cosecha);
        cultivo.setFrecuenciaRiegoDias(riegoCadaDias);
        cultivo.setEstado(estado);
        cultivo.setObservaciones("");
        cultivos.save(cultivo);
    }

    private Usuario usuario(String nombre, Rol rol, String grupo) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setRol(rol);
        u.setGrupo(grupo);
        return u;
    }
}