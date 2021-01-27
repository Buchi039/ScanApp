package de.toowoxx.view

import ConfigReader
import de.toowoxx.controller.MainController
import de.toowoxx.controller.UserController
import de.toowoxx.model.ScanProfileModel
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.DirectoryChooser
import tornadofx.*
import java.io.File


class Editor : View("Editor") {
    val mainView: MainView by inject()

    val mainController: MainController by inject()
    val userController: UserController by inject()

    var scanProfileList = observableListOf<ScanProfileModel>()
    var scanProfileTable: TableView<ScanProfileModel> by singleAssign()

    var scanProfileTitleField: TextField by singleAssign()
    var scanProfileImgCheckbox: CheckBox by singleAssign()
    var scanProfileImgCombobox: ComboBox<String> by singleAssign()
    var scanProfileScanPathField: TextField by singleAssign()
    var scanProfileNAPSCombo: ComboBox<String> by singleAssign()
    var scanProfileFormatCombo: ComboBox<String> by singleAssign()

    var scanButtonFieldset = fieldset()


    var prevSelectionScanProfile: ScanProfileModel? = null

    var iconButton: Button by singleAssign()
    var iconField: Field by singleAssign()

    val iconCheckboxProperty = SimpleBooleanProperty()
    var directoryChooser: DirectoryChooser by singleAssign()

    override val root = hbox()

