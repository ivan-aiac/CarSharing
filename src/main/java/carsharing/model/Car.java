package carsharing.model;

public class Car {
    private final int id;
    private String name;
    private Company company;

    public Car(int id, String name, Company company) {
        this.id = id;
        this.name = name;
        this.company = company;
    }

    public Car(String name, Company company) {
        this(0, name, company);
    }

    public Car(int id) {
        this(id, null, null);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Company getCompany() {
        return company;
    }
}
