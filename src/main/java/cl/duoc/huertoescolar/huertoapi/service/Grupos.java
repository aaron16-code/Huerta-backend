package cl.duoc.huertoescolar.huertoapi.service;
import cl.duoc.huertoescolar.huertoapi.model.Grupo;
import cl.duoc.huertoescolar.huertoapi.repository.GrupoRepository;
import org.springframework.stereotype.Service;
@Service public class Grupos {
 private final GrupoRepository repo;
 public Grupos(GrupoRepository repo) {this.repo=repo;}
 @org.springframework.transaction.annotation.Transactional
 public Grupo resolver(String nombre) {
 if(nombre==null || nombre.isBlank()) return null;
 String limpio=nombre.trim();
 return repo.findByNombreIgnoreCase(limpio).orElseGet(()->{Grupo g=new Grupo();g.setNombre(limpio);return repo.save(g);});
 }
 public static String nombre(Grupo g) {return g==null?null:g.getNombre();}
}
