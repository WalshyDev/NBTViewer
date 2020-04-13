package dev.walshy.nbtviewer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.File;

public class FormController {

    @FXML
    private MenuItem openFile;

    @FXML
    private TextArea result;

    @FXML
    void onOpenFileClicked(ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select NBT file");
        File f = fileChooser.showOpenDialog(null);
        if (f == null) return;

        NBTViewer.getInstance().readFile(f);
    }

    public void setResult(String str) {
        this.result.setText(str);
    }
}
