package com.abinail.viewmodel;

import com.abinail.model.MainModule;
import com.abinail.model.MainModule.ModuleBuilder;

import com.abinail.interfaces.Listener;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class ViewController {
    @FXML
    TextArea textArea;
    @FXML
    Button startButton;
    @FXML
    Button cancelButton;
    @FXML
    Button saveToButton;
    @FXML
    ProgressBar linkProcessedBar;
    @FXML
    Label linkProcessedLabel;

    @FXML
    TextField matchesTextField;
    @FXML
    Label imgLoadedLabel;
    @FXML
    Label imgLoadedSizeLabel;
    @FXML
    ProgressIndicator imgLoadedProgress;

    private File folderToSave;
    private Stage stage;
    private MainModule module;


    private int linkFound;
    private int linkProcessed;
    private int imgLoaded;
    private long imgLoadedSize;
    private int imgFound;
    private int imgProcessed;

    public Listener taskEndListener = (sender, args) -> {
        Platform.runLater(() -> {
            cancelButton.setDisable(true);
            startButton.setDisable(false);
        });
    };

    public Listener linkFoundListener = (sender, arg) -> {
        Platform.runLater(() -> {
            linkFound++;
            updateLinkInfo();
        });
    };

    public Listener linkProcessedListener = (sender, arg) -> {
        Platform.runLater(() -> {
            linkProcessed++;
            updateLinkInfo();
        });
    };

    private void updateLinkInfo() {
        linkProcessedBar.setProgress(linkProcessed / (double) linkFound);
        linkProcessedLabel.setText(String.format("%d of %d", linkProcessed, linkFound));
    }

    public Listener imgLoadedListener = ((sender, arg) -> {
        Platform.runLater(() -> {
            imgLoaded++;
            imgLoadedLabel.setText(String.valueOf(imgLoaded));
        });
    });
    public Listener<Long> imgLoadedSizeListener = ((sender, size) -> {
        Platform.runLater(() -> {
            imgLoaded++;
            imgLoadedSize += size;
            imgLoadedLabel.setText(String.valueOf(imgLoaded));
            imgLoadedSizeLabel.setText(String.valueOf(imgLoadedSize));
        });
    });
    public Listener<Integer> imgFoundListener = ((sender, count) -> {
        Platform.runLater(() -> {
            imgFound += count;
            imgProgress();
        });
    });
    public Listener imgProcessedListener = ((sender, arg) -> {
        Platform.runLater(() -> {
            imgProcessed++;
            imgProgress();
        });
    });

    private void imgProgress() {
        imgLoadedProgress.setProgress(imgProcessed / (double) imgFound);
    }


    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {
        cancelButton.setDisable(true);
    }


    @FXML
    private void startButtonOnClick() throws IOException {
        if (folderToSave == null) {
            new Alert(Alert.AlertType.ERROR, "There is no folder to save", ButtonType.OK).showAndWait();
            saveButtonOnClick();
            return;
        }

        module = new ModuleBuilder().setImgFoundListener(imgFoundListener)
                .setImgLoadedSizeListener(imgLoadedSizeListener).setImgProcessedListener(imgProcessedListener)
                .setLinkFoundListener(linkFoundListener).setLinkProcessedListener(linkProcessedListener)
                .setTaskEndListener(taskEndListener).build();

        module.start(textArea.getText(), matchesTextField.getText(), folderToSave);

        startButton.setDisable(true);
        cancelButton.setDisable(false);
    }

    @FXML
    private void saveButtonOnClick() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        folderToSave = directoryChooser.showDialog(stage);
    }

    @FXML
    public void cancelButtonOnClick() {
        dispose();
    }

    public void dispose() {
        if (module != null)
            module.dispose();
    }
    @FXML
    public void close(){
        dispose();
        stage.close();
    }
}
