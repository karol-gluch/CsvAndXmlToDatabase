import org.w3c.dom.Element;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseController {

    public static String sqlCustomer = "INSERT INTO CUSTOMERS (NAME, SURNAME, AGE) VALUES (?, ?, ?)";
    public static String sqlContact = "INSERT INTO CONTACTS (ID_CUSTOMER, TYPE, CONTACT) VALUES (?, ?, ?)";

    public static Connection getConnection() throws Exception {
        Connection connection = null;
        Properties properties = new Properties();
        InputStream inputStream = new FileInputStream("dataConfig.properties");
        properties.load(inputStream);

        try {
            String driver = properties.getProperty("driver");
            String url = properties.getProperty("urlDb");
            String username = properties.getProperty("username");
            String password = properties.getProperty("password");
            Class.forName(driver);

            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected");

            createTable(connection);
            return connection;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public static void createTable(Connection connection) throws Exception {
        Statement statement = connection.createStatement();

        try {
            statement.execute("DROP TABLE IF EXISTS CONTACTS");
            statement.execute("DROP TABLE IF EXISTS CUSTOMERS");

            statement.execute("CREATE TABLE CUSTOMERS (ID INT NOT NULL AUTO_INCREMENT, NAME VARCHAR(255), SURNAME VARCHAR(255), AGE INT, primary key (ID))");
            statement.execute("CREATE TABLE CONTACTS (ID INT NOT NULL AUTO_INCREMENT, ID_CUSTOMER INT, TYPE INT, CONTACT VARCHAR(30), primary key (ID), FOREIGN KEY (ID_CUSTOMER) REFERENCES CUSTOMERS(ID))");

            System.out.println("Table created");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void insertToDatabasePersonCsv(String name, String subname, String age, PreparedStatement statement) throws Exception {
        statement.setString(1, name);
        statement.setString(2, subname);
        if (age.isEmpty())
            statement.setString(3, null);
        else
            statement.setString(3, age);

        statement.executeUpdate();
    }

    public static void insertToDatabasePersonXml(Element element, PreparedStatement statement) throws Exception {
        if (element.getElementsByTagName("name").item(0) == null)
            statement.setString(1, null);
        else
            statement.setString(1, element.getElementsByTagName("name").item(0).getTextContent());


        if (element.getElementsByTagName("surname").item(0) == null)
            statement.setString(2, null);
        else
            statement.setString(2, element.getElementsByTagName("surname").item(0).getTextContent());


        if (element.getElementsByTagName("age").item(0) == null)
            statement.setString(3, null);
        else
            statement.setString(3, element.getElementsByTagName("age").item(0).getTextContent());


        statement.executeUpdate();
    }


    public static void insertToDatabaseContacts(int id, int type, String value, PreparedStatement statement) throws Exception {
        statement.setInt(1, id);
        statement.setInt(2, type);
        statement.setString(3, value);
        statement.executeUpdate();
    }

}
