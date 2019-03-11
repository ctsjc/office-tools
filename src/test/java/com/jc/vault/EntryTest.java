package com.jc.vault;

import org.junit.Test;

import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;


public class EntryTest {

   @Test
   public void encrypt() throws Exception {
       assertEquals("a7iYGx+0TOQO5eLA7LGppQ==",Entry.encrypt(getSecretKeySpec("a"),"abc"));
    }

    @Test
    public void decrypt()throws Exception  {
        assertEquals("abc",Entry.decrypt(getSecretKeySpec("a"),"a7iYGx+0TOQO5eLA7LGppQ=="));

    }

    private static SecretKeySpec getSecretKeySpec(String key) throws Exception {
        byte[] keyBytes = (key).getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        keyBytes = sha.digest(keyBytes);
        keyBytes = Arrays.copyOf(keyBytes, 16); // use only first 128 bit

        return new SecretKeySpec(keyBytes, "AES");
    }
}