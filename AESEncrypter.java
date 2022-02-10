package com.example.SMSApp;

import android.util.Log;

import java.security.Key;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64.Encoder;
import java.util.Base64.Decoder;

public class AESEncrypter 
{
    private static final String ALGO = "AES";
    private byte[] keyValue;
    private static final String TAG = AESEncrypter.class.getSimpleName();

    public AESEncrypter(String key)
    {
        keyValue = key.getBytes();
    }

    public String encrypt(String Data) throws Exception
    {
        Key key = generateKey();
        javax.crypto.Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE,key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = java.util.Base64.getEncoder().encodeToString(encVal);
        return encryptedValue;
    }

    public String decrypt(String encryptedData) throws Exception
    {
        try
        {
            Key key = generateKey();
            javax.crypto.Cipher c = javax.crypto.Cipher.getInstance(ALGO);
            c.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedValue = java.util.Base64.getDecoder().decode(encryptedData);
            byte[] decValue = c.doFinal(decodedValue);
            String decryptedValue = new String(decValue);
            return decryptedValue;
        }
        catch(Exception e)
        {
            Log.d(TAG, "decoded value: I am here!");
            Logger.getLogger(AESEncrypter.class.getName()).log(Level.SEVERE, null, e);
            return "no message";
        }


    }

    private Key generateKey() throws Exception
    {
        Key key = new SecretKeySpec(keyValue, ALGO);
        return key;
    }

    public static void main(String args[])
    {
        try
        {
            AESEncrypter aes = new AESEncrypter("lv94eptlvihaqqsr");
            String encdata = aes.encrypt("ALARM NAME: Disk_Is_Empty!");
            System.out.println("Encrypted Data - " + encdata);
            String decdata = aes.decrypt(encdata);
            System.out.println("Decrypted Data - " + decdata);
        }
        catch(Exception ex)
        {
            Logger.getLogger(AESEncrypter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}