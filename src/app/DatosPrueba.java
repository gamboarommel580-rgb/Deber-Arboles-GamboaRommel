package app;

import enums.TipoUnidad;
import modelo.Libro;
import modelo.UnidadBiblioteca;
import negocio.ArbolBiblioteca;
import negocio.CatalogoABB;

/** Estructura inicial del deber: 7 unidades, 4 hojas, altura 2 y 13 libros. */
public final class DatosPrueba {

    private DatosPrueba() {
    }

    public static ArbolBiblioteca crear() {
        ArbolBiblioteca arbol = new ArbolBiblioteca(
                new UnidadBiblioteca("BIB", "Biblioteca FISEI", TipoUnidad.BIBLIOTECA));
        arbol.agregar("BIB", new UnidadBiblioteca("SW", "Software", TipoUnidad.AREA));
        arbol.agregar("SW", new UnidadBiblioteca("SW-E1", "Estante 1 de Software", TipoUnidad.ESTANTE));
        arbol.agregar("SW", new UnidadBiblioteca("SW-E2", "Estante 2 de Software", TipoUnidad.ESTANTE));
        arbol.agregar("BIB", new UnidadBiblioteca("TI", "Tecnologías de la Información", TipoUnidad.AREA));
        arbol.agregar("TI", new UnidadBiblioteca("TI-E1", "Estante 1 de TI", TipoUnidad.ESTANTE));
        arbol.agregar("BIB", new UnidadBiblioteca("TEL", "Telecomunicaciones", TipoUnidad.AREA));

        cargar(arbol.buscar("SW-E1").getCatalogo(), 60, 35, 80, 20, 45, 70, 90, 40, 75);
        cargar(arbol.buscar("SW-E2").getCatalogo(), 15, 10, 25);
        cargar(arbol.buscar("TI-E1").getCatalogo(), 50);
        return arbol;
    }

    private static void cargar(CatalogoABB catalogo, int... codigos) {
        for (int c : codigos) {
            catalogo.insertar(new Libro(c, "Libro " + c, "Autor " + c, 2000 + c % 26));
        }
    }
}
