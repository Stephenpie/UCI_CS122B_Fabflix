package XMLparsing;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLmoviestarParser extends DefaultHandler {

    HashMap<String, LinkedList<String>> movies;

    private String tempVal;

    //to maintain context
    private String title;
    private LinkedList<String> stars;

    public XMLmoviestarParser() {
        movies = new HashMap<String, LinkedList<String>>();
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

        System.out.println("No of Movies '" + movies.size() + "'.");

        for (String s:movies.keySet()) {
            System.out.println(s);
            for (String star : movies.get(s)) {
                System.out.println(" --" + star);
            }
        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("filmc")) {
            stars = new LinkedList<>();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("filmc")) {
            movies.put(title, stars);
        } else if (qName.equalsIgnoreCase("t")) {
            title = tempVal;
        } else if (qName.equalsIgnoreCase("a")) {
            stars.add(tempVal);
        }

    }
    
    public HashMap<String, LinkedList<String>> getMoviesStars() {
        return movies;
    }

    public static void main(String[] args) {
        XMLmoviestarParser spe = new XMLmoviestarParser();
        spe.runParser();
    }
}