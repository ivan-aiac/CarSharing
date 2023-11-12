package carsharing;

import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<String> argsList = Arrays.asList(args);
        int i = argsList.indexOf("-databaseFileName");
        String dbName = i == -1 ? "CarSharingDB" : argsList.get(i + 1);
        CarSharing carSharing = new CarSharing(dbName);
        carSharing.start();
    }
}