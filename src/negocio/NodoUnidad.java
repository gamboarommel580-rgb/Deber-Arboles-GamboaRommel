package negocio;

import enums.TipoUnidad;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import modelo.UnidadBiblioteca;

/** Nodo del árbol general: una unidad, sus hijos y, si es ESTANTE, su catálogo. */
public class NodoUnidad {

    private final UnidadBiblioteca dato;
    private final List<NodoUnidad> hijos;
    private final CatalogoABB catalogo;

    public NodoUnidad(UnidadBiblioteca dato) {
        if (dato == null) {
            throw new IllegalArgumentException("El nodo necesita una unidad");
        }
        this.dato = dato;
        this.hijos = new ArrayList<>();
        this.catalogo = dato.getTipo() == TipoUnidad.ESTANTE ? new CatalogoABB() : null;
    }

    public UnidadBiblioteca getDato() {
        return dato;
    }

    /** Agrega el hijo solo si es del tipo inmediatamente inferior. */
    public boolean agregarHijo(NodoUnidad hijo) {
        if (hijo == null || !dato.getTipo().puedeTenerHijo(hijo.getDato().getTipo())) {
            return false;
        }
        return hijos.add(hijo);
    }

    public List<NodoUnidad> getHijos() {
        return Collections.unmodifiableList(hijos);
    }

    public boolean esHoja() {
        return hijos.isEmpty();
    }

    public CatalogoABB getCatalogo() {
        return catalogo;
    }
}
