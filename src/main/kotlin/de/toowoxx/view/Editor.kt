package de.toowoxx.view

import de.toowoxx.controller.UserController
import de.toowoxx.model.UserModel
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import tornadofx.*


class Editor : View("Person Editor") {
    override val root = BorderPane()
    var nameField: TextField by singleAssign()
    var titleField: TextField by singleAssign()
    var personTable: TableView<UserModel> by singleAssign()

    val userController: UserController by inject()

    // Some fake data for our table
    val userList = userController.loadUsersFromJson("users.json").asObservable()

    var prevSelection: UserModel? = null

    init {
        with(root) {
            // TableView showing a list of people
            center {
                tableview(userList) {
                    personTable = this
                    column("Name", UserModel::usernameProperty)

                    // Edit the currently selected person
                    selectionModel.selectedItemProperty().onChange {
                        editPerson(it)
                        prevSelection = it
                    }
                }
            }

            right {
                form {
                    fieldset("Edit person") {
                        field("Name") {
                            textfield() {
                                nameField = this
                            }
                        }
                        field("Title") {
                            textfield() {
                                titleField = this
                            }
                        }
                        button("Save").action {
                            save()
                        }
                    }
                }
            }
        }
    }

    private fun editPerson(person: UserModel?) {
        if (person != null) {
            prevSelection?.apply {
                usernameProperty.unbindBidirectional(nameField.textProperty())
            }
            nameField.bind(person.usernameProperty)
            prevSelection = person
        }
    }

    private fun save() {
        // Extract the selected person from the tableView
        val person = personTable.selectedItem!!

        for (user in userList) {
            if (user.id == person.id) {
                userList.remove(user)
                userList.add(person)
                break
            }
        }

        println("Saving ${person.usernameProperty} / ")
        userController.saveUsersToJson("users.json", userController.dataToJsonData(userList))
    }
}