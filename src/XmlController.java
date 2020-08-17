import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class XmlController {

    public static void readDataFromXml() throws Exception {

        String pathXml = ConfigurationApp.getFilePathConfiguration(); //path to file

        try {
            File inputFile = new File(pathXml);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(inputFile);
            document.getDocumentElement().normalize();

            DatabaseController dbController = new DatabaseController();

            NodeList nodeList = document.getElementsByTagName("person");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    //insert name,surname and age

                    int id_customer = dbController.insertToDatabasePersonXml(element);

                    for (int j = 0; j < element.getElementsByTagName("email").getLength(); j++) {
                        dbController.insertToDatabaseContacts(id_customer, 1, element.getElementsByTagName("email").item(j).getTextContent());
                    }
                    for (int j = 0; j < element.getElementsByTagName("phone").getLength(); j++) {
                        dbController.insertToDatabaseContacts(id_customer, 2, element.getElementsByTagName("phone").item(j).getTextContent());
                    }
                    for (int j = 0; j < element.getElementsByTagName("jabber").getLength(); j++) {
                        dbController.insertToDatabaseContacts(id_customer, 3, element.getElementsByTagName("jabber").item(j).getTextContent());
                    }

                    NodeList nodeListPerson = element.getChildNodes();
                    for (int j = 0; j < nodeListPerson.getLength(); j++) {
                        Node nodePerson = nodeListPerson.item(j);
                        if (nodePerson.getNodeType() == Node.ELEMENT_NODE) {
                            Element elementContacts = (Element) nodePerson;
                            NodeList nodeListContacts = elementContacts.getChildNodes();
                            for (int k = 0; k < nodeListContacts.getLength(); k++) {
                                Node nodeTypeOfContact = nodeListContacts.item(k);
                                if (nodeTypeOfContact.getNodeType() == Node.ELEMENT_NODE) {
                                    Element elementContact = (Element) nodeTypeOfContact;
                                    if (!(elementContact.getTagName().equals("phone") || elementContact.getTagName().equals("email") || elementContact.getTagName().equals("jabber")))
                                        dbController.insertToDatabaseContacts(id_customer, 0, elementContact.getTextContent());
                                }
                            }
                        }
                    }
                }
            }
            System.out.println("XML data inserted to database");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}