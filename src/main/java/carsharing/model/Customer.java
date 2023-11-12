package carsharing.model;

public class Customer {
    private int id;
    private String name;
    private Car rentedCar;

    public Customer(int id, String name, Car rentedCar) {
        this.id = id;
        this.name = name;
        this.rentedCar = rentedCar;
    }

    public Customer(String name) {
        this(0, name, null);
    }

    public Customer(int id, String name) {
        this(id, name, null);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Car getRentedCar() {
        return rentedCar;
    }

    public void setRentedCar(Car rentedCar) {
        this.rentedCar = rentedCar;
    }
}
