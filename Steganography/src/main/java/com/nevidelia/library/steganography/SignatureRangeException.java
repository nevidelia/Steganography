package com.nevidelia.library.steganography;

public class SignatureRangeException extends Exception {
    SignatureRangeException(String error) {
        System.out.println("Error: " + error);
    }
}