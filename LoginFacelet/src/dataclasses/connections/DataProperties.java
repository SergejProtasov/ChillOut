package dataclasses.connections;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DataProperties {
    public static String getProp(String nameProp) {
        InputStream fis;
        Properties property = new Properties();

        try {
            fis = DataProperties.class.getResourceAsStream("config.properties");
            property.load(fis);

            return property.getProperty(nameProp);
        } catch (IOException e) {
           e.printStackTrace();
        }
        return null;
    }
}
