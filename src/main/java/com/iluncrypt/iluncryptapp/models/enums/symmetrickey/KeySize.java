package com.iluncrypt.iluncryptapp.models.enums.symmetrickey;

import java.util.ArrayList;
import java.util.List;

/**
 * Enumeration of supported key sizes for symmetric encryption.
 */
public enum KeySize {
    AES_128(128, SymmetricKeyAlgorithm.AES),
    AES_192(192, SymmetricKeyAlgorithm.AES),
    AES_256(256, SymmetricKeyAlgorithm.AES),
    DES_56(56, SymmetricKeyAlgorithm.DES),
    TDES_112(112, SymmetricKeyAlgorithm.TDES),
    TDES_168(168, SymmetricKeyAlgorithm.TDES);

    private final int size;
    private final SymmetricKeyAlgorithm algorithm;

    KeySize(int size, SymmetricKeyAlgorithm algorithm) {
        this.size = size;
        this.algorithm = algorithm;
    }

    public SymmetricKeyAlgorithm getAlgorithm() {
        return algorithm;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return size + " bits";
    }

    /**
     * Retrieves a `KeySize` enum from an integer key size and algorithm.
     *
     * @param size     The key size in bits.
     * @param algorithm The algorithm to which the key size belongs.
     * @return The corresponding `KeySize` enum.
     * @throws IllegalArgumentException If the key size is not valid for the given algorithm.
     */
    public static KeySize fromSize(int size, SymmetricKeyAlgorithm algorithm) {
        for (KeySize keySize : values()) {
            if (keySize.getSize() == size && keySize.getAlgorithm() == algorithm) {
                return keySize;
            }
        }
        throw new IllegalArgumentException("Invalid KeySize: " + size + " bits for algorithm " + algorithm);
    }

    /**
     * Returns a list of valid key sizes for a given symmetric encryption algorithm.
     *
     * @param algorithm The symmetric encryption algorithm.
     * @return A list of valid `KeySize` values for the given algorithm.
     */
    public static List<KeySize> getValidKeySizes(SymmetricKeyAlgorithm algorithm) {
        List<KeySize> validSizes = new ArrayList<>();
        for (KeySize keySize : values()) {
            if (keySize.getAlgorithm() == algorithm) {
                validSizes.add(keySize);
            }
        }
        return validSizes;
    }
}