    init {
        scanProfileList = userController.getDefaultUser().userButtons
        with(root) {
            minHeight = 510.0

            /** Spalte 1 mit Auswahl des Scanprofils von User
             * * Besteht aus Borderpane
             * Center -> Tabelview mit ScanProfiles
             * Bottom -> Buttons zum Neu erstellen / löschen
             * */
            borderpane {
                center {
                    tableview(scanProfileList) {
                        scanProfileTable = this

                        column("Titel", ScanProfileModel::titleProperty) {
                            prefWidthProperty().bind(scanProfileTable.widthProperty().divide(2))

                        }
                        column("NAPS", ScanProfileModel::napsProfileProperty) {
                            prefWidthProperty().bind(scanProfileTable.widthProperty().divide(2))
                        }


                        selectionModel.selectedItemProperty().onChange {
                            editScanProfile(it)
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
                                /** Bei Buttonklick Confirmation Dialog anzeigen
                                 * ob Profil wirklich gelöscht werden soll
                                 */
                                alert(
                                    type = Alert.AlertType.CONFIRMATION,
                                    header = "Profil löschen",
                                    content = "Lösche Profil: ${scanProfileTable.selectionModel.selectedItem.title}",
                                    actionFn = { btnType ->
                                        if (btnType.buttonData == ButtonBar.ButtonData.OK_DONE) {
                                            deleteScanProfile()
                                            mainView.refreshView()
                                        }
                                    }
                                )
                            }
                        }
                        paddingTop = 10
                        alignment = Pos.BASELINE_RIGHT
                    }
                }
                paddingAll = 15
            }

            /** Spalte 2 mit Feldern zum editieren der Userdaten und Scanprofil Daten
             * Center -> Felder zum editieren der Daten
             * Bottom -> Button zum speicher der Daten
             */
            borderpane {
                center {
                    minWidth = 400.0
                    form {
                        fieldset("Scan Profil bearbeiten") {
                            scanButtonFieldset = this
                            field("Titel") {
                                textfield() {
                                    scanProfileTitleField = this
                                }
                            }
                            field("NAPS Profile") {
                                combobox<String> {
                                    scanProfileNAPSCombo = this
                                    items = ConfigReader().readNAPSProfiles().asObservable()
                                    prefWidth = 190.0

                                }

                                combobox<String> {
                                    scanProfileFormatCombo = this
                                    items = mainController.getAvailableFormats().asObservable()
                                    prefWidth = 70.0

                                }
                            }


                            /** Feld mit den Einstellungen für den Speicherort */
                            field("Pfad") {
                                textfield {
                                    scanProfileScanPathField = this
                                    prefWidthProperty().bind(scanProfileNAPSCombo.widthProperty())
                                }
                                /** DirectoryChooser für Auswahl des Speicherorts */
                                button("Wählen") {
                                    action {
                                        directoryChooser = DirectoryChooser()
                                        directoryChooser.initialDirectory = File(System.getProperty("user.home"))
                                        val showDialog = directoryChooser.showDialog(primaryStage)
                                        if (showDialog != null)
                                            scanProfileScanPathField.text = showDialog.absolutePath
                                    }
                                    prefWidthProperty().bind(scanProfileFormatCombo.widthProperty())
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
                                            scanProfileImgCombobox.value = "" //Wenn Haken entfernt IMG Path leeren
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
                                        prefWidthProperty().bind(scanProfileNAPSCombo.widthProperty())
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
                                    iconButton = button {
                                        alignment = Pos.BOTTOM_LEFT
                                    }
                                }
                                hiddenWhen(!iconCheckboxProperty)
                            }
                        }

                        /** Button zum speichern der veränderten Daten */
                        bottom {
                            hbox {
                                button("Speichern").action {
                                    saveChanges()
                                    mainView.refreshView()
                                }
                                button("Schließen").action {
                                    close()
                                }
                                alignment = Pos.BASELINE_RIGHT
                            }
                            paddingAll = 15
                        }
                    }
                }
            }
        }
    }


    /**
     * Funktion um ausgewähltes Scan Profil zu löschen
     *
     */
    private fun deleteScanProfile() {
        val deletedScanButton = scanProfileTable.selectedItem!!

        for (profile in scanProfileList) {
            if (profile.id == deletedScanButton.id) {
                scanProfileList.remove(deletedScanButton)
                break
            }
        }

        val selectedUser = userController.getDefaultUser()
        selectedUser.userButtons = observableListOf()
        selectedUser.userButtons.addAll(scanProfileList)

        userController.saveUsersToJson(userController.dataToJsonData(userController.userList))

  
    }


    /**
     * Funktion um neues ScanProfil zu erstellen
     *
     */
    private fun newScanProfile() {
        val profile = ScanProfileModel()
        if (scanProfileList.isEmpty())
            profile.id = 1
        else {
            // Neue (noch nicht benutzte) ID suchen
            var usedId = 0
            for (scanProfileModel in scanProfileList) {
                if (usedId < scanProfileModel.id)
                    usedId = scanProfileModel.id
            }
            profile.id = usedId + 1
        }
        profile.title = "Neu"
        profile.imgFilename = ""
        profile.scanPath = File(System.getProperty("user.home")).toString()
        profile.scanFormat = ""
        profile.napsProfile = ""
        scanProfileList.add(profile)

    }


    /**
     * Setzt alle Textfelder usw. auf das zu editierende ScanProfile
     *
     * @param scanProfile Das zu editierende Profil
     */
    private fun editScanProfile(scanProfile: ScanProfileModel?) {

        //Zuerst die Felder von dem zuvor gewählten Scanprofil lösen
        if (scanProfile != null) {
            prevSelectionScanProfile?.apply {
                titleProperty.unbindBidirectional(scanProfileTitleField.textProperty())
                scanPathProperty.unbindBidirectional(scanProfileScanPathField.textProperty())

                scanFormatProperty.unbindBidirectional(scanProfileFormatCombo.valueProperty())
                napsProfileProperty.unbindBidirectional(scanProfileNAPSCombo.valueProperty())
                imgFilenameProperty.unbindBidirectional(scanProfileImgCombobox.valueProperty())
            }


            // Textfelder mit ausgewählten ScanProfil verbinden
            scanProfileTitleField.bind(scanProfile.titleProperty)
            scanProfileScanPathField.bind(scanProfile.scanPathProperty)

            scanProfileImgCheckbox.bind((scanProfile.imgFilename != "").toProperty())

            scanProfileFormatCombo.bind(scanProfile.scanFormatProperty)
            scanProfileNAPSCombo.bind(scanProfile.napsProfileProperty)
            scanProfileImgCombobox.bind(scanProfile.imgFilenameProperty)

            // Scanprofil merken
            prevSelectionScanProfile = scanProfile
        }
    }

    /**
     * Speichert die Änderungen
     *
     */
    private fun saveChanges() {

        val editedUser = userController.getDefaultUser()
        val userAsList = observableListOf(editedUser)

        userController.saveUsersToJson(userController.dataToJsonData(userAsList))
        //mainView.refreshUserbuttons()
    }

}