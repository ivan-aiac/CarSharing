package carsharing.menu;

import carsharing.menu.Menu;

import java.util.*;
import java.util.function.Consumer;

public class ConsumerMenu<T> extends Menu {
    private final Consumer<T> consumer;
    private final Map<Integer, T> values;
    private T result;

    public ConsumerMenu(Consumer<T> consumer) {
        this.consumer = consumer;
        values = new HashMap<>();
    }

    public void addOption(String option, T value) {
            values.put(index, value);
            menuOptions.add(String.format("%d. %s", index, option));
            index++;
    }

    @Override
    public void displayMenu() {
        if (!menuOptions.isEmpty()) {
            try(Scanner scanner = new Scanner(System.in)){
                menuOptions.forEach(System.out::println);
                System.out.printf("%d. %s%n", exitValue, exitOption);
                int option = Integer.parseInt(scanner.nextLine());
                if (option == exitValue) {
                    if (exitAction != null)
                        exitAction.execute();
                } else {
                    if (values.containsKey(option)) {
                        consumer.accept(values.get(option));
                    } else {
                        invalidOption();
                    }
                }
            } catch (Exception e) {
                invalidOption();
            }
        }
    }

    public T getResult() {
        return result;
    }

    private void invalidOption() {
        System.out.println("Invalid menu option");
    }
}
