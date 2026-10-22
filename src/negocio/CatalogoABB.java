package negocio;

import enums.TipoRecorrido;
import java.util.ArrayList;
import java.util.List;
import modelo.Libro;

/**
 * ABB de libros ordenado por código (sin repetidos).
 * Todas las operaciones sobre el árbol son recursivas.
 */
public class CatalogoABB {

    private NodoLibro raiz;

    public NodoLibro getRaiz() {
        return raiz;
    }

    public void setRaiz(NodoLibro raiz) {
        this.raiz = raiz;
    }

    // ---------- Parte C: inserción, búsqueda y conteo ----------

    /** Inserta por código. false si el código ya existe. O(h) */
    public boolean insertar(Libro libro) {
        if (libro == null) {
            throw new IllegalArgumentException("El libro no puede ser null");
        }
        if (buscar(libro.getCodigo()) != null) {
            return false;                                   // repetido
        }
        raiz = insertar(raiz, libro);
        return true;
    }

    private NodoLibro insertar(NodoLibro nodo, Libro libro) {
        if (nodo == null) {
            return new NodoLibro(libro);                    // caso base: lugar libre
        }
        if (libro.getCodigo() < nodo.getDato().getCodigo()) {
            nodo.setIzquierdo(insertar(nodo.getIzquierdo(), libro));
        } else {
            nodo.setDerecho(insertar(nodo.getDerecho(), libro));
        }
        return nodo;                                        // enlaza al regresar
    }

    /** Busca un libro. null si no existe. O(h) */
    public Libro buscar(int codigo) {
        NodoLibro n = buscarNodo(raiz, codigo);
        return n == null ? null : n.getDato();
    }

    private NodoLibro buscarNodo(NodoLibro nodo, int codigo) {
        if (nodo == null) {
            return null;                                    // caso base: no existe
        }
        int actual = nodo.getDato().getCodigo();
        if (codigo == actual) {
            return nodo;                                    // caso base: encontrado
        }
        return codigo < actual
                ? buscarNodo(nodo.getIzquierdo(), codigo)
                : buscarNodo(nodo.getDerecho(), codigo);
    }

    /** Número de libros del catálogo. O(n) */
    public int contar() {
        return contar(raiz);
    }

    private int contar(NodoLibro nodo) {
        if (nodo == null) {
            return 0;                                       // caso base
        }
        return 1 + contar(nodo.getIzquierdo()) + contar(nodo.getDerecho());
    }

    // ---------- Parte C: recorridos y traza ----------

