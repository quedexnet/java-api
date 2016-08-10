package net.quedex.client.pgp;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.PGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyKeyEncryptionMethodGenerator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Date;
import java.util.Hashtable;

import static com.google.common.base.Preconditions.checkNotNull;

public class BcEncryptor {

    private static final int BUFFER_SIZE = 2 << 7;

    private final BcPGPDataEncryptorBuilder dataEncryptor;
    private final BcPublicKey publicKey;
    private final BcPrivateKey ourKey;

    public BcEncryptor(BcPublicKey publicKey, BcPrivateKey ourKey) {
        this.publicKey = checkNotNull(publicKey);
        this.ourKey = checkNotNull(ourKey);

        Security.addProvider(new BouncyCastleProvider());

        dataEncryptor = new BcPGPDataEncryptorBuilder(SymmetricKeyAlgorithmTags.AES_256);
        dataEncryptor.setWithIntegrityPacket(true);
        dataEncryptor.setSecureRandom(new SecureRandom());
    }

    public String encrypt(String message, boolean sign) throws PGPEncryptionException, PGPKeyNotFoundException {

        try {
            PGPSecretKey secretKey = ourKey.getSecretKey();
            PGPPrivateKey privateKey = ourKey.getPrivateKey();

            byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

            PGPLiteralDataGenerator literalDataGenerator = new PGPLiteralDataGenerator();
            PGPEncryptedDataGenerator encryptedDataGenerator = new PGPEncryptedDataGenerator(dataEncryptor);
            encryptedDataGenerator.addMethod(new BcPublicKeyKeyEncryptionMethodGenerator(publicKey.getEncryptionKey()));
            PGPCompressedDataGenerator compressedDataGenerator = new PGPCompressedDataGenerator(PGPCompressedData.ZLIB);
            PGPContentSignerBuilder signerBuilder = new BcPGPContentSignerBuilder(
                    secretKey.getPublicKey().getAlgorithm(),
                    HashAlgorithmTags.SHA256
            );
            PGPSignatureGenerator signatureGenerator = new PGPSignatureGenerator(signerBuilder);
            signatureGenerator.init(PGPSignature.BINARY_DOCUMENT, privateKey);
            PGPSignatureSubpacketGenerator spGen = new PGPSignatureSubpacketGenerator();
            spGen.setSignerUserID(false, (String) secretKey.getPublicKey().getUserIDs().next());
            signatureGenerator.setHashedSubpackets(spGen.generate());
            if (sign) {
                signatureGenerator.update(messageBytes);
            }

            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            Hashtable<String, String> headers = new Hashtable<>();
            headers.put("Version", "QPG");
            ArmoredOutputStream armoredOut = new ArmoredOutputStream(bOut, headers);

            OutputStream encryptedOut = encryptedDataGenerator.open(armoredOut, new byte[BUFFER_SIZE]);
            OutputStream compressedOut = compressedDataGenerator.open(encryptedOut);
            if (sign) {
                signatureGenerator.generateOnePassVersion(false).encode(compressedOut);
            }
            OutputStream literalOut = literalDataGenerator.open(
                    compressedOut,
                    PGPLiteralData.UTF8,
                    PGPLiteralData.CONSOLE,
                    messageBytes.length,
                    new Date()
            );
            literalOut.write(messageBytes);
            literalDataGenerator.close();

            if (sign) {
                signatureGenerator.generate().encode(compressedOut);
            }

            compressedDataGenerator.close();
            encryptedDataGenerator.close();
            armoredOut.close();

            return new String(bOut.toByteArray(), StandardCharsets.UTF_8);

        } catch (PGPException | RuntimeException | IOException e) {
            throw new PGPEncryptionException("Error encrypting message", e);
        }
    }
}
