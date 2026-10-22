package app;

import enums.TipoRecorrido;
import enums.TipoUnidad;
import java.util.Scanner;
import modelo.Libro;
import modelo.UnidadBiblioteca;
import negocio.ArbolBiblioteca;
import negocio.CatalogoABB;
import negocio.NodoUnidad;

/** Menú interactivo del catálogo de la Biblioteca FISEI. */
public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static ArbolBiblioteca arbol = null;

    public static void main(String[] args) {
        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Opción: ");
            try {
                procesar(opcion);
            } catch (IllegalArgumentException e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        } while (opcion != 0);
        System.out.println("Hasta luego.");
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("=== Biblioteca FISEI · Árbol general y ABB ===");
        System.out.println(" 1. Cargar estructura inicial");
        System.out.println(" 2. Mostrar jerarquía");
        System.out.println(" 3. Agregar unidad");
        System.out.println(" 4. Buscar unidad");
        System.out.println(" 5. Altura, hojas, nivel y total de libros");
        System.out.println(" 6. Insertar libro en un estante");
        System.out.println(" 7. Buscar libro");
        System.out.println(" 8. Modificar libro");
        System.out.println(" 9. Eliminar libro");
        System.out.println("10. Recorridos con traza");
        System.out.println("11. Retos (rango, camino, ABB válido y desdePreorden)");
        System.out.println(" 0. Salir");
    }

    private static void procesar(int opcion) {
        if (opcion != 0 && opcion != 1 && arbol == null) {
            System.out.println("Primero cargue la estructura inicial (opción 1).");
            return;
        }
        switch (opcion) {
            case 1 -> {
                arbol = DatosPrueba.crear();
                System.out.println("Estructura inicial cargada.");
            }
            case 2 -> System.out.print(arbol.mostrarJerarquia());
            case 3 -> {
                String padre = leerTexto("Código del padre: ");
                String codigo = leerTexto("Código nuevo: ");
                String nombre = leerTexto("Nombre: ");
                int t = leerEntero("Tipo (1=AREA, 2=ESTANTE): ");
                if (t < 1 || t > 2) {
                    System.out.println("Tipo no válido.");
                    return;
                }
                boolean ok = arbol.agregar(padre, new UnidadBiblioteca(codigo, nombre, TipoUnidad.values()[t]));
                System.out.println(ok ? "Unidad agregada." : "No se pudo agregar (padre, jerarquía o código repetido).");
            }
            case 4 -> {
                NodoUnidad n = arbol.buscar(leerTexto("Código: "));
                System.out.println(n == null ? "No existe." : n.getDato());
            }
            case 5 -> {
                System.out.println("Altura: " + arbol.altura() + " | Hojas: " + arbol.contarHojas()
                        + " | Total de libros: " + arbol.contarLibrosTotal());
                String c = leerTexto("Código para consultar el nivel: ");
                System.out.println("Nivel de " + c + ": " + arbol.nivel(c));
            }
            case 6 -> {
                CatalogoABB cat = estante();
                if (cat == null) return;
                Libro l = new Libro(leerEntero("Código: "), leerTexto("Título: "),
                        leerTexto("Autor: "), leerEntero("Año: "));
                System.out.println(cat.insertar(l) ? "Libro insertado." : "Código repetido.");
            }
            case 7 -> {
                CatalogoABB cat = estante();
                if (cat == null) return;
                Libro l = cat.buscar(leerEntero("Código: "));
                System.out.println(l == null ? "No existe." : l);
            }
            case 8 -> {
                CatalogoABB cat = estante();
                if (cat == null) return;
                boolean ok = cat.modificar(leerEntero("Código: "), leerTexto("Nuevo título: "),
                        leerTexto("Nuevo autor: "), leerEntero("Nuevo año: "));
                System.out.println(ok ? "Libro modificado." : "No existe.");
            }
            case 9 -> {
                CatalogoABB cat = estante();
                if (cat == null) return;
                System.out.println(cat.eliminar(leerEntero("Código: ")) ? "Libro eliminado." : "No existe.");
            }
            case 10 -> {
                CatalogoABB cat = estante();
                if (cat == null) return;
                int t = leerEntero("Recorrido (1=PREORDEN, 2=INORDEN, 3=POSORDEN): ");
                if (t < 1 || t > 3) {
                    System.out.println("Tipo no válido.");
                    return;
                }
                System.out.println(cat.trazaRecorrido(TipoRecorrido.values()[t - 1]));
            }
            case 11 -> {
                CatalogoABB cat = estante();
                if (cat == null) return;
                int a = leerEntero("Rango desde: ");
                int b = leerEntero("Rango hasta: ");
                System.out.println("contarEnRango(" + a + ", " + b + "): " + cat.contarEnRango(a, b));
                int c = leerEntero("Código para caminoBusqueda: ");
                System.out.println("caminoBusqueda(" + c + "): " + cat.caminoBusqueda(c));
                System.out.println("esABBValido(): " + cat.esABBValido());
                // Extra: reconstruir el ABB desde su preorden y comprobar que queda igual
                java.util.List<Libro> pre = cat.recorrer(TipoRecorrido.PREORDEN);
                int[] codigos = new int[pre.size()];
                for (int i = 0; i < codigos.length; i++) {
                    codigos[i] = pre.get(i).getCodigo();
                }
                CatalogoABB copia = CatalogoABB.desdePreorden(codigos);
                System.out.println("desdePreorden (extra) -> " + copia.trazaRecorrido(TipoRecorrido.INORDEN)
                        .lines().reduce((x, y) -> y).orElse(""));
            }
            case 0 -> { }
            default -> System.out.println("Opción no válida.");
        }
    }

    private static CatalogoABB estante() {
        NodoUnidad n = arbol.buscar(leerTexto("Código del estante (ej. SW-E1): "));
        if (n == null || n.getCatalogo() == null) {
            System.out.println("Ese código no es un estante.");
            return null;
        }
        return n.getCatalogo();
    }

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return sc.hasNextLine() ? sc.nextLine().trim() : "";
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            String t = leerTexto(mensaje);
            if (t.isEmpty() && !sc.hasNextLine()) {
                return 0;                  // fin de la entrada: salir
            }
            try {
                return Integer.parseInt(t);
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número entero.");
            }
        }
    }
}
