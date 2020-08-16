import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Scanner;

public class ConfigurationApp {

    public static String filePathConfiguration() throws Exception{
        Scanner scan = new Scanner(System.in);
        Properties p = new Properties();
        InputStream inputStream = new FileInputStream("dataConfig.properties");
        p.load(inputStream);
        String url = p.getProperty("url");

        System.out.println("\n================================\nENTER A FILE NAME WITH EXTENSION");
        String fileName = scan.nextLine();
        String path = url + fileName;

        return path;
    }

    public static void configPath() throws Exception{
        Properties p = new Properties();
        OutputStream os = new FileOutputStream("dataConfig.properties");

        p.setProperty("url", "/Users/karol/Desktop/");
        p.store(os, null);
    }


    public static void menu() throws Exception {
        Scanner scan = new Scanner(System.in);
        System.out.println("====================\n1. CSV TO DATABASE \n2. XML TO DATABASE\n0. EXIT");
        int menuPosition = scan.nextInt();

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
