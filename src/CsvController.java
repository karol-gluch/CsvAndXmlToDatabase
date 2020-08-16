import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.util.regex.Pattern;

public class CsvController {

    public static void insertToDatabasePersonCsv(String name, String subname, String age, PreparedStatement statement) throws Exception {
        statement.setString(1, name);
        statement.setString(2, subname);
        if (age.isEmpty())
            statement.setString(3, null);
        else
            statement.setString(3, age);

        statement.executeUpdate();
    }

    public static void insertToDatabaseContactsCsv(int id, int type, String value, PreparedStatement statement) throws Exception {
        statement.setInt(1, id);
        statement.setInt(2, type);
        statement.setString(3, value);
        statement.executeUpdate();
    }

    public static void readDataFromCsv() throws Exception {
        String pathCsv = ConfigurationApp.filePathConfiguration();

        String sqlCustomer = "INSERT INTO CUSTOMERS (NAME, SURNAME, AGE) VALUES (?, ?, ?)";
        String sqlContact = "INSERT INTO CONTACTS (ID_CUSTOMER, TYPE, CONTACT) VALUES (?, ?, ?)";
        String values[] = new String[0];
        BufferedReader csvReader = new BufferedReader(new FileReader(pathCsv));

        Connection con = DatabaseController.getConnection();

        PreparedStatement statement = con.prepareStatement(sqlCustomer, Statement.RETURN_GENERATED_KEYS);
        PreparedStatement statementC = con.prepareStatement(sqlContact);

        final String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);

        try {
            String row;
            while ((row = csvReader.readLine()) != null) {
                values = row.split(",");

                //insert name,surname and age
                insertToDatabasePersonCsv(values[0], values[1], values[2], statement);

                int id_customer = findCustomerID(statement);

                for (int i = 4; i < values.length; i++) {
                    if (values[i] != null && values[i].length() > 1) {
                        if ((values[i].replaceAll("[^0-9]", "")).length() == 9) {     //phone number
                            insertToDatabaseContactsCsv(id_customer, 2, values[i], statementC);
                        } else if (values[i].contains("@jabber") || values[i].contains("jbr")) {        //jabber
                            insertToDatabaseContactsCsv(id_customer, 3, values[i], statementC);
                        } else if (pattern.matcher(values[i]).matches()) {                              //email
                            insertToDatabaseContactsCsv(id_customer, 1, values[i], statementC);
                        } else {                                                                        //others
                            insertToDatabaseContactsCsv(id_customer, 0, values[i], statementC);
                        }
                    }
                }
            }
            csvReader.close();
            System.out.println("CSV data inserted to database");
        } catch (SQLException ex) {
            ex.printStackTrace();
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
