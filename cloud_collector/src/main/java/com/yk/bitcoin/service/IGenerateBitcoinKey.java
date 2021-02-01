package com.yk.bitcoin.service;

import com.yk.bitcoin.manager.Base58;
import com.yk.bitcoin.manager.KeyPair;
import com.yk.bitcoin.manager.Sha256Hash;
import com.yk.bitcoin.manager.Utils;
import org.bitcoinj.core.ECKey;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

public interface IGenerateBitcoinKey {

    public static final ECNamedCurveParameterSpec SPEC = ECNamedCurveTable.getParameterSpec("secp256k1");

    ECKey generateECKey();

    ECKey generateSecureRandom(SecureRandom random);

    String toBase58String(byte[] pubBytes);

    String toWIFString(byte[] priBytes);

    default String toBitcoinPrivateWIF(byte[] privateBytes) {

        byte[] temp = new byte[privateBytes.length + 1 + 1];
        // 私钥前增加0x80
        byte[] single0x80 = new byte[]{(byte) 0x80};
        // 私钥后增加0x01 -- 压缩格式 compressed format
        byte[] single0x01 = new byte[]{(byte) 0x01};

        System.arraycopy(single0x80, 0, temp, 0, 1);
        System.arraycopy(privateBytes, 0, temp, 1, privateBytes.length);
        System.arraycopy(single0x01, 0, temp, privateBytes.length + 1, 1);

        // temp进行两次sha256
        byte[] hash = Sha256Hash.hashTwice(temp);

        // 取两次hash256后的前四位 -- checksum
        byte[] checksum = new byte[4];
        ByteBuffer bbuffer = ByteBuffer.allocate(hash.length);
        bbuffer.put(hash);
        // 此时的position为32，必须flip后，把position改为0，否则get不到任何数据还会异常
        bbuffer.flip();
        bbuffer.get(checksum, 0, 4);

        // 下面把temp和checksum合并起来
        ByteBuffer buffer2PrivateKey = ByteBuffer.allocate(temp.length + 4);
        buffer2PrivateKey.put(temp);
        buffer2PrivateKey.put(checksum);
        buffer2PrivateKey.flip();
        byte[] f = new byte[temp.length + 4];
        buffer2PrivateKey.get(f);
        String privateString = Base58.encode(f);
        return privateString;
    }
    default String toBitcoinPublicAddr(byte[] publicBytes) {

        byte[] sha256Bytes = Sha256Hash.hash(publicBytes);

        RIPEMD160Digest digest = new RIPEMD160Digest();
        digest.update(sha256Bytes, 0, sha256Bytes.length);
        byte[] ripemd160Bytes = new byte[digest.getDigestSize()];
        digest.doFinal(ripemd160Bytes, 0);


        byte[] networkID = new BigInteger("00", 16).toByteArray();

        byte[] extendedRipemd160Bytes = Utils.add(networkID, ripemd160Bytes);

        byte[] twiceSha256Bytes = Sha256Hash.hash(Sha256Hash.hash(extendedRipemd160Bytes));

        byte[] checksumPub = new byte[4];

        System.arraycopy(twiceSha256Bytes, 0, checksumPub, 0, 4);

        byte[] binaryBitcoinAddressBytes = Utils.add(extendedRipemd160Bytes, checksumPub);

        String bitcoinAddress = Base58.encode(binaryBitcoinAddressBytes);
        return bitcoinAddress;
    }
    default byte[] calcBitcoinPublicKeyBySecp256k1(byte[] privateBytes) {
        // 根据椭圆曲线生成公钥
        ECPoint pointQ = SPEC.getG().multiply(new BigInteger(1, privateBytes));
        byte[] publicKey = pointQ.getEncoded(true);
        return publicKey;
    }
}
