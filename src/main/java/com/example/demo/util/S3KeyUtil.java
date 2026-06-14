package com.example.demo.util;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Construye nombres legibles y seguros para las claves (keys) de S3.
 *
 * <p>Antes los objetos se guardaban con un UUID aleatorio (p.ej.
 * {@code tramites/{tramiteId}/3f9a...-v1.pdf}), por lo que al navegar el bucket
 * en la consola de AWS no se podía reconocer el archivo. Con estos helpers la
 * key incluye el nombre real del documento más un id corto estable, p.ej.
 * {@code tramites/{tramiteId}/cedula-de-identidad-27936cd6-v1.pdf}.</p>
 */
public final class S3KeyUtil {

    private S3KeyUtil() {}

    private static final int MAX_SLUG = 60;

    /** Convierte un texto libre en un segmento seguro para S3: minúsculas, sin acentos, con guiones. */
    public static String slug(String texto) {
        if (texto == null || texto.isBlank()) return "documento";
        String norm = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");          // elimina acentos / diacríticos
        norm = norm.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")       // todo lo no alfanumérico → guion
                .replaceAll("-{2,}", "-")            // colapsa guiones repetidos
                .replaceAll("(^-+|-+$)", "");        // recorta guiones de los extremos
        if (norm.isBlank()) return "documento";
        if (norm.length() > MAX_SLUG) {
            norm = norm.substring(0, MAX_SLUG).replaceAll("-+$", "");
        }
        return norm;
    }

    /**
     * Devuelve los últimos 8 caracteres alfanuméricos de un id, para desambiguar
     * archivos con el mismo nombre sin alargar la key. Es estable para un mismo
     * documento, de modo que todas sus versiones quedan agrupadas en el bucket.
     */
    public static String shortId(String id) {
        if (id == null) return "id";
        String limpio = id.replaceAll("[^a-zA-Z0-9]", "");
        if (limpio.isBlank()) return "id";
        return limpio.length() <= 8 ? limpio : limpio.substring(limpio.length() - 8);
    }

    /** Quita la extensión de un nombre de archivo (para usar el nombre como base del slug). */
    public static String sinExtension(String filename) {
        if (filename == null) return null;
        int i = filename.lastIndexOf('.');
        return i > 0 ? filename.substring(0, i) : filename;
    }

    /** Primer valor no nulo ni en blanco (para elegir el mejor nombre disponible). */
    public static String primerNoVacio(String... valores) {
        if (valores != null) {
            for (String v : valores) {
                if (v != null && !v.isBlank()) return v;
            }
        }
        return null;
    }
}
