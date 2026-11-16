package cl.duoc.huertoescolar.huertoapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/** Errores comunes de los servicios, para no repetir el mismo código. */
public final class Errores {

    private Errores() {
    }

    public static ResponseStatusException noEncontrado(String mensaje) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, mensaje);
    }

    public static ResponseStatusException invalido(String mensaje) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensaje);
    }
}
