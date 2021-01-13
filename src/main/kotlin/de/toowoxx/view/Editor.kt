package de.toowoxx.view

import de.toowoxx.controller.UserController
import de.toowoxx.model.ButtonData
import de.toowoxx.model.UserModel
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import tornadofx.*


class Editor : View("User Editor") {


    override val root = hbox()
    var nameField: TextField by singleAssign()


    var scanButtonTitleField: TextField by singleAssign()
    var scanButtonCommandField: TextField by singleAssign()
    var scanButtonNumberField: TextField by singleAssign()


    var userTable: TableView<UserModel> by singleAssign()
    val userController: UserController by inject()

    var prevSelectionUser: UserModel? = null
    var prevSelectionScanButton: ButtonData? = null


    var scanButtonList = observableListOf<ButtonData>()
    var scanButtonTable: TableView<ButtonData> by singleAssign()

    var scanButtonFieldset = fieldset()


    init {
        with(root) {

            vbox {
                tableview(userController.userList) {
                    userTable = this
                    column("User", UserModel::usernameProperty)

                    // Edit the currently selected person
                    selectionModel.selectedItemProperty().onChange {
                        editUser(it)
                        prevSelectionUser = it

                        if (it != null) {
                            scanButtonList.setAll(it.userButtons)
                        }
                        if (it != null && it.userButtons.isNotEmpty()) {
                            prevSelectionScanButton = it.userButtons[0]
                            scanButtonList.setAll(it.userButtons)
                        }
                    }

                }
                hbox {
                    button("Neuer User").action {
                        newUser()
                    }
                    button("Löschen").action {
                        deleteUser()
                    }
                }

            }.paddingAll = 15


            vbox {
                tableview(scanButtonList) {
                    scanButtonTable = this
                    column("Titel", ButtonData::titleProperty)
                    column("Command", ButtonData::commandProperty)
                    column("Nummer", ButtonData::buttonNumber)


                    selectionModel.selectedItemProperty().onChange {
                        editScanButton(it)
                        prevSelectionScanButton = it
                        scanButtonFieldset.show()
                    }
                }
                hbox {
                    button("Neu").action {
                        newScanButton()
                    }
                    button("Profil löschen").action {
                        deleteScanButton()
                    }
                }

            }.paddingAll = 15

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
                }.hide()
                hbox {
                    button("Speichern").action {
                        saveUser()
                    }
                }
            }.paddingAll = 15
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
        userController.saveUsersToJson("users.json", userController.dataToJsonData(userController.userList))
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

        userController.saveUsersToJson("users.json", userController.dataToJsonData(userController.userList))
        prevSelectionScanButton = null
    }

    private fun newUser() {
        var newUser = UserModel(userController.userList.last().id + 1, "Neu", observableListOf())
        newUser.username = "Neu"
        userController.userList.add(newUser)
        userController.saveUsersToJson("users.json", userController.dataToJsonData(userController.userList))
    }

    private fun newScanButton() {
        val user = userTable.selectedItem!!
        var button = ButtonData(99, "cmd", 2, "title")
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

    private fun editScanButton(button: ButtonData?) {
        if (button != null) {
            prevSelectionScanButton?.apply {
                titleProperty.unbindBidirectional(scanButtonTitleField.textProperty())
                commandProperty.unbindBidirectional(scanButtonCommandField.textProperty())
                buttonNumberProperty.unbindBidirectional(scanButtonNumberField.textProperty())

            }
            scanButtonTitleField.bind(button.titleProperty)
            scanButtonCommandField.bind(button.commandProperty)
            scanButtonNumberField.bind(button.buttonNumberProperty)
            prevSelectionScanButton = button
        }
    }

    private fun saveUser() {
        // Extract the selected person from the tableView
        val editedUser = userTable.selectedItem!!

        for (it in userController.userList) {
            if (it.id == editedUser.id) {
                editedUser.userButtons = observableListOf()
                editedUser.userButtons.addAll(scanButtonList)
                userController.userList.remove(it)
                userController.userList.add(editedUser)

                break
            }
        }
        println("Saving ${editedUser.usernameProperty} / ")
        userController.saveUsersToJson("users.json", userController.dataToJsonData(userController.userList))
    }

    private fun clearScanButtonEdit() {
        scanButtonTitleField.clear()
        scanButtonNumberField.clear()
        scanButtonCommandField.clear()
    }


}