package com.yk.bitcoin.manager;

import com.google.common.primitives.UnsignedBytes;
import com.yk.crypto.Base58;

public class Address implements Comparable<Address> {

    private final byte[] bytes;

    private Address(byte[] bytes) {
        this.bytes = bytes;
    }

    public static Address fromPubKey(byte[] bytes) {
        return new Address(bytes);
    }

    public String toBase58() {
        return Base58.encode(bytes);
    }

    @Override
    public int compareTo(Address o) {
        return UnsignedBytes.lexicographicalComparator().compare(this.bytes, o.bytes);
    }
}
