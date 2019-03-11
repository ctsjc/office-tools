package com.jc.vault;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.cli.*;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.stream.Stream;

public class Entry {

    final static String algorithm="AES";

    final static String fileNameOption = "f";
    final static String keyOption = "k";
    final static String decryptOption = "d";
    final static String encryptOption = "e";
    final static String encryptFileOption = "ef";

    public static String encrypt(SecretKeySpec key, String data){
        byte[] dataToSend = data.getBytes();
        byte[] encryptedData = "".getBytes();
        Cipher c = null;
        try{
            c = Cipher.getInstance(algorithm);
            c.init(Cipher.ENCRYPT_MODE, key);
            encryptedData = c.doFinal(dataToSend);
        }catch (Exception e){
            e.printStackTrace();
        }
        return  new String(new Base64().encode(encryptedData));//.toString();
    }

    public static String decrypt(SecretKeySpec key, String data){
        Cipher c = null;
        byte[] decrypted = null;
        try {
            c = Cipher.getInstance(algorithm);
            c.init(Cipher.DECRYPT_MODE, key);
            decrypted = c.doFinal(new Base64().decode(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(decrypted);
    }


    public static void encrypt(SecretKeySpec key, File file){
        try (Stream<String> stream = Files.lines(Paths.get(file.getAbsolutePath()))) {
            stream.map(s -> encrypt(key,s)).forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void decrypt(SecretKeySpec key, File file){
        try (Stream<String> stream = Files.lines(Paths.get(file.getAbsolutePath()))) {

            stream.map(s -> decrypt(key,s)).forEach(System.out::println);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws Exception {


        CommandLine cmd = getCommandLine(args);
        String filename=cmd.getOptionValue(fileNameOption);
        String key=cmd.getOptionValue(keyOption);
        String valueToEncrypt=cmd.getOptionValue(encryptOption);
        String valueToDecrypy=cmd.getOptionValue(decryptOption);

        System.out.println(filename+"\t"+key+"\t"+valueToEncrypt+"\t"+valueToDecrypy);

        SecretKeySpec secretKeySpec = getSecretKeySpec(key);

        if(cmd.hasOption(encryptOption)) {

            String encryptValue = Entry.encrypt(secretKeySpec, valueToEncrypt);
            System.out.println("\nEncrypted Value\n\t"+valueToEncrypt+"\n\t"+encryptValue);
        }


        if(cmd.hasOption(encryptFileOption)) {
            String toEncryptFile=cmd.getOptionValue(encryptFileOption);
            Entry.encrypt(secretKeySpec, new File(toEncryptFile));
        }
        if(cmd.hasOption(decryptOption)) {
            System.out.println("\nDecrypted Value\n\t"+valueToDecrypy+"\n\t"+Entry.decrypt(secretKeySpec,valueToDecrypy));
        }

        if(cmd.hasOption(fileNameOption)) {
            System.out.println("\nDecrypted Values From File");
            Entry.decrypt(secretKeySpec, new File(filename));
        }

    }

    private static CommandLine getCommandLine(String[] args) throws ParseException {
        System.out.println("-f /j/pass.txt -k key -d value -e value");
        final Options options = new Options();
        options.addOption(new Option(fileNameOption, true, "File name"));
        options.addOption(new Option(keyOption, true, "key"));
        options.addOption(new Option(decryptOption, true, "value to decrypt."));
        options.addOption(new Option(encryptOption, true, "value to encrypt."));
        options.addOption(new Option(encryptFileOption, true, "value to encrypt."));
        CommandLineParser parser = new DefaultParser();
        return parser.parse( options, args);
    }

    private static SecretKeySpec getSecretKeySpec(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] keyBytes = (key).getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        keyBytes = sha.digest(keyBytes);
        keyBytes = Arrays.copyOf(keyBytes, 16); // use only first 128 bit

        return new SecretKeySpec(keyBytes, "AES");
    }


}
