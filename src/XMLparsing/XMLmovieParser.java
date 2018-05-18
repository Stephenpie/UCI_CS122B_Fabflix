package XMLparsing;

import java.io.IOException;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLmovieParser extends DefaultHandler {

    LinkedList<Movie> movies;

    private String tempVal;

    //to maintain context
    private Movie tempMovie;
    
    private String director;
    
    private HashSet<String> genres;
    private HashSet<String> movieIDs;

    public XMLmovieParser() {
        movies = new LinkedList<Movie>();
        genres = new HashSet<>();
        movieIDs = new HashSet<>();
    }

    public void runParser() {
        parseDocument();
//        printData();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("mains243.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Iterate through the list and print
     * the contents
     */
    private void printData() {

        System.out.println("No of Movies '" + movies.size() + "'.");

        Iterator<Movie> it = movies.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("film")) {
            //create a new instance of employee
            tempMovie = new Movie();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
    	tempVal = tempVal.trim();
        if (qName.equalsIgnoreCase("film")) {
            //add it to the list
            tempMovie.setDirector(director);
            movies.add(tempMovie);

        } else if (qName.equalsIgnoreCase("t")) { //title
            tempMovie.setTitle(tempVal.trim());
        } else if (qName.equalsIgnoreCase("year")) { //year
            if (isNumeric(tempVal.trim())) {
                tempMovie.setYear(Integer.parseInt(tempVal.trim()));
            } else {
            	System.out.println("inconsistent movie year:" + tempVal);
            }
        } else if (qName.equalsIgnoreCase("dirname")) { //director
//            tempMovie.setDirector(tempVal);
            director = tempVal.trim();
        } else if (qName.equalsIgnoreCase("cat")) { //genre
//        	System.out.println(tempVal);
        	if (tempVal.length() == 0) {
        		System.out.println("missing genre, genre set to null");
        		tempVal = "Null";
        	} else if (tempVal.length() != 0 && !Character.isLetterOrDigit(tempVal.charAt(tempVal.length() - 1))) {
        		tempVal = tempVal.substring(0, tempVal.length() - 2);
        	}
            tempMovie.setGenre(tempVal.trim());
            genres.add(tempVal.trim());
        } else if (qName.equalsIgnoreCase("fid")) {
        	movieIDs.add(tempVal.trim());
        }

    }
    
    public boolean isNumeric(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch(Exception e) {
            return false;
        }
    }
    
    public LinkedList<Movie> getMovies() {
        return movies;
    }
    
    public HashSet<String> getUniqueGenres() {
    	return genres;
    }
    
    public HashSet<String> getMovieIDs() {
    	return movieIDs;
    }

    public static void main(String[] args) {
        XMLmovieParser spe = new XMLmovieParser();
        spe.runParser();
    }
}