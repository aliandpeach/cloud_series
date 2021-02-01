package com.yk.bitcoin.manager;

import com.google.common.primitives.UnsignedBytes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

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
