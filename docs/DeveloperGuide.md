---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# FApro Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

This project is based on the AddressBook-Level3 project created by the [SE-EDU initiative](https://se-education.org).

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a client).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'filtered' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores the currently 'selected' `Person` object, only the copy of the `Person` object is exposed to the outsiders and the UI will be updated whenever a different `Person` object is 'selected'.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique plan, instead of each `Person` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>

### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.addressbook.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Select feature

#### Implementation

The feature enables us to select a particular client using an index and displays the profile. The mechanism is then facilitated by `SelectCommand` and `PersonProfile` UI component which inherits from the abstract `UiPart`. Additionally, the `PersonProfile` will auto-update if changes to the client is made using the following commands:
* `EditCommand`  —  The `EditCommand#execute()` method will return a `CommandResult` with a `true` for `updateProfile` if the client-to-edit is currently selected.
* `AddTagsCommand`  —  The `AddTagsCommand#execute()` method will also do the same as `EditCommand#execute()`.

The `updateProfile` mentioned above is a field in a `CommandResult` object. Within the object, there is also a `CommandResult#isUpdateProfile()` method to flag to the `MainWindow` if there is a need to update the details in the aforementioned `PersonProfile`.

Given below is an example usage scenario of the select command

Step 1. The user executes `select 1` command to select the 1st client in the address book. The `select` command calls `Model#updateSelectedPerson()`, resulting in the selected client to be stored in the `Model`.

Step 2. The `select` command returns a `CommandResult` object with the `feedbackToUser` as the successfully-selected-client message, `showHelp` as false, `isExit` as false, `updateProfile` as true.

<box type="info" seamless>

**Note:** If a command fails its execution, it will return a `CommandException` with the relevant message instead, just like the other commands.

</box>

Step 3. The `CommandResult` is used in the `MainWindow#executeCommand()` method. Since, `CommandResult#isupdateProfile()` returns true for a `select` command, the `selectedPerson` is retrieved using `Logic#getSelectedPerson` and displayed in the `PersonProfile` using `PersonProfile#setPerson()`.

The following sequence diagram shows how a select operation goes through the `Ui`, `Logic` and `Model` component:

<puml src="diagrams/SelectSequenceDiagram.puml" alt="SelectSequenceDiagram" />

<box type="info" seamless>

