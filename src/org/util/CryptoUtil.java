package org.util;


import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
 
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import org.apache.commons.codec.binary.Base64;
public class CryptoUtil {
	private final static String SALT = "key";
	private final static int REPART = 5;
	public static String encode(String msg) {
		String temp = msg+"{"+SALT+"}";
		byte[] data = temp.getBytes();
		for (int i = 0;i < REPART;i++) {
			data = Base64.encodeBase64(data);
		}
		return new String(data);
	}
	public static String decode(String msg) {
        byte[] date = msg.getBytes();
        for (int i = 0; i <REPART; i++) {
            date = Base64.decodeBase64(date);
        }
        return new String(date).replace("{"+SALT+"}", "");
    }
	
	public static void main(String[] args) {
		String com = "asdqw";
		String s = CryptoUtil.encode(com);
		System.out.println(s);
		System.out.println(CryptoUtil.decode(s));
	}
}