package utp.edu.pe.ecowash.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import utp.edu.pe.ecowash.model.entity.Cliente;
import utp.edu.pe.ecowash.model.repository.ClienteRepository;

import java.security.Principal;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Este método se ejecuta automáticamente antes de renderizar cualquier vista.
     * Inyecta un objeto "cliente" en el modelo (HTML) solo si hay una sesión
     * activa.
     */
    @ModelAttribute("cliente")
    public Cliente inyectarClienteGlobal(Principal principal) {
        if (principal != null && principal.getName() != null) {
            System.out.println("INTENTANDO BUSCAR CLIENTE: " + principal.getName());
            Cliente c = clienteRepository.findByCorreo(principal.getName()).orElse(null);
            if (c == null) {
                System.out.println("CLIENTE NO ENCONTRADO EN LA BD");
            } else {
                System.out.println("CLIENTE INYECTADO AL HTML: " + c.getNombre());
            }
            return c;
        }
        System.out.println("USUARIO NO LOGUEADO O PRINCIPAL NULO");
        return null;
    }
}
