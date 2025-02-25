package com.iluncrypt.iluncryptapp.models.keys;

import com.iluncrypt.iluncryptapp.models.Alphabet;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Representa una clave para el cifrado por permutación.
 * La clave debe estar en formato "(1, 2, 3, ...)".
 */
public class PermutationKey extends Key {
    private final int[] permutation;

    /**
     * Construye una clave de permutación a partir de una cadena en formato "(1, 2, 3, ...)" y un alfabeto.
     *
     * @param permutationStr La cadena que representa la permutación en formato "(1, 2, 3, ...)".
     * @param keyAlphabet    El alfabeto asociado a la clave.
     * @throws IllegalArgumentException Si la cadena tiene un formato incorrecto o no forma una permutación válida.
     */
    public PermutationKey(String permutationStr, Alphabet keyAlphabet) {
        super(keyAlphabet);
        this.permutation = parsePermutation(permutationStr);
        validate();
    }

    /**
     * Parsea la clave de permutación desde el formato "(1, 2, 3, ...)" a un arreglo de enteros.
     *
     * @param permutationStr La cadena de entrada en formato "(1, 2, 3, ...)".
     * @return Arreglo de enteros representando la permutación.
     * @throws IllegalArgumentException Si el formato no es válido.
     */
    private int[] parsePermutation(String permutationStr) {
        // Verificar el formato con una expresión regular
        Pattern pattern = Pattern.compile("^\\((\\d+(,\\s*\\d+)*)\\)$");
        Matcher matcher = pattern.matcher(permutationStr.trim());

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Formato de clave incorrecto. Debe ser '(1, 2, 3, ...)'");
        }

        // Eliminar paréntesis y dividir los números por coma
        String cleanStr = permutationStr.substring(1, permutationStr.length() - 1);
        String[] parts = cleanStr.split(",\\s*");

        int[] perm = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            try {
                perm[i] = Integer.parseInt(parts[i]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("La clave de permutación contiene caracteres no numéricos.");
            }
        }
        return perm;
    }

    /**
     * Valida que la clave sea una permutación válida:
     * - Los valores deben estar en el rango 1..n, donde n es el tamaño de la clave.
     * - No debe haber números repetidos.
     */
    @Override
    public void validate() {
        int n = permutation.length;
        boolean[] seen = new boolean[n + 1]; // Índice 1-based para facilitar validación

        for (int num : permutation) {
            if (num < 1 || num > n) {
                throw new IllegalArgumentException("El número " + num + " está fuera del rango permitido (1-" + n + ").");
            }
            if (seen[num]) {
                throw new IllegalArgumentException("La clave de permutación no puede tener números repetidos.");
            }
            seen[num] = true;
        }
    }

    /**
     * Retorna el arreglo de enteros que representa la permutación.
     *
     * @return Arreglo de la permutación.
     */
    public int[] getPermutation() {
        return permutation;
    }

    /**
     * Retorna la clave en el formato "(1, 2, 3, ...)".
     *
     * @return La clave de permutación formateada.
     */
    @Override
    public String toString() {
        return "(" + Arrays.stream(permutation)
                .mapToObj(String::valueOf)
                .reduce((a, b) -> a + ", " + b)
                .orElse("") + ")";
    }
}
