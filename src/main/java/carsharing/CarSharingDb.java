package carsharing;

import carsharing.model.Car;
import carsharing.model.Company;
import carsharing.model.Customer;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CarSharingDb implements CarSharingDao {
    private final DataSource dataSource;
    private static final String CREATE_COMPANY_TABLE = """
        CREATE TABLE IF NOT EXISTS COMPANY(
        ID INT AUTO_INCREMENT PRIMARY KEY,
        NAME VARCHAR(50) UNIQUE NOT NULL
        )
        """;
    private static final String CREATE_CAR_TABLE = """
        CREATE TABLE IF NOT EXISTS CAR(
        ID INT AUTO_INCREMENT PRIMARY KEY,
        NAME VARCHAR(50) UNIQUE NOT NULL,
        COMPANY_ID INT NOT NULL,
        CONSTRAINT FK_COMPANY FOREIGN KEY (COMPANY_ID)
        REFERENCES COMPANY(ID)
        )
        """;
    private static final String CREATE_CUSTOMER_TABLE = """
        CREATE TABLE IF NOT EXISTS CUSTOMER(
        ID INT AUTO_INCREMENT PRIMARY KEY,
        NAME VARCHAR(50) UNIQUE NOT NULL,
        RENTED_CAR_ID INT NULL,
        CONSTRAINT FK_CAR FOREIGN KEY (RENTED_CAR_ID)
        REFERENCES CAR(ID)
        )
        """;
    private static final String ADD_COMPANY = "INSERT INTO COMPANY(NAME) VALUES (?)";
    private static final String FIND_ALL_COMPANIES = "SELECT * FROM COMPANY";
    private static final String ADD_CAR = "INSERT INTO CAR(NAME, COMPANY_ID) VALUES(?, ?)";
    private static final String FIND_ALL_CARS = "SELECT * FROM CAR";
    private static final String FIND_COMPANY_CARS = "SELECT ID, NAME FROM CAR WHERE COMPANY_ID = ? ORDER BY ID";
    private static final String FIND_AVAILABLE_CARS = """
        SELECT CARS.ID, CARS.NAME FROM (SELECT ID, NAME FROM CAR WHERE COMPANY_ID = ?) AS CARS
        LEFT JOIN CUSTOMER ON CARS.ID = CUSTOMER.RENTED_CAR_ID
        WHERE CUSTOMER.RENTED_CAR_ID IS NULL
        """;
    private static final String ADD_CUSTOMER = "INSERT INTO CUSTOMER(NAME) VALUES (?)";
    private static final String FIND_ALL_CUSTOMERS = "SELECT * FROM CUSTOMER";
    private static final String UPDATE_CUSTOMER = "UPDATE CUSTOMER SET NAME = ?, RENTED_CAR_ID = ?";
    private static final String WHERE_ID = " WHERE ID = ?";


    public CarSharingDb(DataSource dataSource) {
        this.dataSource = dataSource;
        initDB();
    }

    private void initDB(){
        run(CREATE_COMPANY_TABLE);
        run(CREATE_CAR_TABLE);
        run(CREATE_CUSTOMER_TABLE);
    }

    private void run(String query){
        try(Connection connection = dataSource.getConnection();
            Statement st = connection.createStatement()) {
            st.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void addCompany(Company company) {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(ADD_COMPANY) ){
            ps.setString(1, company.getName());
            ps.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Company> findAllCompanies() {
        try(Connection connection = dataSource.getConnection();
            Statement st = connection.createStatement()){
            ResultSet rs = st.executeQuery(FIND_ALL_COMPANIES);
            if (rs.isBeforeFirst()) {
                List<Company> companies = new ArrayList<>();
                while (rs.next()) {
                    int id = rs.getInt("ID");
                    String name = rs.getString("NAME");
                    companies.add(new Company(id, name));
                }
                return companies;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return List.of();
    }

    @Override
    public Optional<Company> findCompanyById(int companyId) {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(FIND_ALL_COMPANIES + WHERE_ID)) {
            ps.setInt(1, companyId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("ID");
                String name = rs.getString("NAME");
                return Optional.of(new Company(id, name));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public void addCar(Car car) {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(ADD_CAR)){
            ps.setString(1, car.getName());
            ps.setInt(2, car.getCompany().getId());
            ps.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Optional<Car> findCarById(int carId) {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(FIND_ALL_CARS + WHERE_ID)) {
            ps.setInt(1, carId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("ID");
                String name = rs.getString("NAME");
                int companyId = rs.getInt("COMPANY_ID");
                Company company = findCompanyById(companyId).orElse(null);
                return Optional.of(new Car(id, name, company));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }

    private List<Car> findCompanyCars(Company company, String query) {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, company.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                List<Car> cars = new ArrayList<>();
                while (rs.next()) {
                    int id = rs.getInt("ID");
                    String name = rs.getString("NAME");
                    cars.add(new Car(id, name, company));
                }
                return cars;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return List.of();
    }

    @Override
    public List<Car> findAvailableCarsByCompany(Company company) {
        return findCompanyCars(company, FIND_AVAILABLE_CARS);
    }

    @Override
    public List<Car> findAllCarsByCompany(Company company) {
        return findCompanyCars(company, FIND_COMPANY_CARS);
    }

    @Override
    public void addCustomer(Customer customer) {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(ADD_CUSTOMER)){
            ps.setString(1, customer.getName());
            ps.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Customer> findAllCustomers() {
        try(Connection connection = dataSource.getConnection();
            Statement st = connection.createStatement()) {
            ResultSet rs = st.executeQuery(FIND_ALL_CUSTOMERS);
            if (rs.isBeforeFirst()) {
                List<Customer> customers = new ArrayList<>();
                while (rs.next()) {
                    int id = rs.getInt("ID");
                    String name = rs.getString("NAME");
                    customers.add(new Customer(id, name));
                }
                return customers;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return List.of();
    }

    @Override
    public Optional<Customer> findCustomerById(int customerId) {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(FIND_ALL_CUSTOMERS + WHERE_ID)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("ID");
                String name = rs.getString("NAME");
                int rentedCarId = rs.getInt("RENTED_CAR_ID");
                Car car = findCarById(rentedCarId).orElse(new Car(rentedCarId));
                return Optional.of(new Customer(id, name, car));
            }
        } catch (SQLException e) {
           System.out.println(e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public boolean updateCustomer(Customer customer) {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(UPDATE_CUSTOMER + WHERE_ID)) {
            ps.setString(1, customer.getName());
            if (customer.getRentedCar() == null) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(2, customer.getRentedCar().getId());
            }
            ps.setInt(3, customer.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}
