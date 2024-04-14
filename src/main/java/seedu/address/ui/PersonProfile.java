package seedu.address.ui;

import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.person.Person;

/**
 * A UI component that displays the detailed profile of a {@code Person}.
 */
public class PersonProfile extends UiPart<Region> {

    private static final String FXML = "PersonProfile.fxml";

    private Person person;

    @FXML
    private VBox profilePane;
    @FXML
    private Label name;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    private FlowPane tags;
    @FXML
    private Label upcoming;
    @FXML
    private Label lastcontact;

    /**
     * Creates a {@code PersonProfile}.
     */
    public PersonProfile() {
        super(FXML);
        profilePane.setVisible(false);
    }

    /**
     * Creates a {@code PersonProfile} with the given {@code Person}.
     */
    public PersonProfile(Person person) {
        super(FXML);
        this.person = person;
        setFields();
        profilePane.setVisible(true);
    }

    /**
     * Updates the {@code PersonProfile} with the given {@code Person}.
     */
    public void setPerson(Person person) {
        this.person = person;
        setFields();
        profilePane.setVisible(true);
    }

    private void setFields() {
        name.setText(person.getName().fullName);
        phone.setText("Phone number: " + person.getPhone().value);
        address.setText("Address: " + person.getAddress().value);
        email.setText("Email: " + person.getEmail().value);
        setUpcomingField();
        setLastcontactField();
        setTagsField();
    }

    private void setUpcomingField() {
        if (person.hasUpcoming()) {
            upcoming.setVisible(true);
            upcoming.setManaged(true);
            upcoming.setText("Upcoming: " + person.getUpcoming().toString());
        } else {
            upcoming.setVisible(false);
            upcoming.setManaged(false);
        }
    }

    private void setLastcontactField() {
        if (person.hasLastcontact()) {
            lastcontact.setVisible(true);
            lastcontact.setManaged(true);
            lastcontact.setText("Last contacted: " + person.getLastcontact().toString());
        } else {
            lastcontact.setVisible(false);
            lastcontact.setManaged(false);
        }
    }

    private void setTagsField() {
        tags.getChildren().clear();
        person.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));
    }

}
