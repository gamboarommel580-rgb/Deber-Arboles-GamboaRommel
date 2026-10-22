package enums;

/** Tipos de unidad de la biblioteca, en orden jerárquico. */
public enum TipoUnidad {
    BIBLIOTECA,
    AREA,
    ESTANTE;

    /** Un hijo solo puede ser del tipo inmediatamente inferior. */
    public boolean puedeTenerHijo(TipoUnidad hijo) {
        return hijo != null && hijo.ordinal() == this.ordinal() + 1;
    }
}
