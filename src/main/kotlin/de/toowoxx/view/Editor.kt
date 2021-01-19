package de.toowoxx.view

import de.toowoxx.controller.MainController
import de.toowoxx.controller.UserController
import de.toowoxx.model.ScanButtonModel
import de.toowoxx.model.UserModel
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.DirectoryChooser
import tornadofx.*
import java.io.File


class Editor : View("User Editor") {
    val mainController: MainController by inject()
    val mainView: MainView by inject()
    val userController: UserController by inject()


    var nameField: TextField by singleAssign()

    var scanButtonList = observableListOf<ScanButtonModel>()
    var scanButtonTable: TableView<ScanButtonModel> by singleAssign()

    var scanButtonTitleField: TextField by singleAssign()
    var scanButtonCommandField: TextField by singleAssign()
    var scanButtonNumberField: TextField by singleAssign()
    var scanButtonImgCheckbox: CheckBox by singleAssign()
    var scanButtonImgCombobox: ComboBox<String> by singleAssign()
    var scanButtonScanPathField: TextField by singleAssign()

    var scanButtonFieldset = fieldset()

    var userTable: TableView<UserModel> by singleAssign()

    var prevSelectionUser: UserModel? = null
    var prevSelectionScanButton: ScanButtonModel? = null


    var iconButton: Button by singleAssign()
    var iconField: Field by singleAssign()

    val iconCheckboxProperty = SimpleBooleanProperty()
    var directoryChooser: DirectoryChooser by singleAssign()


    override val root = hbox()


    init {
        with(root) {
            minHeight = 510.0
            borderpane {
                center {
                    tableview(userController.userList) {
                        userTable = this
                        column("User", UserModel::usernameProperty)
                        // Edit the currently selected person
                        selectionModel.selectedItemProperty().onChange {
                            //prevSelectionUser = it
                            editUser(it)
                            if (it != null)
                                scanButtonList.setAll(it.userButtons)

                            scanButtonFieldset.hide()
                        }

                    }

                    bottom {
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
                }
            }


            borderpane {
                center {
                    tableview(scanButtonList) {
                        scanButtonTable = this
                        column("Titel", ScanButtonModel::titleProperty)
                        column("Command", ScanButtonModel::commandProperty)
                        column("Nummer", ScanButtonModel::buttonNumber)


                        selectionModel.selectedItemProperty().onChange {
                            //prevSelectionScanButton = it
                            editScanButton(it)
                            scanButtonFieldset.show()
                        }
                    }
                }
                bottom {
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
                }
                paddingAll = 15
            }


            borderpane {
                center {
                    minWidth = 400.0
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

                            field("Pfad") {
                                textfield {
                                    scanButtonScanPathField = this
                                }
                                button("Wählen") {
                                    action {
                                        chooseDirectory {
                                            directoryChooser = this
                                            initialDirectory = File("src")

                                            setOnAction { e ->
                                                var selectedDirectory = directoryChooser.showDialog(primaryStage)
                                                scanButtonScanPathField.text = selectedDirectory.absolutePath
                                            }
                                        }
                                    }
                                    minWidth = 60.0
                                    alignment = Pos.BASELINE_RIGHT
                                }
                            }




                            field("Button Nr.") {
                                textfield() {
                                    scanButtonNumberField = this
                                }
                            }




                            field("Mit Icon?") {
                                checkbox("", iconCheckboxProperty) {
                                    scanButtonImgCheckbox = this

                                    action {
                                        if (isSelected)
                                            iconField.show()
                                        else {
                                            iconField.hide()
                                            scanButtonImgCombobox.value = ""
                                        }

                                    }

                                }
                            }

                            fieldset {
                                field("Auswahl") {
                                    combobox<String> {
                                        scanButtonImgCombobox = this
                                        items = mainController.getAvailableIconNames().asObservable()
                                    }
                                    scanButtonImgCombobox.setOnAction {

                                        var iconPath =
                                            mainController.getIconPath(scanButtonImgCombobox.value.toString())
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

                        val button = Button("Select Directory")
                        button.setOnAction { e ->
                            val selectedDirectory = directoryChooser.showDialog(primaryStage)
                            println(selectedDirectory.absolutePath)
                        }



                        bottom {
                            hbox {
                                button("Speichern").action {
                                    saveUser()
                                }
                            }
                            paddingAll = 15
                        }
                    }
                }
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
        var newUser = UserModel()
        newUser.username = "Neu"
        newUser.id = userController.userList.last().id + 1
        userController.userList.add(newUser)
        userController.saveUsersToJson(userController.dataToJsonData(userController.userList))
        mainView.refreshUserbuttons()
    }

    private fun newScanButton() {
        val user = userTable.selectedItem!!
        var button = ScanButtonModel()
        if (scanButtonList.isEmpty())
            button.id = 1
        else
            button.id = scanButtonList.last().id + 1
        button.command = "Neu"
        button.title = "Neu"
        button.buttonNumber = "-1"
        button.imgFilename = ""
        scanButtonList.add(button)

    }

    private fun editUser(user: UserModel?) {

        if (user != null) {
            prevSelectionUser?.apply {
                usernameProperty.unbindBidirectional(nameField.textProperty())
            }
            nameField.bind(user.usernameProperty)
            prevSelectionUser = user
        }
    }

    private fun editScanButton(scanButton: ScanButtonModel?) {
        if (scanButton != null) {
            prevSelectionScanButton?.apply {
                titleProperty.unbindBidirectional(scanButtonTitleField.textProperty())
                commandProperty.unbindBidirectional(scanButtonCommandField.textProperty())
                buttonNumberProperty.unbindBidirectional(scanButtonNumberField.textProperty())
                imgFilenameProperty.unbindBidirectional(scanButtonImgCombobox.valueProperty())
                scanPathProperty.unbindBidirectional(scanButtonScanPathField.textProperty())
            }



            scanButtonTitleField.bind(scanButton.titleProperty)
            scanButtonCommandField.bind(scanButton.commandProperty)
            scanButtonNumberField.bind(scanButton.buttonNumberProperty)
            scanButtonImgCombobox.bind(scanButton.imgFilenameProperty)
            scanButtonImgCheckbox.bind((scanButton.imgFilename != "").toProperty())
            scanButtonScanPathField.bind(scanButton.scanPathProperty)

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