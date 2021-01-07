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
    val persons = userController.loadUsersFromJson("users.json").asObservable()

    var prevSelection: UserModel? = null

    init {
        with(root) {
            // TableView showing a list of people
            center {
                tableview(persons) {
                    personTable = this
                    //column("Name", UserModel::nameProperty)


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

        // A real application would persist the person here
        println("Saving ${person.usernameProperty} / ")
    }
}