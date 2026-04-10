package utp.edu.pe.ecowash.model.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    /**
     * Configura la cadena de filtros de seguridad para definir el acceso a las rutas.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Rutas públicas que no requieren autenticación (estilos, scripts, imágenes y páginas públicas)
                .requestMatchers("/", "/inicio", "/login", "/registro", "/css/**", "/js/**", "/img/**").permitAll()
                // Cualquier otra solicitud requiere que el usuario esté autenticado
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                // Si planeas usar una página de login personalizada en Thymeleaf, indícala aquí
                .loginPage("/login") 
                .permitAll() // Permite a todos acceder a la página de login
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/") // A dónde redirigir al cerrar sesión
                .permitAll()
            );

        return http.build();
    }

    /**
     * Define el codificador de contraseñas que se utilizará para encriptar y verificar
     * las contraseñas de los usuarios en la base de datos (con JPA).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}