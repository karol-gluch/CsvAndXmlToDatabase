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

    public static void insertToDatabasePersonXml(Element eElement, PreparedStatement statement) throws Exception {
        if (eElement.getElementsByTagName("name").item(0) == null)
            statement.setString(1, null);
        else
            statement.setString(1, eElement.getElementsByTagName("name").item(0).getTextContent());


        if (eElement.getElementsByTagName("surname").item(0) == null)
            statement.setString(2, null);
        else
            statement.setString(2, eElement.getElementsByTagName("surname").item(0).getTextContent());


        if (eElement.getElementsByTagName("age").item(0) == null)
            statement.setString(3, null);
        else
            statement.setString(3, eElement.getElementsByTagName("age").item(0).getTextContent());


        statement.executeUpdate();
    }

    public static void insertToDatabaseContactsXml(int id, int type, String value, PreparedStatement statement) throws Exception {
        statement.setInt(1, id);
        statement.setInt(2, type);
        statement.setString(3, value);
        statement.executeUpdate();
    }

    public static void readDataFromXml() throws Exception {

        String pathXml = ConfigurationApp.filePathConfiguration(); //path to file

        try {
            File inputFile = new File(pathXml);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            String sqlCustomer = "INSERT INTO CUSTOMERS (NAME, SURNAME, AGE) VALUES (?, ?, ?)";
            String sqlContact = "INSERT INTO CONTACTS (ID_CUSTOMER, TYPE, CONTACT) VALUES (?, ?, ?)";
            Connection con = DatabaseController.getConnection();

            PreparedStatement statement = con.prepareStatement(sqlCustomer, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement statementC = con.prepareStatement(sqlContact);

            NodeList nList = doc.getElementsByTagName("person");

            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    //insert name,surname and age
                    insertToDatabasePersonXml(eElement, statement);

                    int id_customer = findCustomerID(statement);


                    for (int j = 0; j < eElement.getElementsByTagName("email").getLength(); j++) {
                        insertToDatabaseContactsXml(id_customer, 1, eElement.getElementsByTagName("email").item(j).getTextContent(), statementC);
                    }
                    for (int j = 0; j < eElement.getElementsByTagName("phone").getLength(); j++) {
                        insertToDatabaseContactsXml(id_customer, 2, eElement.getElementsByTagName("phone").item(j).getTextContent(), statementC);
                    }
                    for (int j = 0; j < eElement.getElementsByTagName("jabber").getLength(); j++) {
                        insertToDatabaseContactsXml(id_customer, 3, eElement.getElementsByTagName("jabber").item(j).getTextContent(), statementC);
                    }

                    NodeList cList = eElement.getChildNodes();
                    for (int j = 0; j < cList.getLength(); j++) {
                        Node cNode = cList.item(j);
                        if (cNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element cElement = (Element) cNode;
                            NodeList otherList = cElement.getChildNodes();
                            for (int k = 0; k < otherList.getLength(); k++) {
                                Node otherNode = otherList.item(k);
                                if (otherNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element oElement = (Element) otherNode;
                                    if (!(oElement.getTagName().equals("phone") || oElement.getTagName().equals("email") || oElement.getTagName().equals("jabber")))
                                        insertToDatabaseContactsXml(id_customer, 0, oElement.getTextContent(), statementC);

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
        ResultSet rs = statement.getGeneratedKeys();
        int id_customer = 0;
        if (rs.next()) {
            id_customer = rs.getInt(1);
        }
        return id_customer;
    }
}
