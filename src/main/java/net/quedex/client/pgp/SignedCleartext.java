package net.quedex.client.pgp;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Signed cleartext - result of decryption and signature verification.
 */
public final class SignedCleartext {

    private final String message;
    private final String signerKeyFingerprint;

    public SignedCleartext(String message, String signerKeyFingerprint) {
        checkArgument(!signerKeyFingerprint.isEmpty(), "Empty signerKeyFingerprint");
        this.message = checkNotNull(message, "null message");
        this.signerKeyFingerprint = signerKeyFingerprint;
    }
    public String getMessage() {
        return message;
    }

    public String getSignerKeyFingerprint() {
        return signerKeyFingerprint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignedCleartext that = (SignedCleartext) o;
        return Objects.equal(message, that.message) &&
                Objects.equal(signerKeyFingerprint, that.signerKeyFingerprint);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(message, signerKeyFingerprint);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("message", message)
                .add("signerKeyFingerprint", signerKeyFingerprint)
                .toString();
    }
}
