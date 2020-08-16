import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.util.regex.Pattern;

public class CsvController {



    public static void readDataFromCsv() throws Exception {
        String pathCsv = ConfigurationApp.getFilePathConfiguration();

        String values[] = new String[0];
        BufferedReader csvReader = new BufferedReader(new FileReader(pathCsv));

        Connection connection = DatabaseController.getConnection();

        PreparedStatement statementPerson = connection.prepareStatement(DatabaseController.sqlCustomer, Statement.RETURN_GENERATED_KEYS);
        PreparedStatement statementContact = connection.prepareStatement(DatabaseController.sqlContact);

        final String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);

        try {
            String row;
            while ((row = csvReader.readLine()) != null) {
                values = row.split(",");

                //insert name,surname and age
                DatabaseController.insertToDatabasePersonCsv(values[0], values[1], values[2], statementPerson);

                int id_customer = findCustomerID(statementPerson);

                for (int i = 4; i < values.length; i++) {
                    if (values[i] != null && values[i].length() > 1) {
                        if ((values[i].replaceAll("[^0-9]", "")).length() == 9) {     //phone number
                            DatabaseController.insertToDatabaseContacts(id_customer, 2, values[i], statementContact);
                        } else if (values[i].contains("@jabber") || values[i].contains("jbr")) {        //jabber
                            DatabaseController.insertToDatabaseContacts(id_customer, 3, values[i], statementContact);
                        } else if (pattern.matcher(values[i]).matches()) {                              //email
                            DatabaseController.insertToDatabaseContacts(id_customer, 1, values[i], statementContact);
                        } else {                                                                        //others
                            DatabaseController.insertToDatabaseContacts(id_customer, 0, values[i], statementContact);
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
        ResultSet resultSets = statement.getGeneratedKeys();
        int id_customer = 0;
        if (resultSets.next()) {
            id_customer = resultSets.getInt(1);
        }
        return id_customer;
    }
}
