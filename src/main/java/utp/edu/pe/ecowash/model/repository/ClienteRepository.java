package utp.edu.pe.ecowash.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utp.edu.pe.ecowash.model.entity.Cliente;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    // Método para encontrar un cliente usando su correo electrónico
    Optional<Cliente> findByCorreo(String correo);
}