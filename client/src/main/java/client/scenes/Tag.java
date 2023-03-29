package client.scenes;

import client.utils.ServerUtils;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;

public class Tag extends Pane {

    private final MainCtrl mainCtrl;
    private final ServerUtils server;
    private commons.Tag tag;

    @FXML
    MFXTextField tagName;
    @FXML
    Pane tagPane;
    @FXML
    MFXButton deleteTag;
    @FXML
    MFXButton edit;
    @FXML
    MFXButton saveTag;
    @FXML
    ColorPicker colorPicker;

    public Tag(MainCtrl mainCtrl, ServerUtils server, commons.Tag tag) {

        this.mainCtrl = mainCtrl;
        this.server = server;
        this.tag = tag;

        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/client/scenes/Components/Tag.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            System.out.println("Error");
            throw new RuntimeException(e);
        }

        setColor(tag.getColor());
        tagName.setText(tag.getText());

        colorPicker.getStyleClass().add("button");
        colorPicker.setStyle("-fx-color-label-visible: false;-fx-background-color: transparent");
        colorPicker.setValue(Color.web(tag.getColor()));

        deleteTag.setOnAction(event -> {
            server.removeTag(tag.tagId);
            ((VBox) getParent()).getChildren().remove(this);
        });

        edit.setOnAction(event -> editTag());
        saveTag.setOnAction(event -> saveTag());

        setOnDragDetected(event -> {
            Dragboard db = startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString("Tag:" + tag.tagId);
            db.setContent(content);
            event.consume();
        });
    }

    private void editTag() {
        edit.setVisible(false);
        deleteTag.setVisible(false);
        saveTag.setVisible(true);
        tagName.setDisable(false);

        colorPicker.setVisible(true);
    }

    private void saveTag() {
        edit.setVisible(true);
        deleteTag.setVisible(true);
        saveTag.setVisible(false);
        colorPicker.setVisible(false);
        tagName.setDisable(true);

        tag.setColor("#" + colorPicker.getValue().toString().substring(2, 8));
        setColor(tag.getColor());
        tag.setText(tagName.getText());
        tag = server.updateTag(tag);
    }

    public void setColor(String color) {
        tagPane.setStyle("-fx-background-radius: 10px; -fx-background-color: " + color);
    }

    public Long getTagId() {
        return tag.tagId;
    }
}