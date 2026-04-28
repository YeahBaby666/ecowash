package utp.edu.pe.ecowash.model.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(unique = true, nullable = false)
    private String correo; 

    @Column(nullable = false)
    private String password;

    // 1. Cambiamos a Set y forzamos FetchType.EAGER
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "cliente_direcciones", 
        joinColumns = @JoinColumn(name = "cliente_id")
    )
    @Column(name = "direccion")
    private Set<String> direcciones = new HashSet<>();

    // 2. Cambiamos a Set y forzamos FetchType.EAGER
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "cliente_telefonos", 
        joinColumns = @JoinColumn(name = "cliente_id")
    )
    @Column(name = "telefono")
    private Set<String> telefonos = new HashSet<>();

    // Constructores
    public Cliente() {}

    public Cliente(String nombre, String correo, String password) {
        this.nombre = nombre;
        this.correo = correo;
        this.password = password;
    }

    // Getters y Setters base...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // Actualizados para usar Set en lugar de List
    public Set<String> getDirecciones() { return direcciones; }
    public void setDirecciones(Set<String> direcciones) { this.direcciones = direcciones; }
    public Set<String> getTelefonos() { return telefonos; }
    public void setTelefonos(Set<String> telefonos) { this.telefonos = telefonos; }
}