package utp.edu.pe.ecowash.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import utp.edu.pe.ecowash.model.dto.DatosGeneralesDTO;
import utp.edu.pe.ecowash.model.entity.Cliente;
import utp.edu.pe.ecowash.model.repository.ClienteRepository;

import java.security.Principal;
import java.util.Set; // IMPORTANTE: Cambiado de List a Set

@RestController
@RequestMapping("/api/perfil")
public class PerfilRestController {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public PerfilRestController(ClienteRepository clienteRepository, PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Helper para obtener el cliente actual de la BD
    private Cliente obtenerClienteActual(Principal principal) {
        if (principal == null) throw new RuntimeException("Usuario no autenticado");
        return clienteRepository.findByCorreo(principal.getName())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    @PutMapping("/general")
    @Transactional
    public ResponseEntity<?> actualizarDatosGenerales(@RequestBody DatosGeneralesDTO dto, Principal principal) {
        Cliente cliente = obtenerClienteActual(principal);
        
        cliente.setNombre(dto.nombre());
        // Solo actualizamos la contraseña si el usuario escribió algo distinto a los asteriscos por defecto
        if (dto.password() != null && !dto.password().equals("********") && !dto.password().isBlank()) {
            cliente.setPassword(passwordEncoder.encode(dto.password()));
        }
        
        clienteRepository.save(cliente);
        return ResponseEntity.ok().body("{\"mensaje\": \"Datos actualizados correctamente\"}");
    }

    @PutMapping("/direcciones")
    @Transactional
    public ResponseEntity<?> actualizarDirecciones(@RequestBody Set<String> direcciones, Principal principal) {
        Cliente cliente = obtenerClienteActual(principal);
        // Hibernate se encarga de borrar las anteriores y guardar las nuevas
        cliente.setDirecciones(direcciones);
        clienteRepository.save(cliente);
        return ResponseEntity.ok().body("{\"mensaje\": \"Direcciones guardadas\"}");
    }

    @PutMapping("/telefonos")
    @Transactional
    public ResponseEntity<?> actualizarTelefonos(@RequestBody Set<String> telefonos, Principal principal) {
        Cliente cliente = obtenerClienteActual(principal);
        cliente.setTelefonos(telefonos);
        clienteRepository.save(cliente);
        return ResponseEntity.ok().body("{\"mensaje\": \"Teléfonos guardados\"}");
    }
}