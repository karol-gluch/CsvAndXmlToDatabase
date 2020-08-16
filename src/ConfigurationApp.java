import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

public class ConfigurationApp {

    public static String getFilePathConfiguration() throws Exception{
        Scanner scanner = new Scanner(System.in);
        Properties properties = new Properties();
        InputStream inputStream = new FileInputStream("dataConfig.properties");
        properties.load(inputStream);
        String url = properties.getProperty("url");

        System.out.println("\n================================\nENTER A FILE NAME WITH EXTENSION");
        String fileName = scanner.nextLine();
        String path = url + fileName;

        return path;
    }

    public static void displayMenu() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("====================\n1. CSV TO DATABASE \n2. XML TO DATABASE\n0. EXIT");
        int menuPosition = scanner.nextInt();

        switch (menuPosition) {
            case 1:
                CsvController.readDataFromCsv();
                break;

            case 2:
                XmlController.readDataFromXml();
                break;

            default:
                break;
        }

        System.out.println("================================\nEND");
    }
}
