package XMLparsing;

import java.util.LinkedList;

public class Movie {
    private String id;
    private String title;
    private int year;
    private String director;
    private LinkedList<String> genres;
    
    public Movie() {
        genres = new LinkedList<>();
    }
    
    public Movie(String title, int year, String director) {
        this.title = title;
        this.year = year;
        this.director = director;
        genres = new LinkedList<>();
    }
    
    public String getID() {
        return id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public int getYear() {
        return year;
    }
    
    public String getDirector() {
        return director;
    }
    
    public LinkedList<String> getGenres() {
        return genres;
    }
    
    public void setID(String id) {
        this.id = id;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setYear(int year) {
        this.year = year;
    }
    
    public void setDirector(String director) {
        this.director = director;
    }
    
    public void setGenre(String genre) {
        genres.add(genre);
    }
    
    public String toString() {
        String result = String.format("Movie(%s, %d, %s)\n", title, year, director);
        for (String g: genres) {
            result += " -- " + g;
        }
        return result;
    }
}
