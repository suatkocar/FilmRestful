package util;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

import models.Film;

/*
 * Utility class for converting objects to and from YAML format using the SnakeYAML library.
 * Implements the Singleton pattern to provide a single globally accessible instance.
 */
public class YamlConverter {
    private static YamlConverter instance;

 // Private constructor ensures that this class cannot be instantiated from outside.
    private YamlConverter() {}

    /*
     * Provides a thread-safe way to access the singleton instance of YamlConverter.
     * @return The single instance of YamlConverter.
     */
    public static synchronized YamlConverter getInstance() {
        if (instance == null) {
            instance = new YamlConverter();
        }
        return instance;
    }
    
    /*
     * Converts a Java object to its YAML string representation.
     * @param object The object to be converted to YAML.
     * @return The YAML string representation of the object.
     */
    public String convertToYaml(Object object) {
        try {
            Yaml yaml = new Yaml();
            return yaml.dump(object);
        } catch (YAMLException e) {
            System.err.println("YAML conversion error: " + e.getMessage());
            return null;
        }
    }

    /*
     * Converts a YAML string to an instance of Film.
     * @param yamlStr The YAML string to be converted to a Film object.
     * @return A Film object, or null if the conversion fails.
     */
    public Film convertFromYaml(String yamlStr) {
        try {
            Yaml yaml = new Yaml(new Constructor(Film.class, null));
            return yaml.load(yamlStr);
        } catch (YAMLException e) {
            System.err.println("Error loading YAML: " + e.getMessage());
            return null;
        }
    }
}