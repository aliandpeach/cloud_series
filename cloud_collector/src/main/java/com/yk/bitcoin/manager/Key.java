package com.yk.bitcoin.manager;

import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointUtil;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

public class Key {

    private static final SecureRandom SECURE_RANDOM;

    private static final X9ECParameters CURVE_PARAMS = CustomNamedCurves.getByName("secp256k1");

    private static final ECDomainParameters EC_DOMAIN_PARAMETERS;

    private BigInteger priv;

    private byte[] pri;

    private byte[] pub;

    static {
        FixedPointUtil.precompute(CURVE_PARAMS.getG());
        EC_DOMAIN_PARAMETERS = new ECDomainParameters(CURVE_PARAMS.getCurve(),
                CURVE_PARAMS.getG(),
                CURVE_PARAMS.getN(),
                CURVE_PARAMS.getH());
        SECURE_RANDOM = new SecureRandom();
    }

    public Key() {
        this(SECURE_RANDOM);
    }

    public Key(SecureRandom secureRandom) {
        // 以下代码是通过org.bouncycastle生成的公私钥
        ECKeyPairGenerator generator = new ECKeyPairGenerator();
        ECKeyGenerationParameters keygenParams = new ECKeyGenerationParameters(EC_DOMAIN_PARAMETERS, secureRandom);
        generator.init(keygenParams);
        AsymmetricCipherKeyPair keypair = generator.generateKeyPair();
        ECPrivateKeyParameters privParams = (ECPrivateKeyParameters) keypair.getPrivate();
        ECPublicKeyParameters pubParams = (ECPublicKeyParameters) keypair.getPublic();

        //如果使用BigInteger.toByteArray获取byte[],要注意第一位是0需要丢弃掉
        priv = privParams.getD();

        pri = Utils.bigIntegerToBytes(priv, 32);
        ECPoint ecPoint = pubParams.getQ();
        pub = ecPoint.getEncoded(true);
    }

    public static KeyPair generateKeyByRandom(SecureRandom secureRandom) {
        // 随机生成256bit的私钥，也就是32长度的byte[]
        byte[] privateKey = new byte[32]; // 这个是私钥
        secureRandom.nextBytes(privateKey);

        byte[] temp = new byte[privateKey.length + 1 + 1];

        // 私钥前增加0x80
        byte[] single0x80 = new byte[]{(byte) 0x80};
        // 私钥后增加0x01 -- 压缩格式 compressed format
        byte[] single0x01 = new byte[]{(byte) 0x01};

        System.arraycopy(single0x80, 0, temp, 0, 1);
        System.arraycopy(privateKey, 0, temp, 1, privateKey.length);
        System.arraycopy(single0x01, 0, temp, privateKey.length + 1, 1);

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
        //System.out.println(string); //和107行结果一样


        byte[] ff = new byte[temp.length + 4];
        System.arraycopy(temp, 0, ff, 0, temp.length);
        System.arraycopy(checksum, 0, ff, temp.length, 4);
        privateString = Base58.encode(ff);
        //System.out.println(privateString);//和100行结果一样

        // System.out.println("L5gMDJQRbx5dnT5azAbt2iGoqSF7uPvEUQYcGiiAQNnuGAVjpfc4");

        // 使用上述的私钥 根据椭圆曲线生成公钥
        ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");
        ECPoint pointQ = spec.getG().multiply(new BigInteger(1, privateKey));
        byte[] publicKey = pointQ.getEncoded(true);
        byte[] sha256Bytes = Sha256Hash.hash(publicKey);

        RIPEMD160Digest digest = new RIPEMD160Digest();
        digest.update(sha256Bytes, 0, sha256Bytes.length);
        byte[] ripemd160Bytes = new byte[digest.getDigestSize()];
        digest.doFinal(ripemd160Bytes, 0);


        byte[] networkID = new BigInteger("00", 16).toByteArray();

        byte[] extendedRipemd160Bytes = add(networkID, ripemd160Bytes);

        byte[] twiceSha256Bytes = Sha256Hash.hash(Sha256Hash.hash(extendedRipemd160Bytes));

        byte[] checksumPub = new byte[4];

        System.arraycopy(twiceSha256Bytes, 0, checksumPub, 0, 4);

        byte[] binaryBitcoinAddressBytes = add(extendedRipemd160Bytes, checksumPub);

        String bitcoinAddress = Base58.encode(binaryBitcoinAddressBytes);
        // System.out.println("bitcoinAddress = " + bitcoinAddress);


        // 通过bitcoinj的ECKey类生成公钥(私钥是第一行生成的随机数)
        /*ECKey ecKey = ECKey.fromPrivate(bytes);
        BigInteger priv = ecKey.getPrivKey();
        byte[] bytes2Prix = priv.toByteArray();
        byte[] bytes2Priv = ecKey.getPrivKeyBytes();

        byte[] bytes2pub = ecKey.getPubKey();
        byte[] bytes2pubHash = ecKey.getPubKeyHash();


        LegacyAddress address = LegacyAddress.fromKey(new AbstractBitcoinNetParams() {
            @Override
            public String getPaymentProtocolId() {
                return null;
            }
        }, ecKey);
        String addressString = address.toBase58();
        System.out.println("bitcoinAddress = " + addressString);*/

        return new KeyPair(privateKey, privateString, publicKey, bitcoinAddress);
    }

    /**
     * 两个byte[]数组相加
     *
     * @param dataF f
     * @param dataS s
     * @return byte[]
     */
    public static byte[] add(byte[] dataF, byte[] dataS) {

        byte[] result = new byte[dataF.length + dataS.length];
        System.arraycopy(dataF, 0, result, 0, dataF.length);
        System.arraycopy(dataS, 0, result, dataF.length, dataS.length);

        return result;
    }
}
