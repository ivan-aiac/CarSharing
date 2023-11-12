package carsharing;

import carsharing.model.Car;
import carsharing.model.Company;
import carsharing.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CarSharingDao {
    void addCompany(Company company);
    List<Company> findAllCompanies();
    Optional<Company> findCompanyById(int companyId);
    void addCar(Car car);
    Optional<Car> findCarById(int carId);
    List<Car> findAvailableCarsByCompany(Company company);
    List<Car> findAllCarsByCompany(Company company);
    void addCustomer(Customer customer);
    List<Customer> findAllCustomers();
    Optional<Customer> findCustomerById(int customerId);
    boolean updateCustomer(Customer customer);
}
