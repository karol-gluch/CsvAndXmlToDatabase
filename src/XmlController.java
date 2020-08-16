import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


public class XmlController {

    public static void readDataFromXml() throws Exception {

        String pathXml = ConfigurationApp.getFilePathConfiguration(); //path to file

        try {
            File inputFile = new File(pathXml);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(inputFile);
            document.getDocumentElement().normalize();

            Connection connection = DatabaseController.getConnection();

            PreparedStatement statementPerson = connection.prepareStatement(DatabaseController.sqlCustomer, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement statementContact = connection.prepareStatement(DatabaseController.sqlContact);

            NodeList nodeList = document.getElementsByTagName("person");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    //insert name,surname and age
                    DatabaseController.insertToDatabasePersonXml(element, statementPerson);

                    int id_customer = findCustomerID(statementPerson);

                    for (int j = 0; j < element.getElementsByTagName("email").getLength(); j++) {
                        DatabaseController.insertToDatabaseContacts(id_customer, 1, element.getElementsByTagName("email").item(j).getTextContent(), statementContact);
                    }
                    for (int j = 0; j < element.getElementsByTagName("phone").getLength(); j++) {
                        DatabaseController.insertToDatabaseContacts(id_customer, 2, element.getElementsByTagName("phone").item(j).getTextContent(), statementContact);
                    }
                    for (int j = 0; j < element.getElementsByTagName("jabber").getLength(); j++) {
                        DatabaseController.insertToDatabaseContacts(id_customer, 3, element.getElementsByTagName("jabber").item(j).getTextContent(), statementContact);
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
                                        DatabaseController.insertToDatabaseContacts(id_customer, 0, elementContact.getTextContent(), statementContact);
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

    public static int findCustomerID(PreparedStatement statement) throws Exception{
        ResultSet resultSets = statement.getGeneratedKeys();
        int id_customer = 0;
        if (resultSets.next()) {
            id_customer = resultSets.getInt(1);
        }
        return id_customer;
    }
}
