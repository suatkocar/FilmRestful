package models;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/*
 * Represents a collection of Film objects with annotations for XML binding.
 * Utilised primarily for generating XML outputs that contain multiple Film instances.
 */
@XmlRootElement(name = "films")
public class Films {
    private List<Film> films;

    /*
     * Default constructor which is necessary for JAXB serialisation and deserialisation processes.
     */
    public Films() {}

    /*
     * Constructs a Films object with a pre-defined list of Film objects.
     */
    public Films(List<Film> films) {
        this.films = films;
    }

    /*
     * Returns the list of films.
     * Each film in the list is annotated with @XmlElement(name = "film"),
     * mapping the Film elements under the <film> tag in the generated XML.
     */
    @XmlElement(name = "film")
    public List<Film> getFilms() {
        return films;
    }

    /*
     * Sets the list of films to the provided list.
     * This method allows modification of the list of films contained within the Films object.
     */
    public void setFilms(List<Film> films) {
        this.films = films;
    }
}