**Note:** The lifeline for `SelectCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</box>

#### Design considerations:

**Aspect: How to select a client:**

* **Alternative 1 (current choice):** Select using index.
    * Pros: Easy to implement.
    * Cons: Not very intuitive, have to look up the name, reference the index before selecting the client.

* **Alternative 2:** Select using name.
    * Pros: More intuitive and easy to select.
    * Cons: May result in bugs due to the issue of duplicate names.

_{more aspects and alternatives to be added}_

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th client in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new client. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</box>

Step 4. The user now decides that adding the client was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the client being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* is a financial advisor
* has a need to manage over 50 clients
* prefer desktop apps over other types
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: manage contacts faster than a typical mouse/GUI driven app and revolutionizes client engagement
for financial advisors by facilitating strategic communication and personalised service.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                                                          | I want to …​                                                      | So that I can…​                                                                                             |
|---------|------------------------------------------------------------------|-------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------|
| `* * *` | new financial advisor user                                       | see usage instructions                                            | refer to instructions when I forget how to use the App                                                      |
| `* * *` | financial advisor                                                | add a new client                                                  | store their contacts and other relevant information                                                         |
| `* * *` | financial advisor                                                | delete a client                                                   | remove informtion of the clients that I am no longer serving                                                |
| `* * *` | financial advisor with more than 50 clients                      | find a client by name                                             | locate details of clients without having to go through the entire list                                       |
| `* * *` | financial advisor with more than 50 clients                      | easily identity those that I haven't reach out to for a long time | contact them and check on their progress as well as well-being                                              |
| `* * *` | financial advisor with many upcoming meeting                     | easily view my schedule                                           | plan and prepare the respective information for the respective meetings, serving the client more effectively |
| `* * *` | financial advisor who provides multiple plans for my clients     | add plans to clients based on their existing plans                | keep track of which clients hold which policies                                                             |
| `* * *` | financial advisor who provides multiple plans for my clients     | find clients based on their existing plans                        | provide personalised service to each type of policy holder                                                  |
| `* * *` | financial advisor with more than 50 clients                      | view a client's profile with a few simple commands                | have the relevant information at hand when planning and during the consultations                            |
| `* * `  | financial advisor with more than 50 clients                      | set reminders for all the clients' birthday                       | build personal connection through timely greetings                                                          |
| `*`     | user with many clients in the address book                        | sort clients by name                                              | locate a client easily                                                                                      |
| `*`     | financial advisor who wants to help my clients reach their goals | keep track of their goals and financial progress                  | provide a more curated and clientalized service                                                             |

*{More to be added}*

### Use cases

(For all use cases below, the **System** is `FApro` and the **Actor** is the `financial advisor`, unless specified otherwise)

**Use case: Adding a client**

**MSS**

1.  User requests to add a new client with the relevant information.
2.  FApro adds the client.
3.  FApro shows the client information in the list. <br/>
    Use case ends.

**Extensions**

* 1a. The information provided is invalid or incomplete.
  * 1a1. FApro shows the error message and the correct format. <br/>
  Use case resumes at step 1. 
* 1b. The client already exists in FApro.
    * 1b1. FApro shows a message to let the user know about the duplicated entry. <br/>
    Use case resumes at step 1.

**Use case: Delete a client**

**MSS**

1.  User requests to list clients.
2.  FApro shows a list of clients.
3.  User requests to delete a specific client in the list.
4.  FApro deletes the client. <br/>
    Use case ends.

**Extensions**

* 2a. The list is empty. <br/>
    Use case ends. 
* 3a. The given index is invalid.
    * 3a1. FApro shows an error message. <br/>
    Use case resumes at step 2.

**Use case: Edit a client's info**

**MSS**

1.  User requests to edit a specific client's information.
2.  FApro edits the client's information and shows the updated client information in the list. <br/>
    Use case ends.

**Extensions**

* 1a. The given index is invalid. 
  * 1a1. FApro shows an error message. <br/>
  Use case resumes at step 1. 
* 1b. The information provided is invalid or incomplete.
    * 1b1. FApro shows the error message and the correct format. <br/>
    Use case resumes at step 1.
* 1c. The updated client's name already exists in FApro.
    * 1c1. FApro shows a message to let the user know about the duplicated entry. <br/>
    Use case resumes at step 1.

**Use case: Find a client by name**

**MSS**

1.  User requests to find clients with one or more keywords by name.
2.  FApro shows the filtered list of clients. <br/>
    Use case ends.

**Use case: Viewing a client's profile**

**MSS**

1.  User requests to list clients.
2.  FApro shows a list of clients.
3.  User requests to view the profile of the client in the list.
4.  FApro shows the detailed profile of the client. <br/>
    Use case ends.

**Extensions**

* 2a. The list is empty. <br/>
  Use case ends.
* 3a. The given index is invalid.
    * 3a1. AddressBook shows an error message. <br/>
    Use case resumes at step 2.

**Use case: Adding a plan to a client**

**MSS**

1.  User requests to list clients.
2.  AddressBook shows a list of clients.
3.  User requests to add a specified plan to a specific client in the list.
4.  AddressBook adds the plan to the client. <br/>
    Use case ends.

**Extensions**

* 2a. The list is empty. <br/>
  Use case ends.
* 3a. The given index is invalid.
    * 3a1. AddressBook shows an error message. <br/>
      Use case resumes at step 2.

**Use case: Removing plans from a client**

**MSS**

1.  User requests to list clients.
2.  AddressBook shows a list of clients.
3.  User requests to remove plans from a specific client in the list.
4.  AddressBook removes the plans of the client. <br/>
    Use case ends.

**Extensions**

* 2a. The list is empty. <br/>
  Use case ends.
* 3a. The given index is invalid.
    * 3a1. AddressBook shows an error message. <br/>
    Use case resumes at step 2.

**Use case: Finding all clients that contain any of the plans**

**MSS**

1.  User requests to find clients containing any of the specified plans
2.  AddressBook shows a list of clients

**Extensions**
* 1a. One of the plans is not alphanumeric.
    * 1a1. AddressBook shows an error message.
      Use case resumes at step 1.
  
**Use case: Finding all clients that contain all the plans**

**MSS**

1.  User requests to find clients containing all the specified plans
2.  AddressBook shows a list of clients <br/>
    Use case ends.

**Extensions**
* 1a. One of the plans is not alphanumeric.
    * 1a1. AddressBook shows an error message. <br/>
      Use case resumes at step 1.

**Use case: Clearing all contacts in FApro**

**MSS**

1.  User requests to clear all contacts.
2.  FApro clears all contacts. <br/>
    Use case ends.

**Use case: Exiting FApro**

**MSS**

1.  User requests to exit the application.
2.  FApro closes the window and its system. <br/>
    Use case ends.

**Use case: Finding all contacts who were last contacted**

**MSS**

1.  User requests to see all contacts who were last contacted.
2.  AddressBook shows a list of contacts who were last contacted sorted by least to most recent. <br/>
    Use case ends.

**Use case: Finding all upcoming contacts with appointments**

**MSS**

1.  User requests to find clients containing an upcoming appointment.
2.  AddressBook shows the list of clients, ordering them from the nearest to the farthest upcoming appointment based on date. <br/>
    Use case ends.

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `11` or above installed.
2.  Should be able to hold up to 1000 clients without a noticeable sluggishness in performance for typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4.  The data in FApro should be stored locally and should be in a human editable text file.
5.  Should not use a _DBMS_ to store data.
6.  Should work without requiring an installer.
7.  Should not depend on any _remote server_.
8.  _GUI_ should work well for standard screen resolutions 1920x080 and higher, and, for screen scales 100% and 125%.
9.  The size of the documents should not exceed 15MB/file.
10. The size of the final FApro product should not exceed 100MB.

*{More to be added}*

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **Private contact detail**: A contact detail that is not meant to be shared with others
* **DBMS**: Database Management System, a software suite that facilitates the creation, management, and manipulation of databases.
* **Remote server**: A computer system or software application that provides services or resources to other computers or clients over a network, typically the internet.
* **GUI**: Graphic User Interface, a visual interface that allows users to interact with electronic devices or software using graphical icons, menus, and other graphical elements, rather than text-based commands

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.
<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

### Selecting a client

1. Selecting a client while all clients are being shown

    1. Prerequisites: List all clients using the `list` command. Multiple clients in the list.
    1. Test case: `select 1`<br>
       Expected: First client's details are displayed in the profile panel. Details of the selected client shown in the status message.
    1. Test case: `select 0`<br>
       Expected: No client is selected. Error details shown in the status message.
    1. Other incorrect select commands to try: `select`, `select x`, `...` (where x is larger than the list size)<br>
       Expected: Similar to previous.

1. Selecting a client after finding for a specific client by name

    1. Prerequisites: Find clients with 'alex' in their name using the `find alex` command. One client in the list (if using sample data).
    1. Test case: `select 1`<br>
       Expected: Similar to select while all clients are being shown.
    1. Test case: `select 0`<br>
       Expected: Similar to select while all clients are being shown.
    1. Other incorrect select commands to try: `select`, `select x`, `...` (where x is larger than the list size)<br>
       Expected: Similar to select while all clients are being shown.

### Adding plans

1. Adding plans to a client while all clients are being shown
   1. Prerequisites: List all clients using the `list` command. Multiple clients in the list.
   2. Test case: `addtags 1 t/car `<br>
       Expected: First contact has `car` plan added to him.
   3. Test case: `addtags -1 t/housing`<br>
       Expected: No plans are added. Error details shown in the status message.
   4. Other incorrect commands to try: `addtags`, `addtags 2 t/`, `addtags 1`, `addtags x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.
   
