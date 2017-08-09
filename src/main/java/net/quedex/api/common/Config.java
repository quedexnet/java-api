package net.quedex.api.common;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.io.Resources;
import net.quedex.api.pgp.BcPrivateKey;
import net.quedex.api.pgp.BcPublicKey;
import net.quedex.api.pgp.PGPKeyInitialisationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class Config {

    private final String marketStreamUrl;
    private final String userStreamUrl;
    private final BcPublicKey qdxPublicKey;
    private final BcPrivateKey userPrivateKey;
    private final long accountId;
    private final int nonceGroup;

    /**
     * @param nonceGroup value between 0 and 9, has to be different for every WebSocket connection opened to the
     *                   exchange (e.g. browser and trading bot); our webapp uses nonce_group=0
     */
    public Config(String marketStreamUrl,
                  String userStreamUrl,
                  BcPublicKey qdxPublicKey,
                  BcPrivateKey userPrivateKey,
                  long accountId,
                  int nonceGroup) {
        checkArgument(!marketStreamUrl.isEmpty(), "Empty marketStreamUrl");
        checkArgument(!userStreamUrl.isEmpty(), "Empty userStreamUrl");
        checkArgument(accountId > 0, "accountId=%s <= 0", accountId);
        checkArgument(nonceGroup >= 0, "accountId=%s < 0", nonceGroup);
        this.marketStreamUrl = marketStreamUrl + "?keepalive=true";
        this.userStreamUrl = userStreamUrl + "?keepalive=true";
        this.qdxPublicKey = checkNotNull(qdxPublicKey, "null qdxPublicKey");
        this.userPrivateKey = checkNotNull(userPrivateKey, "null userPrivateKey");
        this.accountId = accountId;
        this.nonceGroup = nonceGroup;
    }

    public static Config fromResource(String resourceName, char[] prvKeyPasspharse) {
        try {
            return fromInputStream(Resources.getResource(resourceName).openStream(), prvKeyPasspharse);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading resource=" + resourceName, e);
        }
    }

    public static Config fromResource(char[] prvKeyPasspharse) {
        return fromResource("qdxConfig.properties", prvKeyPasspharse);
    }

    public static Config fromInputStream(InputStream inputStream, char[] prvKeyPasspharse) {
        Properties props = new Properties();
        try {
            props.load(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException("Error reading properties file", e);
        }

        try {
            return new Config(
                props.getProperty("net.quedex.client.api.marketStreamUrl"),
                props.getProperty("net.quedex.client.api.userStreamUrl"),
                BcPublicKey.fromArmored(props.getProperty("net.quedex.client.api.qdxPublicKey")),
                BcPrivateKey.fromArmored(props.getProperty("net.quedex.client.api.userPrivateKey"), prvKeyPasspharse),
                Long.parseLong(props.getProperty("net.quedex.client.api.accountId")),
                Integer.parseInt(props.getProperty("net.quedex.client.api.nonceGroup"))
            );
        } catch (PGPKeyInitialisationException e) {
            throw new IllegalArgumentException("Error instantiating keys", e);
        }
    }

    public String getMarketStreamUrl() {
        return marketStreamUrl;
    }

    public String getUserStreamUrl() {
        return userStreamUrl;
    }

    public BcPublicKey getQdxPublicKey() {
        return qdxPublicKey;
    }

    public BcPrivateKey getUserPrivateKey() {
        return userPrivateKey;
    }

    public long getAccountId() {
        return accountId;
    }

    public int getNonceGroup() {
        return nonceGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Config config = (Config) o;
        return accountId == config.accountId &&
            nonceGroup == config.nonceGroup &&
            Objects.equal(marketStreamUrl, config.marketStreamUrl) &&
            Objects.equal(userStreamUrl, config.userStreamUrl) &&
            Objects.equal(qdxPublicKey, config.qdxPublicKey) &&
            Objects.equal(userPrivateKey, config.userPrivateKey);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(
            marketStreamUrl,
            userStreamUrl,
            qdxPublicKey,
            userPrivateKey,
            accountId,
            nonceGroup
        );
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("marketStreamUrl", marketStreamUrl)
            .add("userStreamUrl", userStreamUrl)
            .add("qdxPublicKey", qdxPublicKey)
            .add("userPrivateKey", userPrivateKey)
            .add("accountId", accountId)
            .add("nonceGroup", nonceGroup)
            .toString();
    }
}
