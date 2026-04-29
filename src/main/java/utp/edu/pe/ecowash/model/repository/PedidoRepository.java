package utp.edu.pe.ecowash.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utp.edu.pe.ecowash.model.entity.Pedido;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    // Obtiene todos los pedidos de un cliente, ordenados del más reciente al más antiguo
    List<Pedido> findByClienteCorreoOrderByFechaServicioDesc(String correo);

    // Busca un ticket específico para el buscador público
    Optional<Pedido> findByCodigo(String codigo);
}