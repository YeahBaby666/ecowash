package utp.edu.pe.ecowash.model.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import utp.edu.pe.ecowash.model.entity.Cliente;
import utp.edu.pe.ecowash.model.repository.ClienteRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        // 1. Buscamos el cliente en la base de datos por su correo
        Cliente cliente = clienteRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró cuenta con el correo: " + correo));

        // 2. Traducimos nuestro "Cliente" al objeto "UserDetails" que entiende Spring Security
        return User.builder()
                .username(cliente.getCorreo())
                .password(cliente.getPassword()) // La contraseña debe estar encriptada (BCrypt) en la BD
                .roles("CLIENTE") // Le asignamos el rol de cliente por defecto
                .build();
    }
}