    /** Recorrido en preorden, inorden o posorden. O(n) */
    public List<Libro> recorrer(TipoRecorrido tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de recorrido es obligatorio");
        }
        List<Libro> lista = new ArrayList<>();
        recorrer(raiz, tipo, lista);
        return lista;
    }

    private void recorrer(NodoLibro nodo, TipoRecorrido tipo, List<Libro> lista) {
        if (nodo == null) {
            return;                                         // caso base
        }
        if (tipo == TipoRecorrido.PREORDEN) lista.add(nodo.getDato());
        recorrer(nodo.getIzquierdo(), tipo, lista);
        if (tipo == TipoRecorrido.INORDEN) lista.add(nodo.getDato());
        recorrer(nodo.getDerecho(), tipo, lista);
        if (tipo == TipoRecorrido.POSORDEN) lista.add(nodo.getDato());
    }

    /** Traza: entra X, VISITA X, null -> regresa; 2 espacios por nivel. */
    public String trazaRecorrido(TipoRecorrido tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de recorrido es obligatorio");
        }
        StringBuilder traza = new StringBuilder();
        StringBuilder secuencia = new StringBuilder();
        traza(raiz, tipo, "", traza, secuencia);
        traza.append("Secuencia ").append(tipo).append(": ").append(secuencia.toString().trim());
        return traza.toString();
    }

    private void traza(NodoLibro nodo, TipoRecorrido tipo, String sangria,
                       StringBuilder traza, StringBuilder secuencia) {
        if (nodo == null) {
            traza.append(sangria).append("null -> regresa\n");  // caso base
            return;
        }
        int c = nodo.getDato().getCodigo();
        traza.append(sangria).append("entra ").append(c).append('\n');
        if (tipo == TipoRecorrido.PREORDEN) visitar(c, sangria, traza, secuencia);
        traza(nodo.getIzquierdo(), tipo, sangria + "  ", traza, secuencia);
        if (tipo == TipoRecorrido.INORDEN) visitar(c, sangria, traza, secuencia);
        traza(nodo.getDerecho(), tipo, sangria + "  ", traza, secuencia);
        if (tipo == TipoRecorrido.POSORDEN) visitar(c, sangria, traza, secuencia);
    }

    private void visitar(int codigo, String sangria, StringBuilder traza, StringBuilder secuencia) {
        traza.append(sangria).append("VISITA ").append(codigo).append('\n');
        secuencia.append(codigo).append(' ');
    }

    // ---------- Parte C: modificar y eliminar ----------

    /** Modifica todo menos el código. false si no existe. */
    public boolean modificar(int codigo, String titulo, String autor, int anio) {
        NodoLibro nodo = buscarNodo(raiz, codigo);
        if (nodo == null) {
            return false;
        }
        new Libro(codigo, titulo, autor, anio);             // valida antes de cambiar
        Libro libro = nodo.getDato();
        libro.setTitulo(titulo);
        libro.setAutor(autor);
        libro.setAnio(anio);
        return true;
    }

    /** Elimina: hoja, un hijo o dos hijos (sucesor inorden). O(h) */
    public boolean eliminar(int codigo) {
        if (buscarNodo(raiz, codigo) == null) {
            return false;
        }
        raiz = eliminar(raiz, codigo);
        return true;
    }

    private NodoLibro eliminar(NodoLibro nodo, int codigo) {
        if (nodo == null) {
            return null;                                    // caso base
        }
        int actual = nodo.getDato().getCodigo();
        if (codigo < actual) {
            nodo.setIzquierdo(eliminar(nodo.getIzquierdo(), codigo));
        } else if (codigo > actual) {
            nodo.setDerecho(eliminar(nodo.getDerecho(), codigo));
        } else {
            if (nodo.getIzquierdo() == null) {
                return nodo.getDerecho();                   // caso 1 (hoja) y caso 2
            }
            if (nodo.getDerecho() == null) {
                return nodo.getIzquierdo();                 // caso 2: un hijo
            }
            Libro sucesor = minimo(nodo.getDerecho());      // caso 3: dos hijos
            nodo.setDato(sucesor);
            nodo.setDerecho(eliminar(nodo.getDerecho(), sucesor.getCodigo()));
        }
        return nodo;
    }

    private Libro minimo(NodoLibro nodo) {
        if (nodo.getIzquierdo() == null) {
            return nodo.getDato();                          // caso base
        }
        return minimo(nodo.getIzquierdo());
    }

    // ---------- Parte D: retos ----------

    /** Cuenta los códigos en [a, b] con poda: no baja por ramas fuera del rango. */
    public int contarEnRango(int a, int b) {
        if (a > b) {
            int t = a; a = b; b = t;
        }
        return contarEnRango(raiz, a, b);
    }

    private int contarEnRango(NodoLibro nodo, int a, int b) {
        if (nodo == null) {
            return 0;                                       // caso base
        }
        int c = nodo.getDato().getCodigo();
        if (c < a) {
            return contarEnRango(nodo.getDerecho(), a, b);  // poda: la izquierda es menor
        }
        if (c > b) {
            return contarEnRango(nodo.getIzquierdo(), a, b); // poda: la derecha es mayor
        }
        return 1 + contarEnRango(nodo.getIzquierdo(), a, b) + contarEnRango(nodo.getDerecho(), a, b);
    }

    /** Códigos visitados desde la raíz hasta el libro (o hasta donde debería estar). */
    public List<Integer> caminoBusqueda(int codigo) {
        List<Integer> camino = new ArrayList<>();
        caminoBusqueda(raiz, codigo, camino);
        return camino;
    }

    private void caminoBusqueda(NodoLibro nodo, int codigo, List<Integer> camino) {
        if (nodo == null) {
            return;                                         // caso base: no está
        }
        int c = nodo.getDato().getCodigo();
        camino.add(c);
        if (codigo == c) {
            return;                                         // caso base: encontrado
        }
        caminoBusqueda(codigo < c ? nodo.getIzquierdo() : nodo.getDerecho(), codigo, camino);
    }

    /** Verifica todo el ABB: cada nodo debe estar entre un mínimo y un máximo. */
    public boolean esABBValido() {
        return esABBValido(raiz, null, null);
    }

    private boolean esABBValido(NodoLibro nodo, Integer minimo, Integer maximo) {
        if (nodo == null) {
            return true;                                    // caso base
        }
        int c = nodo.getDato().getCodigo();
        if ((minimo != null && c <= minimo) || (maximo != null && c >= maximo)) {
            return false;                                   // fuera de sus límites
        }
        return esABBValido(nodo.getIzquierdo(), minimo, c)
            && esABBValido(nodo.getDerecho(), c, maximo);
    }

    /** EXTRA: reconstruye el ABB desde su preorden, sin insertar uno por uno. O(n) */
    public static CatalogoABB desdePreorden(int[] preorden) {
        CatalogoABB catalogo = new CatalogoABB();
        if (preorden != null) {
            int[] posicion = {0};
            catalogo.setRaiz(construir(preorden, posicion, Integer.MIN_VALUE, Integer.MAX_VALUE));
        }
        return catalogo;
    }

    private static NodoLibro construir(int[] pre, int[] pos, int minimo, int maximo) {
        if (pos[0] >= pre.length) {
            return null;                                    // caso base: no quedan códigos
        }
        int c = pre[pos[0]];
        if (c < minimo || c > maximo) {
            return null;                                    // caso base: no pertenece a este subárbol
        }
        pos[0]++;
        NodoLibro nodo = new NodoLibro(new Libro(c, "Libro " + c, "Autor " + c, 2020));
        nodo.setIzquierdo(construir(pre, pos, minimo, c - 1));
        nodo.setDerecho(construir(pre, pos, c + 1, maximo));
        return nodo;
    }
}
