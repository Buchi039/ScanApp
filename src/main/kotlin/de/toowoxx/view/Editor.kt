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

            /** Spalte 1 mit Auswahl des Users
             * Besteht aus Borderpane
             * Center -> Tabelview
             * Bottom -> Buttons zum Neu erstellen / löschen
             * */
            borderpane {
                center {
                    tableview(userController.userList) {
                        userTable = this
                        column("User", UserModel::usernameProperty)
                        // Edit the currently selected User
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

            /** Spalte 2 mit Auswahl des Scanprofils von User
             * * Besteht aus Borderpane
             * Center -> Tabelview mit ScanProfiles
             * Bottom -> Buttons zum Neu erstellen / löschen
             * */
            borderpane {
                center {
                    tableview(scanButtonList) {
                        scanProfileTable = this
                        column("Titel", ScanProfileModel::titleProperty)
                        column("NAPS Profile", ScanProfileModel::naspProfileProperty)
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
                        button("Neuer Scanbutton") {
                            action {
                                newScanProfile()
                            }
                        }
                        button("Profil löschen") {
                            action {
                                deleteScanButton()
                            }
                        }
                    }
                }
                paddingAll = 15
            }

            /** Spalte 3 mit Feldern zum editieren der Userdaten und Scanprofil Daten
             * Center -> Felder zum editieren der Daten
             * Bottom -> Button zum speicher der Daten
             */
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


                            /** Feld mit den Einstellungen für den Speicherort */
                            field("Pfad") {
                                textfield {
                                    scanProfileScanPathField = this
                                }
                                /** DirectoryChooser für Auswahl des Speicherorts */
                                button("Wählen") {
                                    action {
                                        chooseDirectory {
                                            directoryChooser = this
                                            initialDirectory = File("/")

                                            setOnAction { e ->
                                                val selectedDirectory = directoryChooser.showDialog(primaryStage)
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

                            /** Feld mit den Auswahl ob Icon für Button benutzt werden soll */
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

                            /** Combobox zur Auswahl welches Icon verwendet werden soll */
                            fieldset {
                                field("Auswahl") {
                                    combobox<String> {
                                        scanProfileImgCombobox = this
                                        items = mainController.getAvailableIconNames().asObservable()
                                    }
                                    scanProfileImgCombobox.setOnAction {

                                        val iconPath =
                                            mainController.getIconPath(scanProfileImgCombobox.value.toString())
                                        val imageView = ImageView(Image("file:$iconPath"))
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


                        /** Button zum speichern der veränderten Daten */
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

    /**
     * Funktion um ausgewählten User zu löschen
     *
     */
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

    /**
     * Funktion um ausgewähltes Scan Profil zu löschen
     *
     */
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

    /**
     * Funktion um neues User zu erstellen
     *
     */
    private fun newUser() {
        val newUser = UserModel()
        newUser.username = "Neu"
        newUser.id = userController.userList.last().id + 1
        userController.userList.add(newUser)
        userController.saveUsersToJson(userController.dataToJsonData(userController.userList))
        mainView.refreshUserbuttons()
    }

    /**
     * Funktion um neues ScanProfl zu erstellen
     *
     */
    private fun newScanProfile() {
        val button = ScanProfileModel()
        if (scanButtonList.isEmpty())
            button.id = 1
        else
            button.id = scanButtonList.last().id + 1
        button.napsProfile = "Neu"
        button.title = "Neu"
        button.buttonNumber = "0"
        button.imgFilename = ""
        scanButtonList.add(button)

    }

    /**
     * Setzt alle Textfelder usw. auf das zu editierende UserModel
     *
     * @param user Das zu editierende UserModel
     */
    private fun editUser(user: UserModel?) {
        //Zuerst die Felder von dem zuvor gewählten User lösen
        if (user != null) {
            prevSelectionUser?.apply {
                usernameProperty.unbindBidirectional(nameField.textProperty())
            }
            // Textfelder mit ausgewählten User verbinden
            nameField.bind(user.usernameProperty)
            // User merken
            prevSelectionUser = user
        }
    }

    /**
     * Setzt alle Textfelder usw. auf das zu editierende ScanProfile
     *
     * @param scanProfile Das zu editierende Profil
     */
    private fun editScanButton(scanProfile: ScanProfileModel?) {

        //Zuerst die Felder von dem zuvor gewählten Scanprofil lösen
        if (scanProfile != null) {
            prevSelectionScanProfile?.apply {
                titleProperty.unbindBidirectional(scanProfileTitleField.textProperty())
                naspProfileProperty.unbindBidirectional(scanProfileCommandField.textProperty())
                buttonNumberProperty.unbindBidirectional(scanProfileNumberField.textProperty())
                imgFilenameProperty.unbindBidirectional(scanProfileImgCombobox.valueProperty())
                scanPathProperty.unbindBidirectional(scanProfileScanPathField.textProperty())
            }


            // Textfelder mit ausgewählten ScanProfil verbinden
            scanProfileTitleField.bind(scanProfile.titleProperty)
            scanProfileCommandField.bind(scanProfile.naspProfileProperty)
            scanProfileNumberField.bind(scanProfile.buttonNumberProperty)
            scanProfileImgCombobox.bind(scanProfile.imgFilenameProperty)
            scanProfileImgCheckbox.bind((scanProfile.imgFilename != "").toProperty())
            scanProfileScanPathField.bind(scanProfile.scanPathProperty)

            // Scanprofil merken
            prevSelectionScanProfile = scanProfile
        }
    }

    /**
     * Speichert die User mit allen Änderungen
     *
     */
    private fun saveUser() {
        // Ausgewählten user aus der tableView holen
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
}