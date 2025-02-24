package com.iluncrypt.iluncryptapp.models.keys;

import com.iluncrypt.iluncryptapp.models.Alphabet;

/**
 * Representa una clave para el cifrado por permutación.
 * La clave se define mediante una cadena numérica, por ejemplo "231".
 */
public class PermutationKey extends Key {
    private final int[] permutation;

    /**
     * Construye una clave de permutación a partir de una cadena y un alfabeto.
     *
     * @param permutationStr La cadena que representa la permutación (por ejemplo, "231").
     * @param keyAlphabet    El alfabeto asociado a la clave.
     * @throws IllegalArgumentException Si la cadena contiene caracteres no numéricos o no forma una permutación válida.
     */
    public PermutationKey(String permutationStr, Alphabet keyAlphabet) {
        super(keyAlphabet);
        permutation = new int[permutationStr.length()];
        for (int i = 0; i < permutationStr.length(); i++) {
            char c = permutationStr.charAt(i);
            if (!Character.isDigit(c)) {
                throw new IllegalArgumentException("La clave de permutación debe contener sólo dígitos.");
            }
            int num = Character.getNumericValue(c);
            permutation[i] = num;
        }
        validate();
    }

    /**
     * Valida que la clave sea una permutación válida:
     * - Los dígitos deben estar en el rango 1..n, siendo n el tamaño de la clave.
     * - No se permiten dígitos repetidos.
     */
    @Override
    public void validate() {
        int n = permutation.length;
        boolean[] seen = new boolean[n + 1]; // Usamos índice 1-based
        for (int num : permutation) {
            if (num < 1 || num > n) {
                throw new IllegalArgumentException("El dígito " + num + " no es válido para una permutación de tamaño " + n + ".");
            }
            if (seen[num]) {
                throw new IllegalArgumentException("Los dígitos de la permutación deben ser únicos.");
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
}
