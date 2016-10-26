package net.quedex.api.pgp;

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

public class BcEncryptor
{
    private static final int BUFFER_SIZE = 2 << 7;

    private final BcPGPDataEncryptorBuilder dataEncryptor;
    private final BcPublicKey publicKey;
    private final BcPrivateKey ourKey;

    public BcEncryptor(final BcPublicKey publicKey, final BcPrivateKey ourKey)
    {
        this.publicKey = checkNotNull(publicKey);
        this.ourKey = checkNotNull(ourKey);

        Security.addProvider(new BouncyCastleProvider());

        dataEncryptor = new BcPGPDataEncryptorBuilder(SymmetricKeyAlgorithmTags.AES_128);
        dataEncryptor.setWithIntegrityPacket(true);
        dataEncryptor.setSecureRandom(new SecureRandom());
    }

    public String encrypt(final String message, final boolean sign) throws PGPEncryptionException
    {
        try
        {
            final PGPSecretKey secretKey = ourKey.getSecretKey();
            final PGPPrivateKey privateKey = ourKey.getPrivateKey();

            final byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

            final PGPLiteralDataGenerator literalDataGenerator = new PGPLiteralDataGenerator();
            final PGPEncryptedDataGenerator encryptedDataGenerator = new PGPEncryptedDataGenerator(dataEncryptor);
            encryptedDataGenerator.addMethod(new BcPublicKeyKeyEncryptionMethodGenerator(publicKey.getEncryptionKey()));
            final PGPCompressedDataGenerator compressedDataGenerator = new PGPCompressedDataGenerator(PGPCompressedData.ZLIB);
            final PGPContentSignerBuilder signerBuilder = new BcPGPContentSignerBuilder(
                secretKey.getPublicKey().getAlgorithm(),
                HashAlgorithmTags.SHA256
            );
            final PGPSignatureGenerator signatureGenerator = new PGPSignatureGenerator(signerBuilder);
            signatureGenerator.init(PGPSignature.BINARY_DOCUMENT, privateKey);
            final PGPSignatureSubpacketGenerator spGen = new PGPSignatureSubpacketGenerator();
            spGen.setSignerUserID(false, (String) secretKey.getPublicKey().getUserIDs().next());
            signatureGenerator.setHashedSubpackets(spGen.generate());

            if (sign)
            {
                signatureGenerator.update(messageBytes);
            }

            final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            final Hashtable<String, String> headers = new Hashtable<>();
            headers.put("Version", "QPG");
            final ArmoredOutputStream armoredOut = new ArmoredOutputStream(bOut, headers);

            final OutputStream encryptedOut = encryptedDataGenerator.open(armoredOut, new byte[BUFFER_SIZE]);
            final OutputStream compressedOut = compressedDataGenerator.open(encryptedOut);

            if (sign)
            {
                signatureGenerator.generateOnePassVersion(false).encode(compressedOut);
            }

            final OutputStream literalOut = literalDataGenerator.open(
                compressedOut,
                PGPLiteralData.UTF8,
                PGPLiteralData.CONSOLE,
                messageBytes.length,
                new Date()
            );
            literalOut.write(messageBytes);
            literalDataGenerator.close();

            if (sign)
            {
                signatureGenerator.generate().encode(compressedOut);
            }

            compressedDataGenerator.close();
            encryptedDataGenerator.close();
            armoredOut.close();

            return new String(bOut.toByteArray(), StandardCharsets.UTF_8);

        }
        catch (PGPException | RuntimeException | IOException e)
        {
            throw new PGPEncryptionException("Error encrypting message", e);
        }
    }
}
