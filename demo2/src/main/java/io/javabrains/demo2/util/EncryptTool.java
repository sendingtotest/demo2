
// java
package io.javabrains.demo2.util;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EncryptTool {
    public static void main(String[] args) {
        String propPassword = System.getProperty("JASYPT_ENCRYPTOR_PASSWORD");
        if (propPassword != null) propPassword = propPassword.trim();

        String envPassword = System.getenv("JASYPT_ENCRYPTOR_PASSWORD");
        if (envPassword != null) envPassword = envPassword.trim();

        // Precedence: system property -> env var -> program arg
        String password = (propPassword != null && !propPassword.isEmpty()) ? propPassword
                : (envPassword != null && !envPassword.isEmpty() ? envPassword : null);

        String plaintext = null;

        if (password != null && args.length >= 1) {
            plaintext = args[0].trim();
        } else if (args.length >= 2) {
            password = args[0].trim();
            plaintext = args[1].trim();
        }

        if (password == null || password.isEmpty() || plaintext == null || plaintext.isEmpty()) {
            System.err.println("Usage:");
            System.err.println(" 1) With JVM system property: java -DJASYPT_ENCRYPTOR_PASSWORD=your-pass -jar your.jar <plaintext>");
            System.err.println(" 2) With env var: set JASYPT_ENCRYPTOR_PASSWORD and run: java -jar your.jar <plaintext>");
            System.err.println(" 3) As args: java -jar your.jar <encryptorPassword> <plaintext>");
            System.exit(1);
        }

        // Load algorithm from application.properties if available
        String algorithm = "PBEWithMD5AndDES";
        Properties props = new Properties();
        try (InputStream in = EncryptTool.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) {
                props.load(in);
                String alg = props.getProperty("jasypt.encryptor.algorithm");
                if (alg != null && !alg.trim().isEmpty()) {
                    algorithm = alg.trim();
                }
            }
        } catch (IOException e) {
            // ignore and use default algorithm
        }

        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(password);
        encryptor.setAlgorithm(algorithm);

        String encrypted = encryptor.encrypt(plaintext);
        System.out.println("ENC(" + encrypted + ")");
    }
}
