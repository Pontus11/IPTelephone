import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;

import java.util.Comparator;


/**
 * Created by pontu on 2018-03-17.
 */
/*
GUI class for the display of the contact view.
 */
public class ContactView extends ListView<ContactDisplay> {
    private PhoneGuiFX phoneGuiFX;

    /*
    Standard constructor.
    Sets the size of the contactView.
    When the selected contactDisplay changes the chat window is cleared by calling
    PhoneGuiFX.clearChatWindow().
    The new contactDisplay that has been selected then has its chat history displayed in the chat window by calling
    PhoneGuiFX.loadChatWindow().
     */
    public ContactView(PhoneGuiFX gui) {
        phoneGuiFX = gui;

        this.setPrefHeight(450);
        this.setPrefWidth(200);
        this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ContactDisplay>() {
            @Override
            public void changed(ObservableValue<? extends ContactDisplay> observable, ContactDisplay oldValue, ContactDisplay newValue) {
                if(oldValue != null) {
                    oldValue.removeSelection();
                    phoneGuiFX.clearChatWindow();
                }
                if(newValue != null) {
                    newValue.selectContact();
                    System.out.println(newValue.getUser().getLastKnownIp());
                    phoneGuiFX.loadChatWindow(newValue);
                }
            }
        });
    }
}

