package login;

import javafx.fxml.FXML;
import operating.window.SheetWindowController;

public class LoginWindowController {

    @FXML private SheetWindowController sheetWindowController;


    @FXML
    public void initialize() {
        sheetWindowController.setLoginWindowController(this);
    }
}
