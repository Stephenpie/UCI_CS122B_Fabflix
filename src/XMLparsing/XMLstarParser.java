package XMLparsing;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLstarParser extends DefaultHandler {

	HashSet<Star> stars;

    private String tempVal;

    //to maintain context
    private Star tempEmp;

    public XMLstarParser() {
        stars = new HashSet<Star>();
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
            sp.parse("actors63.xml", this);

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

        Iterator<Star> it = stars.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
        System.out.println("No of Stars '" + stars.size() + "'.");
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("actor")) {
            //create a new instance of employee
            tempEmp = new Star();
//            tempEmp.setType(attributes.getValue("type"));
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("actor")) {
            //add it to the list
            stars.add(tempEmp);

        } else if (qName.equalsIgnoreCase("stagename")) {
            tempEmp.setName(tempVal.trim());
        } else if (qName.equalsIgnoreCase("dob")) {
            if (isNumeric(tempVal)) {
                tempEmp.setBirthYear(Integer.parseInt(tempVal.trim()));
            } else {
            	System.out.println("inconsitent star birthYear:" + tempVal);
            }
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
    
    public HashSet<Star> getStars() {
    	return stars;
    }

    public static void main(String[] args) {
        XMLstarParser spe = new XMLstarParser();
        spe.runParser();
        spe.printData();
    }
}