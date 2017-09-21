/**
 *
 */
package fr.cnes.doi;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import fr.cnes.doi.client.ClientProxyTest;
import fr.cnes.doi.security.UtilsCryptography;
import fr.cnes.doi.settings.DoiSettings;

/**
 * Class to read the settings from the crypted config file and to enable the
 * proxy if the system property has been sets
 *
 * @author Claire
 *
 */
public class InitSettingsForTest {  

    /**
     * Init loggers
     */
    private static final Logger LOGGER = Logger.getLogger(InitSettingsForTest.class.getName());

    /**
     * Init
     *
     */
    public static void init() {
        try {
            String secretKey = System.getProperty("private.key");
            if (secretKey != null) {                
                String result;
                try (InputStream inputStream = ClientProxyTest.class.getResourceAsStream("/config.properties")) {
                    result = new BufferedReader(new InputStreamReader(inputStream)).lines()
                            .collect(Collectors.joining("\n"));
                }
                result = UtilsCryptography.decrypt(result, secretKey);
                // Replace the value to use the proxy by the system property
                String useProxy = System.getProperty("proxy.use");                
                if (useProxy != null) {
                    result = result.replace("Starter.Proxy.used = false", "Starter.Proxy.used=" + useProxy);
                } else {
                    LOGGER.log(Level.INFO, "The key proxy.use is not set, default param applied");
                }
                // Replace the value to use pre prod context
                result = result.replace("Starter.CONTEXT_MODE=DEV", "Starter.CONTEXT_MODE=PRE_PROD");
                InputStream stream = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));
                DoiSettings.getInstance().setPropertiesFile(stream);
            } else {
                LOGGER.log(Level.SEVERE, "The property private.key must be set to decrypt the config file");

            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during initialisation of the settings", e);
        }

    }

}
