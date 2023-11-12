package carsharing.menu;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ActionMenu extends Menu {
    private final Map<Integer, MenuAction> navMap;

    public ActionMenu() {
        navMap = new HashMap<>();
    }

    public void addOption(String option, MenuAction action) {
            menuOptions.add(String.format("%d. %s", index, option));
            navMap.put(index, action);
            index++;
    }

    @Override
    public void displayMenu() {
        if (!menuOptions.isEmpty()) {
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.println();
                menuOptions.forEach(System.out::println);
                System.out.printf("%d. %s%n", exitValue, exitOption);
                int option = Integer.parseInt(scanner.nextLine());
                if (option == exitValue) {
                    if (exitAction != null)
                        exitAction.execute();
                } else {
                    navMap.getOrDefault(option, this::invalidOption).execute();
                }
            } catch (Exception e) {
                invalidOption();
            }
        }
    }

    private void invalidOption() {
        System.out.println("Invalid menu option");
    }
}
