package models;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/*
 * Represents the Film entity model with annotations for XML binding.
 */
@XmlRootElement(name = "film")
public class Film {
	private int id;
	private String title;
	private int year;
	private String director;
	private String stars;
	private String review;

    /*
     * Default constructor required for JAXB.
     */
	public Film() {
	}

    /*
     * Constructs a Film with specified details.
     */
	public Film(int id, String title, int year, String director, String stars, String review) {
		this.id = id;
		this.title = title;
		this.year = year;
		this.director = director;
		this.stars = stars;
		this.review = review;
	}
	
    /*
     * Gets the ID of the film.
     * This property is marked with @XmlElement to include it in XML representation.
     */
	@XmlElement
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

    /*
     * Gets the title of the film.
     * This property is marked with @XmlElement to include it in XML representation.
     */
	@XmlElement
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

    /*
     * Gets the year the film.
     * This property is marked with @XmlElement to include it in XML representation.
     */
	@XmlElement
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

    /*
     * Gets the name of the director of the film.
     * This property is marked with @XmlElement to include it in XML representation.
     */
	@XmlElement
	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

    /*
     * Gets the stars of the film.
     * This property is marked with @XmlElement to include it in XML representation.
     */
	@XmlElement
	public String getStars() {
		return stars;
	}

	public void setStars(String stars) {
		this.stars = stars;
	}

    /*
     * Gets the review of the film.
     * This property is marked with @XmlElement to include it in XML representation.
     */
	@XmlElement
	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}
}