import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseController {

    public static Connection getConnection() throws Exception {
        Connection conn = null;
        try {
            String driver = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://localhost/aaa?useUnicode=true&characterEncoding=utf8";
            String username = "root";
            String password = "";
            Class.forName(driver);

            conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connected");

            createTable(conn);
            return conn;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public static void createTable(Connection conn) throws Exception {
        Statement stat = conn.createStatement();

        try {
            stat.execute("DROP TABLE IF EXISTS CONTACTS");
            stat.execute("DROP TABLE IF EXISTS CUSTOMERS");

            stat.execute("CREATE TABLE CUSTOMERS (ID INT NOT NULL AUTO_INCREMENT, NAME VARCHAR(255), SURNAME VARCHAR(255), AGE INT, primary key (ID))");
            stat.execute("CREATE TABLE CONTACTS (ID INT NOT NULL AUTO_INCREMENT, ID_CUSTOMER INT, TYPE INT, CONTACT VARCHAR(30), primary key (ID), FOREIGN KEY (ID_CUSTOMER) REFERENCES CUSTOMERS(ID))");

            System.out.println("Table created");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
