package menu;

public enum MainMenu{
    READ_FILE("Load sheet (XML)"),
    SHOW_SHEET("Show sheet"),
    SHOW_CELL_VALUE("Show value of a single cell"),
    UPDATE_CELL_VALUE("Update value of a single cell"),
    SHOW_VERSIONS("Show versions"),
    EXIT("Exit the system");

    private final String description;

    MainMenu(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static void PrintMenu() {
        System.out.println("=== " + "Main Menu" + " ===");
        int number = 1;
        for (MainMenu option : MainMenu.values()) {
            System.out.printf("%d. %s%n", number++, option.getDescription());
        }
        System.out.println("=================");
    }

}
