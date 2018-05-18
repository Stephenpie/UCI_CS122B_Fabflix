package XMLparsing;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLmoviestarParser extends DefaultHandler {

//    HashMap<String, LinkedList<String>> movies;
//	HashMap<String, HashMap<String, LinkedList<String>>> movies;
	HashMap<String, Movie> movies;

    private String tempVal;

    //to maintain context
    private String title;
    private String mid;
    private String director;
    private LinkedList<String> stars;
    private HashSet<String> movieIDs;
    private Movie movie;

    public XMLmoviestarParser(HashSet<String> movieIDs) {
    	this.movieIDs = movieIDs;
        movies = new HashMap<>();
    }

    public void runParser() {
        parseDocument();
        printData();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("casts124.xml", this);

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

        for (String s:movies.keySet()) {
            System.out.println(s + ", " + movies.get(s));
        }
        System.out.println("No of Movies '" + movies.size() + "'.");
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("filmc")) {
        	movie = new Movie();
//            stars = new LinkedList<>();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("filmc")) {
        	
        } else if (qName.equalsIgnoreCase("t")) { // title
        	movie.setTitle(tempVal.trim());
        	movie.setDirector(director);
        } else if (qName.equalsIgnoreCase("a")) { // star
        	movies.put(tempVal, movie);
        } else if (qName.equalsIgnoreCase("is")) { //director
        	director = tempVal.trim();
        } else if (qName.equalsIgnoreCase("f")) {
        	movie.setID(tempVal.trim());
        }
    }
    
    public HashMap<String, Movie> getMoviesStars() {
        return movies;
    }

    public static void main(String[] args) {
        XMLmoviestarParser spe = new XMLmoviestarParser(new HashSet<>());
        spe.runParser();
    }
}