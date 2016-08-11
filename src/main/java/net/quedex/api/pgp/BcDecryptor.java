package net.quedex.api.pgp;

import com.google.common.io.ByteStreams;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.bc.BcPGPObjectFactory;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentVerifierBuilderProvider;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyDataDecryptorFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import static com.google.common.base.Preconditions.checkNotNull;

public class BcDecryptor {

    private static final long HIDDEN_RECIPIENT_KEY_ID = 0;

    private final BcPublicKey publicKey;
    private final BcPrivateKey ourKey;

    public BcDecryptor(BcPublicKey publicKey, BcPrivateKey ourKey) {
        this.publicKey = checkNotNull(publicKey);
        this.ourKey = checkNotNull(ourKey);
    }

    public String decrypt(String message)
            throws PGPDecryptionException, PGPKeyNotFoundException, PGPUnknownRecipientException, PGPInvalidSignatureException {
        try {
            InputStream in = PGPUtil.getDecoderStream(new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8)));

            PGPObjectFactory encryptedFactory = new BcPGPObjectFactory(in);
            Object object = encryptedFactory.nextObject();

            PGPEncryptedDataList encryptedDataList;

            if (object instanceof PGPEncryptedDataList) {
                encryptedDataList = (PGPEncryptedDataList) object;
            } else {
                encryptedDataList = (PGPEncryptedDataList) encryptedFactory.nextObject();
            }

            PGPPublicKeyEncryptedData encryptedData = null;
            InputStream clear = null;

            for (Iterator it = encryptedDataList.getEncryptedDataObjects(); it.hasNext(); ) {
                encryptedData = (PGPPublicKeyEncryptedData) it.next();

                if (encryptedData.getKeyID() == HIDDEN_RECIPIENT_KEY_ID) {

                    for (final PGPPrivateKey keyToCheck : ourKey.getPrivateKeys()) {
                        try {
                            clear = encryptedData.getDataStream(new BcPublicKeyDataDecryptorFactory(keyToCheck));
                            break;
                        } catch (Exception e) { /* fall through and retry */ }
                    }
                } else {
                    try {
                        PGPPrivateKey privateKey = ourKey.getPrivateKeyWithId(encryptedData.getKeyID());
                        clear = encryptedData.getDataStream(new BcPublicKeyDataDecryptorFactory(privateKey));
                    } catch (PGPKeyNotFoundException e) { /* fall through and retry */ }
                }

                if (clear != null) {
                    break;
                }
            }

            if (clear == null) {
                throw new PGPUnknownRecipientException("Message is encrypted for unknown recipient");
            }

            PGPObjectFactory plainFactory = new BcPGPObjectFactory(clear);
            Object nextObject = plainFactory.nextObject();

            PGPCompressedData compressedData = (PGPCompressedData) nextObject;
            PGPObjectFactory uncompressedFactory = new BcPGPObjectFactory(compressedData.getDataStream());

            plainFactory = uncompressedFactory;
            nextObject = uncompressedFactory.nextObject();

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            PGPOnePassSignatureList sigList = (PGPOnePassSignatureList) nextObject;
            PGPOnePassSignature signature = sigList.get(0);

            nextObject = plainFactory.nextObject();
            if (!(nextObject instanceof PGPLiteralData)) {
                throw new PGPDataValidationException("Expected literal data packet");
            }
            PGPLiteralData literalData = (PGPLiteralData) nextObject;
            ByteStreams.copy(literalData.getInputStream(), out);

            PGPSignatureList signatureList = (PGPSignatureList) plainFactory.nextObject();

            signature.init(new BcPGPContentVerifierBuilderProvider(), publicKey.getSigningKey());
            signature.update(out.toByteArray());

            if (signature.verify(signatureList.get(0))) {
                checkIntegrity(encryptedData);

                return new String(out.toByteArray(), StandardCharsets.UTF_8);
            }

            throw new PGPInvalidSignatureException("The signature is not valid");

        } catch (PGPException | RuntimeException | IOException e) {
            throw new PGPDecryptionException("Error Decrypting message", e);
        }
    }

    private static void checkIntegrity(PGPPublicKeyEncryptedData encryptedData)
            throws PGPException, IOException, PGPDecryptionException {
        if (encryptedData.isIntegrityProtected()) {
            if (!encryptedData.verify()) {
                throw new PGPDecryptionException("Message failed integrity check");
            }
        } else {
            throw new PGPDecryptionException("Message not integrity protected");
        }
    }
}
