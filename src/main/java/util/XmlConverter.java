package util;

import java.io.StringReader;
import java.io.StringWriter;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

/*
 * Utility class that handles XML conversions using JAXB for marshall (object to XML)
 * and unmarshall (XML to object) operations.
 * This class uses a singleton pattern to ensure only one instance is used throughout the application.
 */
public class XmlConverter {
    private static XmlConverter instance;
    
 // Private constructor to prevent external instantiation.
    private XmlConverter() {}

    /*
     * Provides a synchronised, thread-safe method to obtain a singleton instance of the XmlConverter.
     * @return The single instance of XmlConverter.
     */
    public static synchronized XmlConverter getInstance() {
        if (instance == null) {
            instance = new XmlConverter();
        }
        return instance;
    }

    /*
     * Converts a Java object to its XML representation.
     * @param object The object to convert to XML.
     * @return A string containing the XML representation of the object.
     * If an error occurs during conversion, it returns an XML formatted error message.
     */
    public String convertToXml(Object object) {
        try {
            JAXBContext context = JAXBContext.newInstance(object.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter writer = new StringWriter();
            marshaller.marshal(object, writer);
            String xmlOutput = writer.toString();
            System.out.println(xmlOutput);
            return xmlOutput;
        } catch (JAXBException e) {
            System.err.println("JAXB conversion error: " + e.getMessage());
            return "<response>Error occurred: " + e.getMessage() + "</response>";
        }
    }

    /*
     * Converts an XML string back into an object of the specified class type.
     * @param xml The XML string to convert.
     * @param clazz The class of the object to which the XML should be converted.
     * @return The object converted from XML.
     * @throws RuntimeException if there is an error during the unmarshalling process.
     */
    public <T> T convertFromXml(String xml, Class<T> clazz) {
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return clazz.cast(unmarshaller.unmarshal(new StringReader(xml)));
        } catch (JAXBException e) {
            System.err.println("JAXB unmarshalling error: " + e.getMessage());
            throw new RuntimeException("Error processing XML data", e);
        }
    }
}