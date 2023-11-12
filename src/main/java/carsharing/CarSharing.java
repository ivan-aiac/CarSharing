package carsharing;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;

public class CarSharing {
    private final CarSharingUI ui;

    public CarSharing(String dbName) {
        DataSource dataSource = createH2DataSource(dbName);
        CarSharingDb db = new CarSharingDb(dataSource);
        CarSharingService service = new CarSharingService(db);
        ui = new CarSharingUI(service);
    }

    public void start() {
        ui.displayMenu();
    }

    private DataSource createH2DataSource(String dbName) {
        String h2_URL = "jdbc:h2:./src/carsharing/db/";
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl(h2_URL + dbName);
        return dataSource;
    }
}
