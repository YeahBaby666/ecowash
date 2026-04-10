package utp.edu.pe.ecowash.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    /**
     * Muestra la página de inicio de sesión.
     * Spring Security redirigirá aquí automáticamente cuando se requiera autenticación.
     */
    @GetMapping("/login")
    public String mostrarLogin() {
        // Devuelve el archivo src/main/resources/templates/login.html
        return "login"; 
    }

    /**
     * Muestra la página de registro de usuarios.
     * Al no tener JPA configurado aún, esta ruta solo muestra la vista.
     */
    @GetMapping("/registro")
    public String mostrarRegistro() {
        // Devuelve el archivo src/main/resources/templates/register.html
        return "register"; 
    }
}