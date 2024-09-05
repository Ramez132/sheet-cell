package menu;

public enum MainMenu{
    READ_FILE("Load a sheet-cell from an XML file."),
    SHOW_SHEET("Print the most recent sheet to the console."),
    SHOW_CELL_VALUE("Print the data of a specific cell."),
    UPDATE_CELL_VALUE("Update the original value of a specific cell and print the new sheet."),
    SHOW_VERSIONS("Print a sheet from a specific version."),
    EXIT("Exit the system");

    private final String description;

    MainMenu(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static void PrintMenuAndAskForInput() {
        System.out.println();
        System.out.println("Main Menu:");

        int number = 1;
        for (MainMenu option : MainMenu.values()) {
            System.out.printf("%d. %s%n", number++, option.getDescription());
        }
        System.out.println();
        System.out.println("Please write only the number of your choice (1-6), and then press enter.");
    }

}
