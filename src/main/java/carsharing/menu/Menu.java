package carsharing.menu;

import java.util.*;

public abstract class Menu {
    protected final List<String> menuOptions;
    protected MenuAction exitAction;
    protected int exitValue;
    protected String exitOption;
    protected int index;

    public Menu() {
        menuOptions = new ArrayList<>();
        exitAction = null;
        exitValue = 0;
        exitOption = "Exit";
        index = 1;
    }

    public abstract void displayMenu();

    public void setExitOption(String option, MenuAction action) {
            exitOption = option;
            exitAction = action;
    }

    public void setExitOption(MenuAction action) {
        setExitOption("Exit", action);
    }

    public void setExitOption(String option) {
        setExitOption(option, null);
    }

}
