package utp.edu.pe.ecowash.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import utp.edu.pe.ecowash.model.entity.Cliente;
import utp.edu.pe.ecowash.model.repository.ClienteRepository;

@Controller
public class InicioController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 1. Función para abrir el HTML principal (Prototipo Cliente Web).
     * Corresponde a la ruta raíz de la aplicación.
     */
    @GetMapping("/")
    public String mostrarInicio() {
        // Devuelve el archivo src/main/resources/templates/index.html
        return "index";
    }

    /**
     * 2. Función para abrir el HTML de inicio de sesión.
     * Spring Security redirigirá aquí cuando se requiera autenticación.
     */
    @GetMapping("/login")
    public String mostrarLogin() {
        // Devuelve el archivo src/main/resources/templates/login.html
        return "login"; 
    }

    /**
     * 3. Función para abrir el HTML de registro.
     * Por el momento, devuelve solo la vista sin lógica de JPA/Guardado.
     */
    @GetMapping("/registro")
    public String mostrarRegistro() {
        // Devuelve el archivo src/main/resources/templates/register.html
        return "register"; 
    }
    @GetMapping("/contacto")
        public String contacto() {
        return "contacto";
    }
    @GetMapping("/servicios")
    public String servicios() {
    return "services";
    }
    @GetMapping("/nosotros")
    public String nosotros() {
        return "nosotros";
    }
    
    

    /**
     * Función POST para procesar el registro y guardar en la base de datos.
     */
    
    @PostMapping("/registro")
    public String procesarRegistro(@RequestParam("nombre") String nombre,
                                    @RequestParam("email") String email,
                                    @RequestParam("password") String password) {
        
        // 1. Creamos la instancia del nuevo Cliente
        Cliente nuevoCliente = new Cliente();
        nuevoCliente.setNombre(nombre);
        nuevoCliente.setCorreo(email); // Relacionamos el 'email' del formulario HTML con 'correo' de la BD
        
        // 2. Encriptamos la contraseña con BCrypt antes de guardarla
        nuevoCliente.setPassword(passwordEncoder.encode(password));
        
        // 3. Guardamos en la base de datos con JPA
        clienteRepository.save(nuevoCliente);
        System.out.println("Nuevo cliente registrado: " + email);
        System.out.println("Contraseña encriptada: " + nuevoCliente.getPassword());

        // 4. Redirigimos al login tras un registro exitoso
        return "redirect:/login";
    }
}