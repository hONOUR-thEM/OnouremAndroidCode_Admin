package com.onourem.android.activity.ui.dashboard.subscription.ccavenue.utility;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class AesCryptUtil {
    Cipher ecipher;
    Cipher dcipher;
    /**
     * Input a string that will be md5 hashed to create the key.
     * @return void, cipher initialized
     */

    public AesCryptUtil(String key){
        SecretKeySpec skey = new SecretKeySpec(getMD5(key), "AES");
        this.setupCrypto(skey);
    }

    private void setupCrypto(SecretKey key){
        // Create an 8-byte initialization vector
        byte[] iv = new byte[]
                {
                        0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f
                };

        AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
        try
        {
            ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            // CBC requires an initialization vector
            ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Input is a string to encrypt.
     * @return a Hex string of the byte array
     */
    public String encrypt(String plaintext){
        try{
            byte[] ciphertext = ecipher.doFinal(plaintext.getBytes("UTF-8"));
            return this.byteToHex(ciphertext);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
    /**
     * Input encrypted String represented in HEX
     * @return a string decrypted in plain text
     */
    public String decrypt(String hexCipherText) throws Exception{

        String plaintext = new String(dcipher.doFinal(this.hexToByte(hexCipherText)), "UTF-8");
        return  plaintext;

    }

    public String decrypt(byte[] ciphertext){
        try{
            String plaintext = new String(dcipher.doFinal(ciphertext), "UTF-8");
            return  plaintext;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] getMD5(String input){
        try{
            byte[] bytesOfMessage = input.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            return md.digest(bytesOfMessage);
        }  catch (Exception e){
            return null;
        }
    }

    static final String HEXES = "0123456789ABCDEF";

    public static String byteToHex( byte [] raw ) {
        if ( raw == null ) {
            return null;
        }
        String result = "";
        for (int i=0; i < raw.length; i++) {
            result +=
                    Integer.toString( ( raw[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return result;
    }

    public static byte[] hexToByte( String hexString){
        int len = hexString.length();
        byte[] ba = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            ba[i/2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i+1), 16));
        }
        return ba;
    }

    public static String asHex(byte buf[]){
        StringBuffer strbuf = new StringBuffer(buf.length * 2);
        for(int i = 0; i < buf.length; i++){
            if((buf[i] & 0xff) < 16)
                strbuf.append("0");
            strbuf.append(Long.toString(buf[i] & 0xff, 16));
        }
        return strbuf.toString();
    }


    public static void main(String args[]){

        String result=null;
        String err=null;
        String key = null;
        String data = null;
        String action = null;

        //4d6fb8d865cb588c537f5c8f9807308ca488d3b991b8b581f173f8515e4986fe17d0ce39217c4faa3a7254fe1eab1b6dbf5c0d87c3f58583723678d0e56384064bf435f15bd89f307c27cbeb24cd5884a2b73bde274bae12c9c96aed1a9141cff0b243edcb4f365e905e121f19c6809d2e495a5e39092dc3ffab27138dda6eb75dc15f2c5c188ecfe44d71c85284c5e02436589b1900f57f08e8a2eb8664e004664f4155e39b0e9595b893374024179bc137d54879ad26944f759942cd839a7119d1b22589edd10f4347730804184294d7ac8b88f7b9256c667a0d4a8a7314f3eaa19f51268d8dca0666fc62e65e886c15fdb389f2ca659eda2ebe8d3276ccfea90224932f657f9d6d63af6715332fea5409dec086e14e62a20241d1c9b4e98a3ec13dd65392d66637516fceb8e0e33f62bddc5ebc5199592ef7441594117f3d8e26c5edc61e6c85705d6e9568850d09b4568d6cafe1d8e880c50259a95710ce6bcd0c7d2e64e87d12eede19e368273ea685b26e3d3770bc04b0ca4051e5b520927a941d8524f61d57c507885e8f04b25ac8f8f3131001e9099dae269167fb2739c61d7fae93655839020a9a6b2c129f57b1a780917ff4f51e00a7ae49b459552321640e484b2245bdec37bc1f9e3ac6ae32023ab697295a442595c5db8b83d679164aefd35fcbfdcc7b51acb2135919

        if(args==null || args.length<3)
            err="error: missing one or more arguments. Usage: AesCryptUtil key data <enc|dec>";
        else {
            key = args[0];
            data = args[1];
            action = args[2];

            if(key==null)
                err="error: no key";
            else
            if(key.length()<32)
                err="error: key length less than 32 bytes";
            else
            if(data==null || action==null)
                err="error: no data";
            else
            if(action==null)
                err="error: no action";
            else
            if(!action.equals("enc") && !action.equals("dec"))
                err="error: invalid action";
        }

        if(err==null) {
            try {
                AesCryptUtil encrypter = new AesCryptUtil(key);

                if(action.equals("enc"))
                    result = encrypter.encrypt(data);
                else
                    result = encrypter.decrypt(data);
            }
            catch (Exception e) {
                err="error : Exception in performing the requested operation : " + e;
            }
        }
        if(result!=null)
            System.out.println(result);
        else
            System.out.println(err);
    }

}
