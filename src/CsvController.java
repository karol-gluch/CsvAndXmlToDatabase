import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.util.regex.Pattern;

public class CsvController {

    public static void readDataFromCsv() throws Exception {
        String pathCsv = ConfigurationApp.getFilePathConfiguration();

        String values[] = new String[0];
        BufferedReader csvReader = new BufferedReader(new FileReader(pathCsv));

        DatabaseController dbController = new DatabaseController();

        final String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);

        try {
            String row;
            while ((row = csvReader.readLine()) != null) {
                values = row.split(",");

                //insert name,surname and age

                int id_customer = dbController.insertToDatabasePersonCsv(values[0], values[1], values[2]);


                for (int i = 4; i < values.length; i++) {
                    if (values[i] != null && values[i].length() > 1) {
                        if ((values[i].replaceAll("[^0-9]", "")).length() == 9) {     //phone number
                            dbController.insertToDatabaseContacts(id_customer, 2, values[i]);
                        } else if (values[i].contains("@jabber") || values[i].contains("jbr")) {        //jabber
                            dbController.insertToDatabaseContacts(id_customer, 3, values[i]);
                        } else if (pattern.matcher(values[i]).matches()) {                              //email
                            dbController.insertToDatabaseContacts(id_customer, 1, values[i]);
                        } else {                                                                        //others
                            dbController.insertToDatabaseContacts(id_customer, 0, values[i]);
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
}
