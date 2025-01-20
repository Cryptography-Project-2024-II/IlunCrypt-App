package com.iluncrypt.iluncryptapp.models.keys;

import com.iluncrypt.iluncryptapp.models.Alphabet;

/**
 * Represents a key for the Affine Cipher, ensuring that 'a' is coprime with the alphabet size.
 * The Affine Key consists of two values:
 * - **a**: The multiplicative key (must be coprime with the alphabet size).
 * - **b**: The additive key.
 *
 * This class ensures that 'a' is valid by checking its greatest common divisor (GCD) with the alphabet size.
 */
public class AffineKey extends Key {
    private final int a;
    private final int b;

    /**
     * Constructs an AffineKey with the given values.
     *
     * @param a            The multiplicative key (must be coprime with the alphabet size).
     * @param b            The additive key.
     * @param keyAlphabet  The alphabet used for encryption.
     * @throws IllegalArgumentException If 'a' is not coprime with the alphabet size.
     */
    public AffineKey(int a, int b, Alphabet keyAlphabet) {
        super(keyAlphabet);
        this.a = a;
        this.b = b;
        validate();
    }

    /**
     * Validates that 'a' is coprime with the alphabet size.
     *
     * @throws IllegalArgumentException If 'a' is not coprime with the alphabet size.
     */
    @Override
    public void validate() {
        if (gcd(a, keyAlphabet.size()) != 1) {
            throw new IllegalArgumentException("The value of 'a' must be coprime with the alphabet size (" + keyAlphabet.size() + ").");
        }
    }

    /**
     * Computes the greatest common divisor (GCD) of two numbers using recursion.
     *
     * @param x First number.
     * @param y Second number.
     * @return The greatest common divisor (GCD) of x and y.
     */
     public static int gcd(int x, int y) {
         while (y != 0) {
             int t = x;
             x = y;
             y = t % y;
         }
         return x;
    }

    /**
     * Gets the multiplicative key 'a'.
     *
     * @return The value of 'a'.
     */
    public int getA() {
        return a;
    }

    /**
     * Gets the additive key 'b'.
     *
     * @return The value of 'b'.
     */
    public int getB() {
        return b;
    }

    /**
     * Gets the alphabet associated with the key.
     *
     * @return The key alphabet .
     */
    public Alphabet getKeyAlphabet() {
        return this.keyAlphabet;
    }
}
