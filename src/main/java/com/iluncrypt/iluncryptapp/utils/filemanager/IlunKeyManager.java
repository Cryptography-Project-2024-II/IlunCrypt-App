package com.iluncrypt.iluncryptapp.utils.filemanager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * Extiende la funcionalidad de IlunFileManager para la escritura y lectura de llaves.
 */
public class IlunKeyManager {

    /**
     * Guarda una pareja de llaves (pública y privada) en dos archivos separados.
     * La clave pública se guarda con extensión .ilunpbk y la privada con .ilunpvk.
     * Se añade un checksum (SHA-256) al final de cada archivo.
     *
     * @param publicKey  La llave pública en bytes.
     * @param privateKey La llave privada en bytes.
     * @param stage      El Stage para mostrar los FileChooser.
     * @return Un arreglo de bytes que es la concatenación de ambas llaves (opcional), o null si se cancela.
     * @throws IOException              Si ocurre un error al escribir los archivos.
     * @throws NoSuchAlgorithmException Si no se encuentra el algoritmo de hash.
     */
    public static byte[] writePublicPrivateKeyFiles(byte[] publicKey, byte[] privateKey, Stage stage) throws IOException, NoSuchAlgorithmException {
        // Configurar FileChooser para la clave pública
        FileChooser fcPublic = new FileChooser();
        fcPublic.setTitle("Save Public Key");
        fcPublic.getExtensionFilters().add(new ExtensionFilter("Public Key (*.ilunpbk)", "*.ilunpbk"));
        fcPublic.setInitialFileName("public" + ".ilunpbk");
        File publicFile = fcPublic.showSaveDialog(stage);
        if (publicFile == null) {
            return null; // Usuario canceló la operación
        }

        // Configurar FileChooser para la clave privada
        FileChooser fcPrivate = new FileChooser();
        fcPrivate.setTitle("Save Private Key");
        fcPrivate.getExtensionFilters().add(new ExtensionFilter("Private Key (*.ilunpvk)", "*.ilunpvk"));
        fcPrivate.setInitialFileName("private" + ".ilunpvk");
        File privateFile = fcPrivate.showSaveDialog(stage);
        if (privateFile == null) {
            return null; // Usuario canceló la operación
        }

        // Calcular checksum (SHA-256) para cada llave
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] publicChecksum = digest.digest(publicKey);
        byte[] privateChecksum = digest.digest(privateKey);

        // Escribir la llave pública junto con su checksum
        try (FileOutputStream fos = new FileOutputStream(publicFile)) {
            fos.write(publicKey);
            fos.write(publicChecksum);
        }

        // Escribir la llave privada junto con su checksum
        try (FileOutputStream fos = new FileOutputStream(privateFile)) {
            fos.write(privateKey);
            fos.write(privateChecksum);
        }

        // Retornar la concatenación de ambas llaves (opcional, según lo que necesites)
        ByteBuffer buffer = ByteBuffer.allocate(publicKey.length + privateKey.length);
        buffer.put(publicKey);
        buffer.put(privateKey);
        return buffer.array();
    }

    /**
     * Guarda una llave única (simétrica o de cifradores clásicos) en un archivo con extensión .ilunk.
     * Se añade un checksum (SHA-256) al final del archivo.
     *
     * @param key   La llave en bytes.
     * @param stage El Stage para mostrar el FileChooser.
     * @return El contenido del archivo escrito en forma de byte[], o null si se cancela.
     * @throws IOException              Si ocurre un error al escribir el archivo.
     * @throws NoSuchAlgorithmException Si no se encuentra el algoritmo de hash.
     */
    public static byte[] writeUniqueKeyFile(byte[] key, Stage stage) throws IOException, NoSuchAlgorithmException {
        // Configurar FileChooser para la llave única
        FileChooser fcUnique = new FileChooser();
        fcUnique.setTitle("Save Symmetric Key");
        fcUnique.getExtensionFilters().add(new ExtensionFilter("Symmetric Key (*.ilunk)", "*.ilunk"));
        fcUnique.setInitialFileName("key" + ".ilunk");
        File uniqueFile = fcUnique.showSaveDialog(stage);
        if (uniqueFile == null) {
            return null; // Usuario canceló la operación
        }

        // Calcular checksum para la llave
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] checksum = digest.digest(key);

        // Escribir la llave y el checksum en el archivo
        try (FileOutputStream fos = new FileOutputStream(uniqueFile)) {
            fos.write(key);
            fos.write(checksum);
        }

        // Retornar el contenido del archivo escrito
        return Files.readAllBytes(uniqueFile.toPath());
    }

    /**
     * Abre un FileChooser para seleccionar un archivo de llave y retorna su contenido en un arreglo de bytes.
     * Permite seleccionar archivos con extensiones .ilunpbk, .ilunpvk y .ilunk.
     *
     * @param stage El Stage para mostrar el FileChooser.
     * @return El contenido del archivo llave en bytes, o null si se cancela la operación.
     * @throws IOException Si ocurre un error al leer el archivo.
     */
    public static byte[] readKeyFile(Stage stage) throws IOException {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select Key File");
        fc.getExtensionFilters().add(new ExtensionFilter("IlunCrypt Keys (*.ilunpbk, *.ilunpvk, *.ilunk)", "*.ilunpbk", "*.ilunpvk", "*.ilunk"));
        File keyFile = fc.showOpenDialog(stage);
        if (keyFile == null) {
            return null; // Usuario canceló la operación
        }
        return Files.readAllBytes(keyFile.toPath());
    }
}
