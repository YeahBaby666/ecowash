package utp.edu.pe.ecowash.model.enums;

public enum EstadoPedido {
    PENDIENTE,     // Recién creado en el checkout
    RECOGIDO,      // El repartidor ya lo tiene
    EN_LAVADORA,   // Proceso interno
    PLANCHANDO,    // Proceso interno
    TERMINADO,     // Listo para enviar
    ENTREGADO,      // Finalizado e histórico
    CANCELADO       // Si el cliente cancela antes de la recogida
}