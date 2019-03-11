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


    public static void decrypt(SecretKeySpec key, File file){
        try (Stream<String> stream = Files.lines(Paths.get(file.getAbsolutePath()))) {

            stream.map(s -> decrypt(key,s)).forEach(System.out::println);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws Exception {

        String fileNameOption = "f";
        CommandLine cmd = getCommandLine(args, fileNameOption);
        String filename=cmd.getOptionValue(fileNameOption);
        String key=cmd.getOptionValue("k");
        String value=cmd.getOptionValue("d");
        System.out.println(filename+"\t"+key+"\t"+value);

        SecretKeySpec secretKeySpec = getSecretKeySpec(key);

        if(cmd.hasOption("d")) {
            String encryptValue = Entry.encrypt(secretKeySpec, value);
            System.out.println(encryptValue);
        }
        //System.out.println(Entry.decrypt(secretKeySpec,encryptValue));

        if(cmd.hasOption(fileNameOption))
            Entry.decrypt(secretKeySpec,new File(filename));

    }

    private static CommandLine getCommandLine(String[] args, String fileNameOption) throws ParseException {
        System.out.println("-f /j/pass.txt -k key -d value");
        final Options options = new Options();
        options.addOption(new Option(fileNameOption, true, "File name"));

        options.addOption(new Option("k", true, "key"));
        options.addOption(new Option("d", true, "value to decrypt."));
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
