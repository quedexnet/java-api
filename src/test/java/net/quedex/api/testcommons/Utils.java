package net.quedex.api.testcommons;

import com.google.common.io.Resources;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Properties;

public class Utils {
    private Utils() {}

    public static char[] getKeyPassphraseFromProps() {
        Properties props = new Properties();
        try {
            props.load(Resources.getResource("qdxConfig.properties").openStream());
        } catch (IOException e) {
            throw new IllegalStateException("Error reading properties file", e);
        }

        return props.getProperty("dont.do.it.in.production.privateKeyPasspharse").toCharArray();
    }

    public static BigDecimal $(String price) {
        return new BigDecimal(price);
    }

    public static BigDecimal $(int price) {
        return BigDecimal.valueOf(price);
    }
}
