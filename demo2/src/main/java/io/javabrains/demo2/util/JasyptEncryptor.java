package io.javabrains.demo2.util;

// Small utility to encrypt strings to be used with Jasypt (PBEWithMD5AndDES)
public class JasyptEncryptor {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java -cp <classpath> io.javabrains.demo2.util.JasyptEncryptor <password> <plaintext>");
            System.exit(1);
        }
        String password = args[0];
        String plaintext = args[1];
        org.jasypt.encryption.pbe.StandardPBEStringEncryptor encryptor = new org.jasypt.encryption.pbe.StandardPBEStringEncryptor();
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        encryptor.setPassword(password);
        String encrypted = encryptor.encrypt(plaintext);
        System.out.println("ENC(" + encrypted + ")");
    }
}