2. Adding plans to a selected client
   1. Prerequisites: Select first client using `select 1` command.
   2. Test case: `addtags 1 t/car `<br>
      Expected: First contact has `car` plan added to him. Panel at the side is updated to include the `car` plan.
   3. Other test cases similar to above.

### Finding clients by their plans

1. Finding clients using the `findtagsor` command
   1. Test case: `findtagsor car housing`<br>
       Expected: All clients with either `car` or `housing` plans are listed
   2. Test case: `findtagsor`<br>
       Expected: Command not executed. Error details shown in the status message.
   3. Other incorrect commands to try: `findtagsor`, `findtagsor $#@`<br>
      Expected: Similar to previous.
2. Finding clients using the `findtagsand` command
    1. Test case: `findtagsand car housing`<br>
       Expected: All clients with both `car` or `housing` plans are listed
    2. Test case: `findtagsand`<br>
       Expected: Command not executed. Error details shown in the status message.
    3. Other incorrect commands to try: `findtagsand`, `findtagsand $#@`<br>
       Expected: Similar to previous.
--------------------------------------------------------------------------------------------------------------------

## **Appendix: Planned Enhancement**

Team size: 4

1. **Change `tag` to `plan`**: Currently, the error messages and commands use the wording `tag` instead of `plan`. However, our tags are specifically meant for financial plans and such ambiguous naming may confuse the users. Thus, we plan to change all the 'tag' to 'plan' to give the user a clearer picture.
2. **Change and standardize all the `person` to `client`**: Currently, some error messages and success messages refer to the person in FApro as `person` while others refer to them as `client`. This may lead to confusion of the users, thinking that we are referring to two different things when we are indeed referring to the same thing. Thus, we intend to change all to `client` as it is more specific and suit our context better.
3. **Enable tags (plans) with very long name to wrap**: Currently, when the tags are too long, the characters that is beyond the max width of the person card or the profile panel will be cut off and there is no way for the financial advisors to view the full tag. Thus, we intend to make the tags wrap around the max width of their container (person card or profile panel).
4. **Clear the profile panel if the selected person is deleted**: Currently, when the selected person is deleted from FApro, the profile panel will still display his/her information. Thus, we plan to fix this by clearing the profile panel when the selected person is deleted.
5. **Clear the profile panel if the entries in FApro are cleared**: This is similar to the enhancement above, only difference is that the `ClearCommand` is used to delete all clients/persons in FApro. Thus, our plan to fix this issue is also similar, which is to clear the profile panel.
6. **Remove upcoming date**: There is currently no way for the financial advisors to remove the upcoming dates even after the appointments have passed. We plan to implement a command to remove  the upcoming dates after the appointments have passed or when the clients cancel the appointment.
7. **Enable tags (plans) to be more than one word**: Our tags currently can only accommodate a single word. However, financial plans may consist of multiple words to differentiate one from the other. Thus, we plan to relax on the restriction of the plan parameters and allow multiple words.
8. **Add notes to the Profile Panel**: The profile panel currently only shows the default profile image, contact details, and existing plans. It is not adding much value to the financial advisors. Thus, we intend to implement a command which can add notes for a specific client that can be seen only on the profile panel. This will come in handy for the financial advisors when they want to take down reminders for themselves when preparing for the appointment, take down important details of the client during the appointment itself or even take down things to do after the appointment.

