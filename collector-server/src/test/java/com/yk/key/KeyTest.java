package com.yk.key;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yk.crypto.BinHexSHAUtil;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Utils;
import org.junit.Test;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class KeyTest {

    @Test
    public void test() {

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 256; i++) {
            if (i % 2 == 0)
                stringBuilder.append(0);
            else
                stringBuilder.append(1);
        }
        System.out.println(stringBuilder.length());
        System.out.println(stringBuilder);

        JSONArray jsonArray = new JSONArray();
        jsonArray.add("aa");
        jsonArray.add("bb");
        String b = jsonArray.toJSONString();
        Object a = JSONArray.parse("[\"aaa\",\"bbb\",\"ccc\"]");

        /*keyGen("1", true);
        keyGen("12", true);
        keyGen("123", true);
        keyGen("1234", true);
        keyGen("12345", true);
        keyGen("123456", true);
        keyGen("1234567", true);x     0
        2222222222222

        keyGen("12345678", true);
        keyGen("123456789", true);
        keyGen("123456789A", true);
        keyGen("123456789AB", true);
        keyGen("123456789ABC", true);
        keyGen("123456789ABCD", true);
        keyGen("123456789ABCDE", true);
        keyGen("123456789ABCDEF", true);
        keyGen("9", true);*/
        byte [] bytes = "Satoshi Nakamoto".getBytes();
        String hex_ = BinHexSHAUtil.byteArrayToHex(Sha256Hash.hash(bytes));
        keyGen(hex_, true);
//        byte[] bytesA = Utils.removeBytesFirst0FromBigInteger(new BigInteger(stringBuilder.toString(), 2).toByteArray(), 32);
//        byte[] bytesB = Utils.string2bytes(stringBuilder.toString());
//        System.out.println(bytesA.equals(bytesB));
//        keyGen(new BigInteger(stringBuilder.toString(), 2).toString(16), true);
    }

    public void keyGen(String hex, boolean compressed) {
        byte[] privateKey = Utils.bigIntegerToBytes(new BigInteger(hex, 16), 32);

        byte[] temp = new byte[privateKey.length + 1 + (compressed ? 1 : 0)];

        // 私钥前增加0x80
        byte[] single0x80 = new byte[]{(byte) 0x80};

        System.arraycopy(single0x80, 0, temp, 0, 1);
        System.arraycopy(privateKey, 0, temp, 1, privateKey.length);

        if (compressed) {
            // 私钥后增加0x01 -- 压缩格式 compressed format
            byte[] single0x01 = new byte[]{(byte) 0x01};
            System.arraycopy(single0x01, 0, temp, privateKey.length + 1, 1);
        }

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
        System.out.println(privateString);// KwDiBf89QgGbjEhKnhXJuH7LrciVrZi3qYjgd9MBZHVw2ree5zWk
    }
}
