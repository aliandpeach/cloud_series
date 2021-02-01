package com.yk.bitcoin.service.impl;

import com.yk.bitcoin.manager.KeyPair;
import com.yk.bitcoin.service.IGenerateBitcoinKey;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Utils;
import org.bitcoinj.params.AbstractBitcoinNetParams;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;


@Service
public class GenerateBitcoinKeyImpl implements IGenerateBitcoinKey {
    /**
     * @return ECKey
     */
    @Override
    public ECKey generateECKey() {
        ECKey ecKey = new ECKey();
        return ecKey;
    }

    @Override
    public ECKey generateSecureRandom(SecureRandom random) {
        if (null == random) {
            random = new SecureRandom();
        }
        // 随机生成256bit的私钥，也就是32长度的byte[]
        byte[] privateKey = new byte[32]; // 这个是私钥
        random.nextBytes(privateKey);
        return ECKey.fromPrivate(privateKey);
    }

    @Override
    public String toBase58String(byte[] pubBytes) {
        return toBitcoinPublicAddr(pubBytes);
    }

    @Override
    public String toWIFString(byte[] priBytes) {
        return toBitcoinPrivateWIF(priBytes);
    }
}
