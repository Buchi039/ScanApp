package de.toowoxx.view

import de.toowoxx.controller.UserController
import de.toowoxx.model.ButtonData
import de.toowoxx.model.UserModel
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import tornadofx.*


class Editor : View("User Editor") {
    
    override val root = BorderPane()
    var nameField: TextField by singleAssign()

    var buttonTitleField: TextField by singleAssign()


    var userTable: TableView<UserModel> by singleAssign()


    val userController: UserController by inject()
    val userList = userController.userList
    var prevSelectionUser: UserModel? = null
    var prevSelectionButton: ButtonData? = null


    var buttonList = observableListOf<ButtonData>()
    var buttonTable: TableView<ButtonData> by singleAssign()


    init {
        with(root) {
            // TableView showing a list of people
            left {
                tableview(userList) {
                    userTable = this
                    column("User", UserModel::usernameProperty)

                    // Edit the currently selected person
                    selectionModel.selectedItemProperty().onChange {
                        editUser(it)
                        prevSelectionUser = it
                        if (it != null) {
                            buttonList.setAll(it.userButtons)

                        }
                        if (it != null && it.userButtons.isNotEmpty()) {
                            prevSelectionButton = it.userButtons[0]
                            buttonList.setAll(it.userButtons)

                        }
                    }
                }
            }

            center {
                center {
                    form {
                        fieldset("User bearbeiten") {
                            field("Name") {
                                textfield() {
                                    nameField = this
                                }

                            }
                            fieldset("Buttontitle") {
                                textfield() {
                                    buttonTitleField = this
                                }
                            }/*
                        field("Title") {
                            textfield() {
                                titleField = this
                            }
                        }*/

                            button("Speichern").action {
                                saveUser()
                            }
                            button("Löschen").action {
                                deleteUser()
                            }
                        }
                    }
                }
                right {
                    var buttonTable = tableview(buttonList) {
                        buttonTable = this
                        column("Buttons", ButtonData::titleProperty)
                        column("Buttons", ButtonData::commandProperty)
                        column("Buttons", ButtonData::buttonNumber)


                        // Edit the currently selected person
                        selectionModel.selectedItemProperty().onChange {
                            editButton(it)
                            prevSelectionButton = it
                        }
                    }
                }
            }
            right {

            }
            bottom {

                button("New user").action {
                    newUser()
                }
            }
        }
    }

    private fun deleteUser() {
        val deletedUser = userTable.selectedItem!!

        for (user in userList) {
            if (user.id == deletedUser.id) {
                userList.remove(deletedUser)
                break
            }
        }
        userController.saveUsersToJson("users.json", userController.dataToJsonData(userList))
    }

    private fun newUser() {
        userList.last().id
        var newUser = UserModel(userList.last().id + 1, "Neu", observableListOf())
        newUser.username = "Neu"
        userList.add(newUser)
        userController.saveUsersToJson("users.json", userController.dataToJsonData(userList))
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

    private fun editButton(button: ButtonData?) {
        if (button != null) {
            prevSelectionButton?.apply {
                titleProperty.unbindBidirectional(buttonTitleField.textProperty())
            }
            buttonTitleField.bind(button.titleProperty)
            prevSelectionButton = button
        }
    }

    private fun saveUser() {
        // Extract the selected person from the tableView
        val editedUser = userTable.selectedItem!!

        for (it in userList) {
            if (it.id == editedUser.id) {
                userList.remove(it)
                userList.add(editedUser)
                break
            }
        }

        println("Saving ${editedUser.usernameProperty} / ")
        userController.saveUsersToJson("users.json", userController.dataToJsonData(userList))
        AdminView().onRefresh()
    }

}