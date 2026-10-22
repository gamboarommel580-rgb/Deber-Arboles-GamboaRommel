package app;

import enums.TipoRecorrido;
import enums.TipoUnidad;
import java.util.List;
import modelo.Libro;
import modelo.UnidadBiblioteca;
import negocio.ArbolBiblioteca;
import negocio.CatalogoABB;
import negocio.NodoLibro;

/** Casos de prueba obligatorios del deber. Muestra X/N pruebas OK. */
public class PruebasBiblioteca {

    private static int ok = 0;
    private static int total = 0;

    public static void main(String[] args) {
        System.out.println("=== PruebasBiblioteca ===");

        // 1. Biblioteca solo con la raíz
        ArbolBiblioteca soloRaiz = new ArbolBiblioteca(
                new UnidadBiblioteca("BIB", "Biblioteca FISEI", TipoUnidad.BIBLIOTECA));
        verificar("Solo raíz: altura 0 y 1 hoja",
                soloRaiz.altura() == 0 && soloRaiz.contarHojas() == 1);

        // 2. Biblioteca inicial
        ArbolBiblioteca bib = DatosPrueba.crear();
        verificar("Inicial: altura 2, 4 hojas y 13 libros",
                bib.altura() == 2 && bib.contarHojas() == 4 && bib.contarLibrosTotal() == 13);

        // 3. Niveles
        verificar("Nivel BIB=0, sw=1, SW-E2=2, XYZ=-1",
                bib.nivel("BIB") == 0 && bib.nivel("sw") == 1
                && bib.nivel("SW-E2") == 2 && bib.nivel("XYZ") == -1);

        // 4. Jerarquía inválida
        verificar("ESTANTE como hijo de BIBLIOTECA: false",
                !bib.agregar("BIB", new UnidadBiblioteca("X-E1", "Estante suelto", TipoUnidad.ESTANTE)));

        // 5. Recorridos de SW-E1
        CatalogoABB e1 = bib.buscar("SW-E1").getCatalogo();
        verificar("Preorden SW-E1", codigos(e1, TipoRecorrido.PREORDEN).equals("60 35 20 45 40 80 70 75 90"));
        verificar("Inorden SW-E1", codigos(e1, TipoRecorrido.INORDEN).equals("20 35 40 45 60 70 75 80 90"));
        verificar("Posorden SW-E1", codigos(e1, TipoRecorrido.POSORDEN).equals("20 40 45 35 75 70 90 80 60"));

        // 6. Código repetido
        verificar("Insertar código repetido (60): false",
                !e1.insertar(new Libro(60, "Repetido", "Autor", 2020)));

        // 7. Retos
        verificar("caminoBusqueda(42) = [60, 35, 45, 40]",
                e1.caminoBusqueda(42).equals(List.of(60, 35, 45, 40)));
        verificar("contarEnRango(40, 75) = 5", e1.contarEnRango(40, 75) == 5);

        // 8. Insertar 42 y eliminar 20 (hoja), 70 (un hijo) y 60 (dos hijos)
        e1.insertar(new Libro(42, "Libro 42", "Autor 42", 2024));
        e1.eliminar(20);
        e1.eliminar(70);
        e1.eliminar(60);
        verificar("Preorden final 75 35 45 40 42 80 90",
                codigos(e1, TipoRecorrido.PREORDEN).equals("75 35 45 40 42 80 90"));

        // 9. Código inexistente
        verificar("Buscar/modificar/eliminar 999: null / false / false",
                e1.buscar(999) == null && !e1.modificar(999, "T", "A", 2020) && !e1.eliminar(999));

        // 10. Libro inválido
        verificar("Libro con datos inválidos: IllegalArgumentException", lanzaExcepcion());

        // 11. esABBValido con un nieto mal ubicado
        CatalogoABB malo = new CatalogoABB();
        NodoLibro r = nodo(50);
        r.setIzquierdo(nodo(30));
        r.getIzquierdo().setDerecho(nodo(60)); // 60 > 50 pero está a la izquierda de 50
        malo.setRaiz(r);
        verificar("esABBValido con nieto mal ubicado: false", !malo.esABBValido());
        verificar("esABBValido del catálogo correcto: true", e1.esABBValido());

        // 12. Extra: desdePreorden
        CatalogoABB rec = CatalogoABB.desdePreorden(new int[]{60, 35, 20, 45, 40, 80, 70, 75, 90});
        verificar("Extra desdePreorden: mismo inorden y posorden",
                codigos(rec, TipoRecorrido.INORDEN).equals("20 35 40 45 60 70 75 80 90")
                && codigos(rec, TipoRecorrido.POSORDEN).equals("20 40 45 35 75 70 90 80 60"));

        System.out.println(ok + "/" + total + " pruebas OK");
    }

    private static void verificar(String nombre, boolean condicion) {
        total++;
        if (condicion) {
            ok++;
        }
        System.out.println((condicion ? "[OK]    " : "[FALLA] ") + nombre);
    }

    private static String codigos(CatalogoABB catalogo, TipoRecorrido tipo) {
        StringBuilder sb = new StringBuilder();
        for (Libro l : catalogo.recorrer(tipo)) {
            sb.append(l.getCodigo()).append(' ');
        }
        return sb.toString().trim();
    }

    private static boolean lanzaExcepcion() {
        try {
            new Libro(-5, "", "Autor", 1800);
            return false;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    private static NodoLibro nodo(int codigo) {
        return new NodoLibro(new Libro(codigo, "Libro " + codigo, "Autor", 2020));
    }
}
