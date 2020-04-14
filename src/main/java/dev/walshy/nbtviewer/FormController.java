package dev.walshy.nbtviewer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FormController {

    @FXML
    private MenuItem openFile;

    @FXML
    private MenuItem saveFile;

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

    @FXML
    void onSaveFileClicked(ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();

        final FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        final File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(this.result.getText());
                fw.flush();
            } catch (IOException e) {
                NBTViewer.printOutput(e);
            }
        }
    }

    public void setResult(String str) {
        this.result.setText(str);
    }

    public void appendText(String str) {
        this.result.appendText(str);
    }
}
