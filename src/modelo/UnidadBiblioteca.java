package modelo;

import enums.TipoUnidad;

/** Dato del árbol general: biblioteca, área o estante. */
public class UnidadBiblioteca {

    private final String codigo;
    private String nombre;
    private final TipoUnidad tipo;

    public UnidadBiblioteca(String codigo, String nombre, TipoUnidad tipo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código es obligatorio");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo es obligatorio");
        }
        this.codigo = codigo.trim().toUpperCase();
        setNombre(nombre);
        this.tipo = tipo;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        this.nombre = nombre.trim();
    }

    public TipoUnidad getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre + " (" + tipo + ")";
    }
}
