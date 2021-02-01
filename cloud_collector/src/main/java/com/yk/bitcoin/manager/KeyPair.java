package com.yk.bitcoin.manager;

public class KeyPair {
    byte[] privateKeyEncode;

    byte[] publicKeyEncode;

    private String privateKeyBitCoinString;

    private String publicKeyBitCoinString;

    public byte[] getPrivateKeyEncode() {
        return privateKeyEncode;
    }

    public byte[] getPublicKeyEncode() {
        return publicKeyEncode;
    }

    public String getPrivateKeyBitCoinString() {
        return privateKeyBitCoinString;
    }


    public String getPublicKeyBitCoinString() {
        return publicKeyBitCoinString;
    }

    public KeyPair() {
    }

    public KeyPair(byte[] privateKeyEncode, String privateKeyBitCoinString, byte[] publicKeyEncode, String publicKeyBitCoinString) {
        this.privateKeyEncode = privateKeyEncode;
        this.privateKeyBitCoinString = privateKeyBitCoinString;
        this.publicKeyEncode = publicKeyEncode;
        this.publicKeyBitCoinString = publicKeyBitCoinString;
    }
}
