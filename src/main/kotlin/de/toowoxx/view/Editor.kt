package de.toowoxx.view

import de.toowoxx.controller.MainController
import de.toowoxx.controller.UserController
import de.toowoxx.model.ScanButtonModel
import de.toowoxx.model.UserModel
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import tornadofx.*


class Editor : View("User Editor") {
    val mainController: MainController by inject()

    override val root = hbox()
    var nameField: TextField by singleAssign()


    var scanButtonTitleField: TextField by singleAssign()
    var scanButtonCommandField: TextField by singleAssign()
    var scanButtonNumberField: TextField by singleAssign()
    var scanButtonImgCheckbox: CheckBox by singleAssign()


    var userTable: TableView<UserModel> by singleAssign()
    val userController: UserController by inject()

    var prevSelectionUser: UserModel? = null
    var prevSelectionScanButton: ScanButtonModel? = null


    var scanButtonList = observableListOf<ScanButtonModel>()
    var scanButtonTable: TableView<ScanButtonModel> by singleAssign()

    var scanButtonFieldset = fieldset()

    var iconButton: Button by singleAssign()
    var iconField: Field by singleAssign()

    val iconCheckboxProperty = SimpleBooleanProperty()


    val mainView: MainView by inject()


    init {
        with(root) {

            vbox() {

                tableview(userController.userList) {
                    userTable = this
                    column("User", UserModel::usernameProperty)
                    title = "Test123"
                    // Edit the currently selected person
                    selectionModel.selectedItemProperty().onChange {
                        editUser(it)
                        prevSelectionUser = it
                        if (it != null)
                            scanButtonList.setAll(it.userButtons)

                        scanButtonFieldset.hide()
                    }

                }
                hbox {
                    button("Neuer User").action {
                        newUser()
                    }
                    button("Löschen").action {
                        deleteUser()
                    }
                    paddingTop = 10
                }
                paddingAll = 15
            }


            vbox {
                tableview(scanButtonList) {
                    scanButtonTable = this
                    column("Titel", ScanButtonModel::titleProperty)
                    column("Command", ScanButtonModel::commandProperty)
                    column("Nummer", ScanButtonModel::buttonNumber)


                    selectionModel.selectedItemProperty().onChange {
                        editScanButton(it)
                        prevSelectionScanButton = it
                        scanButtonFieldset.show()
                    }
                }
                hbox {
                    button("Neuer Scanbutton").action {
                        newScanButton()
                        paddingRight = 5
                    }
                    button("Profil löschen").action {
                        deleteScanButton()
                    }
                    paddingTop = 10
                }
                paddingAll = 15
            }

            form {
                fieldset("User bearbeiten") {
                    field("Name") {
                        textfield() {
                            nameField = this
                        }
                    }
                }
                fieldset("Scan Button bearbeiten") {
                    scanButtonFieldset = this
                    field("Titel") {
                        textfield() {
                            scanButtonTitleField = this
                        }
                    }
                    field("Scan Profile") {
                        textfield() {
                            scanButtonCommandField = this
                        }
                    }
                    field("Button Nr.") {
                        textfield() {
                            scanButtonNumberField = this
                        }
                    }




                    field("Mit Icon?") {
                        checkbox("", iconCheckboxProperty) {
                            action {
                                if (isSelected)
                                    iconField.show()
                                else
                                    iconField.hide()
                            }

                        }
                    }

                    fieldset {
                        field("Auswahl") {
                            var iconCombo = combobox<String> {
                                items = mainController.getAvailableIconNames().asObservable()
                            }
                            iconCombo.setOnAction { event ->

                                var iconPath = mainController.getIconPath(iconCombo.value.toString())
                                var imageView = ImageView(Image("file:$iconPath"))
                                imageView.fitHeight = 70.0
                                imageView.fitWidth = 70.0
                                iconButton.graphic = imageView
                            }
                        }

                        iconField = field("Vorschau") {
                            iconButton = button { }
                        }
                        hiddenWhen(!iconCheckboxProperty)
                    }
                }.hide()


                hbox {
                    button("Speichern").action {
                        saveUser()
                    }
                }
                paddingAll = 15
            }
        }
    }


    private fun deleteUser() {
        val deletedUser = userTable.selectedItem!!

        for (user in userController.userList) {
            if (user.id == deletedUser.id) {
                userController.userList.remove(deletedUser)
                break
            }
        }
        userController.saveUsersToJson(userController.dataToJsonData(userController.userList))
        mainView.refreshUserbuttons()
    }

    private fun deleteScanButton() {
        val deletedScanButton = scanButtonTable.selectedItem!!

        for (button in scanButtonList) {
            if (button.id == deletedScanButton.id) {
                scanButtonList.remove(deletedScanButton)
                break
            }
        }

        val selectedUser = userTable.selectedItem!!
        selectedUser.userButtons = observableListOf()
        selectedUser.userButtons.addAll(scanButtonList)

        userController.saveUsersToJson(userController.dataToJsonData(userController.userList))
        prevSelectionScanButton = null
    }

    private fun newUser() {
        var newUser = UserModel(userController.userList.last().id + 1, "Neu", observableListOf())
        newUser.username = "Neu"
        newUser.id = userController.userList.last().id + 1
        userController.userList.add(newUser)
        userController.saveUsersToJson(userController.dataToJsonData(userController.userList))
        mainView.refreshUserbuttons()
    }

    private fun newScanButton() {
        val user = userTable.selectedItem!!
        var button = ScanButtonModel(99, "cmd", 2, "title")
        if (scanButtonList.isEmpty())
            button.id = 1
        else
            button.id = scanButtonList.last().id + 1
        button.command = "cmd"
        button.title = "neu"
        button.buttonNumber = "99"
        scanButtonList.add(button)

    }

    private fun editUser(person: UserModel?) {
        if (person != null) {
            prevSelectionUser?.apply {
                usernameProperty.unbindBidirectional(nameField.textProperty())
            }
            nameField.bind(person.usernameProperty)
            prevSelectionUser = person
        }
    }

    private fun editScanButton(scanButton: ScanButtonModel?) {
        if (scanButton != null) {
            prevSelectionScanButton?.apply {
                titleProperty.unbindBidirectional(scanButtonTitleField.textProperty())
                commandProperty.unbindBidirectional(scanButtonCommandField.textProperty())
                buttonNumberProperty.unbindBidirectional(scanButtonNumberField.textProperty())

            }
            scanButtonTitleField.bind(scanButton.titleProperty)
            scanButtonCommandField.bind(scanButton.commandProperty)
            scanButtonNumberField.bind(scanButton.buttonNumberProperty)
            prevSelectionScanButton = scanButton
        }
    }

    private fun saveUser() {
        // Extract the selected person from the tableView
        val editedUser = userTable.selectedItem!!

        for (it in userController.userList) {
            if (it.id == editedUser.id) {
                editedUser.userButtons.setAll(scanButtonList)
                break
            }
        }
        println("Saving ${editedUser.usernameProperty} / ")
        userController.saveUsersToJson(userController.dataToJsonData(userController.userList))
        mainView.refreshUserbuttons()
    }

    private fun clearScanButtonEdit() {
        scanButtonTitleField.clear()
        scanButtonNumberField.clear()
        scanButtonCommandField.clear()
    }

}