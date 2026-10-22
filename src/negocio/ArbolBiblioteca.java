package negocio;

import enums.TipoUnidad;
import java.util.List;
import modelo.UnidadBiblioteca;

/**
 * Árbol general n-ario: BIBLIOTECA -> AREA -> ESTANTE.
 * Cada método público valida y delega en métodos privados recursivos.
 * Los hijos de un nodo también se recorren con recursión (por índice), sin bucles.
 */
public class ArbolBiblioteca {

    private final NodoUnidad raiz;

    public ArbolBiblioteca(UnidadBiblioteca biblioteca) {
        if (biblioteca == null || biblioteca.getTipo() != TipoUnidad.BIBLIOTECA) {
            throw new IllegalArgumentException("La raíz debe ser de tipo BIBLIOTECA");
        }
        this.raiz = new NodoUnidad(biblioteca);
    }

    public NodoUnidad getRaiz() {
        return raiz;
    }

    // ---------- agregar ----------

    /** Agrega una unidad bajo su padre: código único y jerarquía BIBLIOTECA -> AREA -> ESTANTE. */
    public boolean agregar(String codigoPadre, UnidadBiblioteca nueva) {
        if (codigoPadre == null || nueva == null) {
            return false;
        }
        if (buscar(nueva.getCodigo()) != null) {
            return false;                                  // código repetido
        }
        NodoUnidad padre = buscar(codigoPadre);
        if (padre == null) {
            return false;                                  // el padre no existe
        }
        return padre.agregarHijo(new NodoUnidad(nueva));   // valida la jerarquía
    }

    // ---------- buscar ----------

    /** Busca una unidad sin distinguir mayúsculas. null si no existe. O(N) */
    public NodoUnidad buscar(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return null;
        }
        return buscar(raiz, codigo.trim().toUpperCase());
    }

    private NodoUnidad buscar(NodoUnidad nodo, String codigo) {
        if (nodo.getDato().getCodigo().equals(codigo)) {
            return nodo;                                   // caso base: encontrado
        }
        return buscarEnHijos(nodo.getHijos(), 0, codigo);  // caso recursivo
    }

    private NodoUnidad buscarEnHijos(List<NodoUnidad> hijos, int i, String codigo) {
        if (i == hijos.size()) {
            return null;                                   // caso base: no quedan hijos
        }
        NodoUnidad encontrado = buscar(hijos.get(i), codigo);
        return encontrado != null ? encontrado : buscarEnHijos(hijos, i + 1, codigo);
    }

    // ---------- altura ----------

    /** Altura en aristas: una hoja vale 0. */
    public int altura() {
        return altura(raiz);
    }

    private int altura(NodoUnidad nodo) {
        return 1 + alturaHijos(nodo.getHijos(), 0);        // hoja: 1 + (-1) = 0
    }

    private int alturaHijos(List<NodoUnidad> hijos, int i) {
        if (i == hijos.size()) {
            return -1;                                     // caso base: sin más hijos
        }
        return Math.max(altura(hijos.get(i)), alturaHijos(hijos, i + 1));
    }

    // ---------- contarHojas ----------

    /** Número de unidades sin hijos. */
    public int contarHojas() {
        return contarHojas(raiz);
    }

    private int contarHojas(NodoUnidad nodo) {
        if (nodo.esHoja()) {
            return 1;                                      // caso base
        }
        return contarHojasHijos(nodo.getHijos(), 0);
    }

    private int contarHojasHijos(List<NodoUnidad> hijos, int i) {
        if (i == hijos.size()) {
            return 0;
        }
        return contarHojas(hijos.get(i)) + contarHojasHijos(hijos, i + 1);
    }

    // ---------- nivel ----------

    /** Nivel de la unidad (raíz = 0). -1 si no existe. */
    public int nivel(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return -1;
        }
        return nivel(raiz, codigo.trim().toUpperCase(), 0);
    }

    private int nivel(NodoUnidad nodo, String codigo, int profundidad) {
        if (nodo.getDato().getCodigo().equals(codigo)) {
            return profundidad;                            // caso base: encontrado
        }
        return nivelHijos(nodo.getHijos(), 0, codigo, profundidad + 1);
    }

    private int nivelHijos(List<NodoUnidad> hijos, int i, String codigo, int profundidad) {
        if (i == hijos.size()) {
            return -1;                                     // caso base: no está
        }
        int n = nivel(hijos.get(i), codigo, profundidad);
        return n != -1 ? n : nivelHijos(hijos, i + 1, codigo, profundidad);
    }

    // ---------- mostrarJerarquia ----------

    /** Jerarquía en preorden, con 2 espacios por nivel. */
    public String mostrarJerarquia() {
        StringBuilder sb = new StringBuilder();
        mostrarJerarquia(raiz, 0, sb);
        return sb.toString();
    }

    private void mostrarJerarquia(NodoUnidad nodo, int nivel, StringBuilder sb) {
        sb.append("  ".repeat(nivel))
          .append(nodo.getDato().getCodigo()).append(" - ").append(nodo.getDato().getNombre())
          .append('\n');                                   // primero el nodo (preorden)
        mostrarHijos(nodo.getHijos(), 0, nivel + 1, sb);   // luego cada hijo
    }

    private void mostrarHijos(List<NodoUnidad> hijos, int i, int nivel, StringBuilder sb) {
        if (i == hijos.size()) {
            return;                                        // caso base
        }
        mostrarJerarquia(hijos.get(i), nivel, sb);
        mostrarHijos(hijos, i + 1, nivel, sb);
    }

    // ---------- contarLibrosTotal ----------

    /** Total de libros de todos los estantes. */
    public int contarLibrosTotal() {
        return contarLibros(raiz);
    }

    private int contarLibros(NodoUnidad nodo) {
        int propios = nodo.getCatalogo() == null ? 0 : nodo.getCatalogo().contar();
        return propios + contarLibrosHijos(nodo.getHijos(), 0);
    }

    private int contarLibrosHijos(List<NodoUnidad> hijos, int i) {
        if (i == hijos.size()) {
            return 0;                                      // caso base
        }
        return contarLibros(hijos.get(i)) + contarLibrosHijos(hijos, i + 1);
    }
}
