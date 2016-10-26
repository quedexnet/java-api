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

public class Config
{
    private final String marketStreamUrl;
    private final String marketDataUrl;
    private final String userStreamUrl;
    private final BcPublicKey qdxPublicKey;
    private final BcPrivateKey userPrivateKey;
    private final long accountId;
    private final int nonceGroup;

    public Config(
        final String marketStreamUrl,
        final String marketDataUrl,
        final String userStreamUrl,
        final BcPublicKey qdxPublicKey,
        final BcPrivateKey userPrivateKey,
        final long accountId,
        final int nonceGroup)
    {
        checkArgument(!marketStreamUrl.isEmpty(), "Empty marketStreamUrl");
        checkArgument(!marketDataUrl.isEmpty(), "Empty marketDataUrl");
        checkArgument(!userStreamUrl.isEmpty(), "Empty userStreamUrl");
        checkArgument(accountId > 0, "accountId=%s <= 0", accountId);
        checkArgument(nonceGroup >= 0, "accountId=%s < 0", nonceGroup);
        this.marketStreamUrl = marketStreamUrl + "?keepalive=true";
        this.marketDataUrl = marketDataUrl;
        this.userStreamUrl = userStreamUrl + "?keepalive=true";
        this.qdxPublicKey = checkNotNull(qdxPublicKey, "null qdxPublicKey");
        this.userPrivateKey = checkNotNull(userPrivateKey, "null userPrivateKey");
        this.accountId = accountId;
        this.nonceGroup = nonceGroup;
    }

    public static Config fromResource(final String resourceName, final char[] prvKeyPasspharse)
    {
        try
        {
            return fromInputStream(Resources.getResource(resourceName).openStream(), prvKeyPasspharse);
        }
        catch (final IOException e)
        {
            throw new IllegalArgumentException("Error reading resource=" + resourceName, e);
        }
    }

    public static Config fromResource(final char[] prvKeyPasspharse)
    {
        return fromResource("qdxConfig.properties", prvKeyPasspharse);
    }

    public static Config fromInputStream(final InputStream inputStream, final char[] prvKeyPasspharse)
    {
        final Properties props = new Properties();
        try
        {
            props.load(inputStream);
        }
        catch (final IOException e)
        {
            throw new IllegalStateException("Error reading properties file", e);
        }

        try
        {
            return new Config(
                props.getProperty("net.quedex.client.api.marketStreamUrl"),
                props.getProperty("net.quedex.client.api.marketDataUrl"),
                props.getProperty("net.quedex.client.api.userStreamUrl"),
                BcPublicKey.fromArmored(props.getProperty("net.quedex.client.api.qdxPublicKey")),
                BcPrivateKey.fromArmored(props.getProperty("net.quedex.client.api.userPrivateKey"), prvKeyPasspharse),
                Long.parseLong(props.getProperty("net.quedex.client.api.accountId")),
                Integer.parseInt(props.getProperty("net.quedex.client.api.nonceGroup"))
            );
        }
        catch (final PGPKeyInitialisationException e)
        {
            throw new IllegalArgumentException("Error instantiating keys", e);
        }
    }

    public String getMarketStreamUrl()
    {
        return marketStreamUrl;
    }

    public String getInstrumentDataUrl()
    {
        return marketDataUrl + "/instrument_data";
    }

    public String getMarketDataUrl()
    {
        return marketDataUrl;
    }

    public String getUserStreamUrl()
    {
        return userStreamUrl;
    }

    public BcPublicKey getQdxPublicKey()
    {
        return qdxPublicKey;
    }

    public BcPrivateKey getUserPrivateKey()
    {
        return userPrivateKey;
    }

    public long getAccountId()
    {
        return accountId;
    }

    public int getNonceGroup()
    {
        return nonceGroup;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        final Config config = (Config) o;
        return accountId == config.accountId &&
            nonceGroup == config.nonceGroup &&
            Objects.equal(marketStreamUrl, config.marketStreamUrl) &&
            Objects.equal(marketDataUrl, config.marketDataUrl) &&
            Objects.equal(userStreamUrl, config.userStreamUrl) &&
            Objects.equal(qdxPublicKey, config.qdxPublicKey) &&
            Objects.equal(userPrivateKey, config.userPrivateKey);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(marketStreamUrl, marketDataUrl, userStreamUrl, qdxPublicKey, userPrivateKey, accountId, nonceGroup);
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
            .add("marketStreamUrl", marketStreamUrl)
            .add("marketDataUrl", marketDataUrl)
            .add("userStreamUrl", userStreamUrl)
            .add("qdxPublicKey", qdxPublicKey)
            .add("userPrivateKey", userPrivateKey)
            .add("accountId", accountId)
            .add("nonceGroup", nonceGroup)
            .toString();
    }
}
