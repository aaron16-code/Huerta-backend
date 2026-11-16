package cl.duoc.huertoescolar.huertoapi.exception;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Convierte los errores en respuestas con un mensaje que la app puede mostrar. */
@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validacion(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getDefaultMessage())
                .collect(Collectors.joining(". "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", mensaje));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> integridad(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", "No se puede completar: hay datos relacionados (por ejemplo, tareas o cultivos de ese registro)"));
    }
}
