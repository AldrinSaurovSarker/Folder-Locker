import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

public class folderHidingHandler {
    private String folderPath;
    private String keyPathDirectory;

    public folderHidingHandler(String folderPath, String keyPathDirectory) {
        this.folderPath = folderPath;
        this.keyPathDirectory = keyPathDirectory;
    }

    public void decrypt(SecretKey secretKey) {
        try {
            File folder = new File(this.folderPath);
            File[] files = folder.listFiles();

            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".enc")) {
                    // Decrypt the file using AES decryption
                    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                    cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(new byte[16]));

                    FileInputStream inputStream = new FileInputStream(file);
                    byte[] inputBytes = new byte[(int) file.length()];
                    inputStream.read(inputBytes);

                    byte[] outputBytes = cipher.doFinal(inputBytes);

                    String outputFilePath = file.getParent() + "\\" + file.getName().replace(".enc", "");
                    File outputFile = new File(outputFilePath);
                    FileOutputStream outputStream = new FileOutputStream(outputFile);
                    outputStream.write(outputBytes);

                    if (!file.delete()) {
                        System.out.println("Failed to delete file: " + file.getAbsolutePath());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void encrypt(SecretKey secretKey) {
        try {
            File folder = new File(this.folderPath);
            File[] files = folder.listFiles();

            for (File file : files) {
                if (file.isFile()) {
                    // Encrypt the file using AES encryption
                    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                    cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(new byte[16]));

                    FileInputStream inputStream = new FileInputStream(file);
                    byte[] inputBytes = new byte[(int) file.length()];
                    inputStream.read(inputBytes);

                    byte[] outputBytes = cipher.doFinal(inputBytes);

                    String outputFilePath = file.getParent() + "\\" + file.getName() + ".enc";
                    File outputFile = new File(outputFilePath);
                    FileOutputStream outputStream = new FileOutputStream(outputFile);
                    outputStream.write(outputBytes);

                    if (!file.delete()) {
                        System.out.println("Failed to delete file: " + file.getAbsolutePath());
                    } else {
                        System.out.println("Files deleted successfully");
                    }

                    Path encryptedFilePath = outputFile.toPath();
                    Files.setAttribute(encryptedFilePath, "dos:hidden", true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SecretKey generateKey() {
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keyGenerator.init(128);
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] keyBytes = secretKey.getEncoded();
        Path keyPath = Paths.get(this.keyPathDirectory);

        try {
            Files.write(keyPath, keyBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return secretKey;
    }

    public SecretKey extractKey() {
        Path keyPath = Paths.get(this.keyPathDirectory);
        SecretKey secretKey = null;

        try {
            byte[] keyBytes = Files.readAllBytes(keyPath);
            secretKey = new SecretKeySpec(keyBytes, "AES");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return secretKey;
    }
}
