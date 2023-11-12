package carsharing;

import carsharing.menu.ActionMenu;
import carsharing.menu.ConsumerMenu;
import carsharing.menu.Menu;
import carsharing.menu.MenuType;
import carsharing.model.Car;
import carsharing.model.Company;
import carsharing.model.Customer;

import java.util.*;
import java.util.stream.IntStream;

import static carsharing.menu.MenuType.*;

public class CarSharingUI {

    private final CarSharingService service;
    private final Scanner scanner;
    private final Map<MenuType, Menu> navMap;
    private MenuType currentMenu;
    private Company selectedCompany;
    private Customer selectedCustomer;
    private boolean running;

    public CarSharingUI(CarSharingService service) {
        this.service = service;
        scanner = new Scanner(System.in);
        navMap = new HashMap<>(values().length);
        loadNavigationMap();
        currentMenu = MAIN;
        running = true;
    }

    private void loadNavigationMap() {
        ActionMenu mainMenu = new ActionMenu();
        mainMenu.addOption("Log in as a manager", this::toManagerMenu);
        mainMenu.addOption("Log in as a customer", this::showCustomers);
        mainMenu.addOption("Create a customer", this::createCustomer);
        mainMenu.setExitOption(this::exit);

        ActionMenu managerMenu = new ActionMenu();
        managerMenu.addOption("Company list", this::showCompanies);
        managerMenu.addOption("Create a company", this::createCompany);
        managerMenu.setExitOption("Back", this::toMainMenu);

        ActionMenu companyMenu = new ActionMenu();
        companyMenu.addOption("Car list", this::showCompanyCars);
        companyMenu.addOption("Create a car", this::createCar);
        companyMenu.setExitOption("Back", this::toManagerMenu);

        ActionMenu customerMenu = new ActionMenu();
        customerMenu.addOption("Rent a car", this::rentCarChooseCompany);
        customerMenu.addOption("Return a rented car", this::returnRentedCar);
        customerMenu.addOption("My rented car", this::rentedCarInfo);
        customerMenu.setExitOption("Back", this::toMainMenu);

        navMap.put(MAIN, mainMenu);
        navMap.put(MANAGER, managerMenu);
        navMap.put(COMPANY, companyMenu);
        navMap.put(CUSTOMER, customerMenu);
    }

    public void displayMenu() {
        while (running) {
            navMap.get(currentMenu).displayMenu();
        }
    }

    private void showCompanies() {
        List<Company> companies = service.findAllCompanies();
        System.out.println();
        if (companies.isEmpty()) {
            System.out.println("The company list is empty!");
        } else {
            System.out.println("Choose a company:");
            ConsumerMenu<Company> companiesMenu = new ConsumerMenu<>(this::toCompanyMenu);
            companies.forEach(company -> companiesMenu.addOption(company.getName(), company));
            companiesMenu.setExitOption("Back");
            companiesMenu.displayMenu();
        }
    }

    private void createCompany() {
        System.out.println();
        System.out.println("Enter the company name:");
        String companyName = scanner.nextLine();
        service.addCompany(new Company(companyName));
        System.out.println("The company was created!");
    }

    private void showCompanyCars() {
        List<Car> companyCars = service.findAllCarsByCompany(selectedCompany);
        System.out.println();
        if (companyCars.isEmpty()) {
            System.out.println("The car list is empty!");
        } else {
            System.out.printf("'%s' cars:%n", selectedCompany.getName());
            IntStream.range(0, companyCars.size())
                    .forEach(i -> System.out.printf("%d. %s%n", i + 1, companyCars.get(i).getName()));
        }
    }

    private void createCar() {
        System.out.println();
        System.out.println("Enter the car name:");
        String carName = scanner.nextLine();
        service.addCar(new Car(carName, selectedCompany));
        System.out.println("The car was added!");
    }

    private void createCustomer() {
        System.out.println();
        System.out.println("Enter the customer name:");
        String customerName = scanner.nextLine();
        service.addCustomer(new Customer(customerName));
        System.out.println("The customer was added!");
    }

    private void showCustomers() {
        System.out.println();
        List<Customer> customers = service.findAllCustomers();
        if (customers.isEmpty()) {
            System.out.println("The customer list is empty!");
        } else {
            System.out.println("Customer list:");
            ConsumerMenu<Customer> customersMenu = new ConsumerMenu<>(this::toCustomerMenu);
            customers.forEach(customer -> customersMenu.addOption(customer.getName(), customer));
            customersMenu.setExitOption("Back");
            customersMenu.displayMenu();
        }
    }

    private void rentCarChooseCompany() {
        System.out.println();
        Optional<Car> opCar = service.findRentedCarByCostumer(selectedCustomer);
        if (opCar.isPresent()) {
            System.out.println("You've already rented a car!");
            return;
        }
        List<Company> companies = service.findAllCompanies();
        if (companies.isEmpty()) {
            System.out.println("The company list is empty!");
            return;
        }
        System.out.println("Choose a company:");
        ConsumerMenu<Company> companiesMenu = new ConsumerMenu<>(this::rentCarChooseCar);
        companies.forEach(c -> companiesMenu.addOption(c.getName(), c));
        companiesMenu.setExitOption("Back");
        companiesMenu.displayMenu();
    }

    private void rentCarChooseCar(Company company) {
        System.out.println();
        List<Car> availableCars = service.findAvailableCarsByCompany(company);
        if (availableCars.isEmpty()) {
            System.out.printf("No available cars in the '%s' company%n", company.getName());
        } else {
            System.out.println("Choose a car:");
            ConsumerMenu<Car> carsMenu = new ConsumerMenu<>(this::rentCar);
            availableCars.forEach(car -> carsMenu.addOption(car.getName(), car));
            carsMenu.setExitOption("Back");
            carsMenu.displayMenu();
        }
    }

    private void rentCar(Car car) {
        System.out.println();
        selectedCustomer.setRentedCar(car);
        service.rentCar(selectedCustomer);
        System.out.printf("You rented '%s'%n", car.getName());
    }

    private void returnRentedCar() {
        System.out.println();
        Optional<Car> opCar = service.findRentedCarByCostumer(selectedCustomer);
        if (opCar.isEmpty()) {
            System.out.println("You didn't rent a car!");
        } else {
            if (service.returnRentedCarByCostumer(selectedCustomer)) {
                System.out.println("You've returned a rented car!");
            }
        }
    }

    private void rentedCarInfo() {
        System.out.println();
        Optional<Car> opCar = service.findRentedCarByCostumer(selectedCustomer);
        if (opCar.isEmpty()) {
            System.out.println("You didn't rent a car!");
        } else {
            Car car = opCar.get();
            System.out.printf("Your rented car:%n%s%n", car.getName());
            System.out.printf("Company:%n%s%n", car.getCompany().getName());
        }
    }

    private void setCurrentMenu(MenuType menu) {
        currentMenu = menu;
    }

    private void toCompanyMenu(Company company) {
        selectedCompany = company;
        currentMenu = COMPANY;
        System.out.printf("%n'%s' company%n", company.getName());
    }

    private void toCustomerMenu(Customer customer) {
        selectedCustomer = customer;
        currentMenu = CUSTOMER;
    }

    private void toManagerMenu() {
        setCurrentMenu(MANAGER);
    }

    private void toMainMenu() {setCurrentMenu(MAIN);}

    private void exit() {
        running = false;
    }
}
