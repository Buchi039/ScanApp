package de.toowoxx.view

import de.toowoxx.controller.MainController
import de.toowoxx.controller.UserController
import de.toowoxx.model.ScanProfileModel
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

    var scanButtonList = observableListOf<ScanProfileModel>()
    var scanProfileTable: TableView<ScanProfileModel> by singleAssign()

    var scanProfileTitleField: TextField by singleAssign()
    var scanProfileCommandField: TextField by singleAssign()
    var scanProfileNumberField: TextField by singleAssign()
    var scanProfileImgCheckbox: CheckBox by singleAssign()
    var scanProfileImgCombobox: ComboBox<String> by singleAssign()
    var scanProfileScanPathField: TextField by singleAssign()

    var scanButtonFieldset = fieldset()

    var userTable: TableView<UserModel> by singleAssign()

    var prevSelectionUser: UserModel? = null
    var prevSelectionScanProfile: ScanProfileModel? = null


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
                        scanProfileTable = this
                        column("Titel", ScanProfileModel::titleProperty)
                        column("Command", ScanProfileModel::commandProperty)
                        column("Nummer", ScanProfileModel::buttonNumber)


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
                        fieldset("Scan Profil bearbeiten") {
                            scanButtonFieldset = this
                            field("Titel") {
                                textfield() {
                                    scanProfileTitleField = this
                                }
                            }
                            field("Scan Profile") {
                                textfield() {
                                    scanProfileCommandField = this
                                }
                            }

                            field("Pfad") {
                                textfield {
                                    scanProfileScanPathField = this
                                }
                                button("Wählen") {
                                    action {
                                        chooseDirectory {
                                            directoryChooser = this
                                            initialDirectory = File("src")

                                            setOnAction { e ->
                                                var selectedDirectory = directoryChooser.showDialog(primaryStage)
                                                scanProfileScanPathField.text = selectedDirectory.absolutePath
                                            }
                                        }
                                    }
                                    minWidth = 60.0
                                    alignment = Pos.BASELINE_RIGHT
                                }
                            }




                            field("Button Nr.") {
                                textfield() {
                                    scanProfileNumberField = this
                                }
                            }




                            field("Mit Icon?") {
                                checkbox("", iconCheckboxProperty) {
                                    scanProfileImgCheckbox = this

                                    action {
                                        if (isSelected)
                                            iconField.show()
                                        else {
                                            iconField.hide()
                                            scanProfileImgCombobox.value = ""
                                        }

                                    }

                                }
                            }

                            fieldset {
                                field("Auswahl") {
                                    combobox<String> {
                                        scanProfileImgCombobox = this
                                        items = mainController.getAvailableIconNames().asObservable()
                                    }
                                    scanProfileImgCombobox.setOnAction {

                                        var iconPath =
                                            mainController.getIconPath(scanProfileImgCombobox.value.toString())
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
        val deletedScanButton = scanProfileTable.selectedItem!!

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
        prevSelectionScanProfile = null
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
        var button = ScanProfileModel()
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

    private fun editScanButton(scanProfile: ScanProfileModel?) {
        if (scanProfile != null) {
            prevSelectionScanProfile?.apply {
                titleProperty.unbindBidirectional(scanProfileTitleField.textProperty())
                commandProperty.unbindBidirectional(scanProfileCommandField.textProperty())
                buttonNumberProperty.unbindBidirectional(scanProfileNumberField.textProperty())
                imgFilenameProperty.unbindBidirectional(scanProfileImgCombobox.valueProperty())
                scanPathProperty.unbindBidirectional(scanProfileScanPathField.textProperty())
            }



            scanProfileTitleField.bind(scanProfile.titleProperty)
            scanProfileCommandField.bind(scanProfile.commandProperty)
            scanProfileNumberField.bind(scanProfile.buttonNumberProperty)
            scanProfileImgCombobox.bind(scanProfile.imgFilenameProperty)
            scanProfileImgCheckbox.bind((scanProfile.imgFilename != "").toProperty())
            scanProfileScanPathField.bind(scanProfile.scanPathProperty)

            prevSelectionScanProfile = scanProfile
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
        scanProfileTitleField.clear()
        scanProfileNumberField.clear()
        scanProfileCommandField.clear()
    }

}