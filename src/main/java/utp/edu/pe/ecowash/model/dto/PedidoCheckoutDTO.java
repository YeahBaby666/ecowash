package utp.edu.pe.ecowash.model.dto;

import java.util.List;

/**
 * DTO (Data Transfer Object) inmutable para recibir el JSON del frontend 
 * al momento de realizar el pago/checkout.
 */
public record PedidoCheckoutDTO(
    String nombre,
    String telefono,
    String direccion,
    String modo,
    String fecha,
    String horario,
    String instrucciones,
    String servicio,
    Double total,
    List<ItemDTO> items
) {
    // Sub-Record para la lista de prendas (carrito)
    public record ItemDTO(
        String nombre,
        Integer cantidad,
        Double precio
    ) {}
}