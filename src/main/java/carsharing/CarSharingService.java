package carsharing;

import carsharing.model.Car;
import carsharing.model.Company;
import carsharing.model.Customer;

import java.util.List;
import java.util.Optional;

public class CarSharingService {

    private final CarSharingDb db;

    public CarSharingService(CarSharingDb db) {
        this.db = db;
    }

    public void addCompany(Company company) {
        db.addCompany(company);
    }

    public List<Company> findAllCompanies() {
        return db.findAllCompanies();
    }

    public void addCar(Car car) {
        db.addCar(car);
    }

    public List<Car> findAllCarsByCompany(Company company) {
        return db.findAllCarsByCompany(company);
    }

    public void addCustomer(Customer customer) {
        db.addCustomer(customer);
    }

    public List<Customer> findAllCustomers() {
        return db.findAllCustomers();
    }

    public boolean returnRentedCarByCostumer(Customer customer) {
        customer.setRentedCar(null);
        return db.updateCustomer(customer);
    }

    public void rentCar(Customer customer) {
        db.updateCustomer(customer);
    }

    public Optional<Car> findRentedCarByCostumer(Customer customer) {
        Optional<Customer> opCustomer = db.findCustomerById(customer.getId());
        if (opCustomer.isPresent()) {
            Customer dbCustomer = opCustomer.get();
            if (dbCustomer.getRentedCar().getId() != 0) {
                return Optional.of(dbCustomer.getRentedCar());
            }
        }
        return Optional.empty();
    }

    public List<Car> findAvailableCarsByCompany(Company company) {
        return db.findAvailableCarsByCompany(company);
    }
}
