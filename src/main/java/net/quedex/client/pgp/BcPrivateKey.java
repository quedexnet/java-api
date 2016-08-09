package net.quedex.client.pgp;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public final class BcPrivateKey {

    private static final char[] EMPTY_CHAR_ARRAY = new char[0];

    private final PGPSecretKey secretKey;
    private final PGPPrivateKey privateKey;
    private final String fingerprint;
    private final ImmutableMap<Long, PGPPrivateKey> privateKeys;
    private final BcPublicKey publicKey;

    public static BcPrivateKey fromArmored(String armoredKeyString) throws PGPKeyInitialisationException {
        return fromArmored(armoredKeyString, EMPTY_CHAR_ARRAY);
    }

    public static BcPrivateKey fromArmored(String armoredKeyString, char[] passphrase)
            throws PGPKeyInitialisationException {
        return new BcPrivateKey(armoredKeyString, passphrase);
    }

    BcPrivateKey(String armoredKeyString, char[] passphrase) throws PGPKeyInitialisationException {
        try {
            PGPSecretKeyRing secKeyRing = new PGPSecretKeyRing(
                    new ArmoredInputStream(new ByteArrayInputStream(armoredKeyString.getBytes(StandardCharsets.US_ASCII))),
                    new BcKeyFingerprintCalculator());

            PBESecretKeyDecryptor decryptor = new BcPBESecretKeyDecryptorBuilder(new BcPGPDigestCalculatorProvider())
                    .build(passphrase);

            ImmutableMap.Builder<Long, PGPPrivateKey> builder = ImmutableMap.builder();
            List<PGPPublicKey> pubKeys = new ArrayList<>(2);

            for (Iterator iterator = secKeyRing.getSecretKeys(); iterator.hasNext(); ) {
                PGPSecretKey secretKey = (PGPSecretKey) iterator.next();
                PGPPrivateKey privateKey = secretKey.extractPrivateKey(decryptor);
                builder.put(privateKey.getKeyID(), privateKey);
                pubKeys.add(secretKey.getPublicKey());
            }

            this.secretKey = secKeyRing.getSecretKey();
            this.privateKeys = builder.build();
            this.privateKey = this.secretKey.extractPrivateKey(decryptor);
            if (pubKeys.size() >= 2) {
                this.publicKey = new BcPublicKey(pubKeys.get(0), pubKeys.get(1));
            } else {
                this.publicKey = new BcPublicKey(pubKeys.get(0), pubKeys.get(0));
            }

        } catch (PGPException | RuntimeException | IOException e) {
            throw new PGPKeyInitialisationException("Error instantiating a private key", e);
        }
        checkNotNull(this.secretKey);
        checkNotNull(this.privateKey);

        this.fingerprint = BcPublicKey.hexFingerprint(secretKey.getPublicKey());
    }

    PGPSecretKey getSecretKey() {
        return secretKey;
    }

    PGPPrivateKey getPrivateKey() {
        return privateKey;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    PGPPrivateKey getPrivateKeyWithId(long keyId) throws PGPKeyNotFoundException {
        if (!privateKeys.containsKey(keyId)) {
            throw new PGPKeyNotFoundException(
                    String.format("Key with id: %s not found", Long.toHexString(keyId).toUpperCase())
            );
        }
        return privateKeys.get(keyId);
    }

    public Collection<PGPPrivateKey> getPrivateKeys() {
        return privateKeys.values();
    }

    public BcPublicKey getPublicKey() {
        return publicKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || o.getClass() != getClass()) {
            return false;
        }

        BcPrivateKey that = (BcPrivateKey) o;

        return Objects.equal(fingerprint, that.fingerprint);
    }

    @Override
    public int hashCode() {
        return fingerprint.hashCode();
    }
}
