package org.stringtecnologia.string_api.util;

public interface DominioEnum {
    CategoriaDominio getCategoria();
    /*
    default String getCodigo() {
        return ((Enum<?>) this).name();
    }*/

    default String getCodigo() {
        if (!(this instanceof Enum<?> e)) {
            throw new IllegalStateException("DominioEnum deve ser implementado por enum");
        }
        return e.name();
    }
}