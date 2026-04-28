package utp.edu.pe.ecowash.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import utp.edu.pe.ecowash.model.dto.PedidoCheckoutDTO;
import utp.edu.pe.ecowash.model.entity.Cliente;
import utp.edu.pe.ecowash.model.entity.Pedido;
import utp.edu.pe.ecowash.model.entity.PedidoItem;
import utp.edu.pe.ecowash.model.enums.EstadoPedido;
import utp.edu.pe.ecowash.model.repository.ClienteRepository;
import utp.edu.pe.ecowash.model.repository.PedidoRepository;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;

    public PedidoController(PedidoRepository pedidoRepository, ClienteRepository clienteRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
    }

    // ─── 1. VISTAS HTML (Thymeleaf) ───

    @GetMapping("/pedidos")
    public String verMisPedidos(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        
        List<Pedido> todosMisPedidos = pedidoRepository.findByClienteCorreoOrderByFechaServicioDesc(principal.getName());
        
        // BLINDAJE: Agregamos p.getEstado() != null para evitar el NullPointerException
        List<Pedido> enProceso = todosMisPedidos.stream()
                .filter(p -> p.getEstado() != null && 
                             p.getEstado() != EstadoPedido.ENTREGADO && 
                             p.getEstado() != EstadoPedido.CANCELADO)
                .toList();
                
        // BLINDAJE: Agregamos p.getEstado() != null
        List<Pedido> historial = todosMisPedidos.stream()
                .filter(p -> p.getEstado() != null && 
                            (p.getEstado() == EstadoPedido.ENTREGADO || 
                             p.getEstado() == EstadoPedido.CANCELADO))
                .toList();

        model.addAttribute("pedidosProceso", enProceso);
        model.addAttribute("pedidosHistorial", historial);
        
        return "pedidos"; 
    }

    @GetMapping("/rastreo")
    public String rastrearPedido(@RequestParam("codigo") String codigo, Model model) {
        Pedido pedido = pedidoRepository.findByCodigo(codigo.trim()).orElse(null);
        model.addAttribute("pedidoEncontrado", pedido);
        return "rastreo"; // Debes crear un rastreo.html que muestre el ticket
    }

    // ─── 2. API REST (Guardado desde JS) ───

    @PostMapping("/api/pedidos/crear")
    @ResponseBody
    @Transactional
    public ResponseEntity<?> crearPedido(@RequestBody PedidoCheckoutDTO dto, Principal principal) {
        try {
            if (principal == null)
                return ResponseEntity.status(401).body("Debes iniciar sesión");

            Cliente cliente = clienteRepository.findByCorreo(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado en BD"));

            Pedido nuevoPedido = new Pedido();
            nuevoPedido.setCodigo("ECO-" + java.util.UUID.randomUUID().toString().substring(0, 6).toUpperCase());
            nuevoPedido.setCliente(cliente);
            nuevoPedido.setNombreReceptor(dto.nombre());
            nuevoPedido.setTelefonoContacto(dto.telefono());
            nuevoPedido.setDireccionServicio(dto.direccion());
            nuevoPedido.setTipoEntrega(dto.modo());

            // Si la fecha viene nula o mal formateada, esto evitará que explote en silencio
            if (dto.fecha() != null && !dto.fecha().isBlank()) {
                nuevoPedido.setFechaServicio(java.time.LocalDate.parse(dto.fecha()));
            }

            nuevoPedido.setHorario(dto.horario());
            nuevoPedido.setInstrucciones(dto.instrucciones());
            nuevoPedido.setServicioElegido(dto.servicio());
            nuevoPedido.setTotal(dto.total());
            nuevoPedido.setEstado(EstadoPedido.PENDIENTE);

            if (dto.items() != null) {
                for (PedidoCheckoutDTO.ItemDTO itemDto : dto.items()) {
                    PedidoItem item = new PedidoItem(itemDto.nombre(), itemDto.cantidad(), itemDto.precio());
                    nuevoPedido.agregarItem(item);
                }
            }

            // Aquí suele ocurrir el 90% de los errores 500 (Base de datos)
            pedidoRepository.save(nuevoPedido);

            return ResponseEntity.ok("{\"codigoGenerado\": \"" + nuevoPedido.getCodigo() + "\"}");

        } catch (Exception e) {
            // 1. Imprime la traza completa en la consola de tu IDE
            e.printStackTrace();
            // 2. Le envía el mensaje exacto al Javascript
            return ResponseEntity.status(500).body("Error en BD o Lógica: " + e.getMessage());
        }
    }

    // --- API DE CANCELACIÓN CON REEMBOLSO ---
    @PostMapping("/api/pedidos/cancelar/{codigo}")
    @ResponseBody
    @Transactional
    public ResponseEntity<?> cancelarPedido(@PathVariable String codigo, Principal principal) {
        if (principal == null) return ResponseEntity.status(401).body("Sesión expirada");

        Pedido pedido = pedidoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        // Reglas de Reembolso
        double porcentajeReembolso = 0.0;
        switch (pedido.getEstado()) {
            case PENDIENTE -> porcentajeReembolso = 1.0;     // 100%
            case RECOGIDO -> porcentajeReembolso = 0.7;      // 70% (descuento logística)
            case EN_LAVADORA -> porcentajeReembolso = 0.4;   // 40% (ya se usaron insumos)
            default -> porcentajeReembolso = 0.0;            // 0% si ya está casi listo
        }

        double montoDevolucion = pedido.getTotal() * porcentajeReembolso;
        
        pedido.setEstado(EstadoPedido.CANCELADO);
        pedido.setInstrucciones(pedido.getInstrucciones() + 
            " [CANCELADO: Devolución de S/ " + String.format("%.2f", montoDevolucion) + "]");
        
        pedidoRepository.save(pedido);

        return ResponseEntity.ok("{\"reembolso\": " + montoDevolucion + "}");
    }

    // --- API DE RASTREO PARA EL INDEX (Devuelve JSON) ---
    @GetMapping("/api/pedidos/rastrear")
    @ResponseBody
    public ResponseEntity<?> obtenerDatosRastreo(@RequestParam String codigo) {
        return pedidoRepository.findByCodigo(codigo.trim())
                .map(p -> {
                    // Creamos un mapa simple para no enviar toda la entidad pesada
                    return ResponseEntity.ok(java.util.Map.of(
                        "codigo", p.getCodigo(),
                        "servicio", p.getServicioElegido(),
                        "estado", p.getEstado().toString(),
                        "total", p.getTotal(),
                        "nombre", p.getNombreReceptor(),
                        "direccion", p.getDireccionServicio(),
                        "fecha", p.getFechaServicio().toString()
                    ));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}