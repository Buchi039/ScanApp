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
    var scanProfileSplitScan: CheckBox by singleAssign()

    var scanProfileImgCombobox: ComboBox<String> by singleAssign()
    var scanProfileScanPathField: TextField by singleAssign()
    var scanProfileNAPSCombo: ComboBox<String> by singleAssign()
    var scanProfileFormatCombo: ComboBox<String> by singleAssign()

    var scanButtonFieldset = fieldset()


    var prevSelectionScanProfile: ScanProfileModel? = null

    var iconButton: Button by singleAssign()
    var iconField: Field by singleAssign()

    val iconCheckboxProperty = SimpleBooleanProperty()
    val splitScanProperty = SimpleBooleanProperty()

    // var directoryChooser: DirectoryChooser by singleAssign()

    override val root = hbox()

    init {
        userController.init()
        scanProfileList = userController.getDefaultUser().userButtons
        with(root) {
            minHeight = 510.0

            /** Spalte 1 mit Auswahl der Scanprofile
             * * Besteht aus Borderpane
             * Center -> Tabelview mit ScanProfiles
             * Bottom -> Buttons zum Neu erstellen / löschen
             */
            borderpane {
                center {
                    minWidth = 500.0
                    tableview(scanProfileList) {
                        scanProfileTable = this

                        column("Nr", ScanProfileModel::idProperty) {
                            prefWidthProperty().bind(scanProfileTable.widthProperty().multiply(8 / 100.0))
                        }

                        column("Titel", ScanProfileModel::titleProperty) {
                            prefWidthProperty().bind(scanProfileTable.widthProperty().multiply(36 / 100.0))
                        }
                        column("Helper", ScanProfileModel::napsProfileProperty) {
                            prefWidthProperty().bind(scanProfileTable.widthProperty().multiply(32 / 100.0))
                        }
                        column("Format", ScanProfileModel::scanFormatProperty) {
                            prefWidthProperty().bind(scanProfileTable.widthProperty().multiply(24 / 100.0))
                        }

                        selectionModel.selectedItemProperty().onChange {
                            editScanProfile(it)
                            scanButtonFieldset.show()
                        }
                    }
                }
                left {
                    vbox {
                        paddingRight = 10.0
                        button() {
                            action {
                                moveScanProfileUp()
                            }
                            val imageViewUp =
                                ImageView(mainController.getImageFromResource("arrow_up.png"))

                            imageViewUp.fitHeight = 10.0
                            imageViewUp.fitWidth = 10.0
                            this.graphic = imageViewUp
                        }
                        button() {
                            action {
                                moveScanProfileDown()
                            }
                            val imageViewDown =
                                ImageView(mainController.getImageFromResource("arrow_down.png"))
                            imageViewDown.fitHeight = 10.0
                            imageViewDown.fitWidth = 10.0
                            this.graphic = imageViewDown
                        }
                    }
                }
                bottom {
                    hbox {
                        button("Neues Scanprofil") {
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

            /** Spalte 2 mit Feldern zum editieren der Scanprofil Daten
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
                            field("Helper Profile") {
                                combobox<String> {
                                    scanProfileNAPSCombo = this
                                    items = ConfigReader().readNAPSProfiles().asObservable()
                                    prefWidth = 190.0
                                }

                                combobox<String> {
                                    scanProfileFormatCombo = this
                                    items = mainController.getAvailableFormats().asObservable()
                                    prefWidth = 80.0
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
                                        var dirChooser = DirectoryChooser()
                                        dirChooser.initialDirectory = File(System.getProperty("user.home"))
                                        val showDialog = dirChooser.showDialog(primaryStage)
                                        if (showDialog != null)
                                            scanProfileScanPathField.text = showDialog.absolutePath
                                    }
                                    prefWidthProperty().bind(scanProfileFormatCombo.widthProperty())
                                }
                            }

                            /** Feld mit der Auswahl ob für jede Seite ein extra File angelegt werden soll */
                            field("Extra File pro Seite") {
                                checkbox("", splitScanProperty) {
                                    scanProfileSplitScan = this

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
                                            scanProfileImgCombobox.value = "" // Wenn Haken entfernt IMG Path leeren
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
                                        println(iconPath)
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

        scanProfileList.sortBy { it.id }
        for (i in 0 until scanProfileList.size) {
            scanProfileList[i].id = i + 1
        }
    }

    /**
     * Schiebt Profil in der Tabelle um einen Eintrag nach oben
     *
     */
    private fun moveScanProfileUp() {

        scanProfileList.sortBy { it.id }
        val profileToMove = scanProfileTable.selectedItem

        if (profileToMove != null) {
            for (i in 0 until scanProfileList.size) {
                if (profileToMove.id == scanProfileList[i].id) {
                    if (i == 0)
                        return
                    else {
                        val profileId = profileToMove.id
                        val profileSwitchId = scanProfileList[i - 1].id
                        // Setzt das Profil in der Liste um 1 nach oben
                        // Profil welches zuvor an der Stelle war kommt auf den "freien" Platz
                        scanProfileList[i - 1].id = profileId
                        scanProfileTable.selectedItem!!.id = profileSwitchId

                        // Liste nach ID sortieren
                        scanProfileList.sortBy { it.id }
                        return
                    }
                }
            }
        }
    }

    /**
     * SChiebt Profil in Tabelle um einen Eintrag nach unten
     *
     */
    private fun moveScanProfileDown() {

        scanProfileList.sortBy { it.id }
        val profileToMove = scanProfileTable.selectedItem

        if (profileToMove != null) {
            for (i in 0 until scanProfileList.size) {
                if (profileToMove.id == scanProfileList[i].id) {
                    // Profil ist schon ganz unten
                    if (i == scanProfileList.lastIndex)
                        return
                    else {
                        val profileId = profileToMove.id
                        val profileSwitchId = scanProfileList[i + 1].id
                        // Setzt das Profil in der Liste um 1 nach unten
                        // Profil welches zuvor an der Stelle war kommt auf den "freien" Platz
                        scanProfileList[i + 1].id = profileId
                        scanProfileTable.selectedItem!!.id = profileSwitchId

                        // Liste nach ID sortieren
                        scanProfileList.sortBy { it.id }
                        return
                    }
                }
            }
        }
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
        profile.splitScan = false

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
                splitScanProperty.unbindBidirectional(scanProfileSplitScan.selectedProperty())
            }


            // Textfelder mit ausgewählten ScanProfil verbinden
            scanProfileTitleField.bind(scanProfile.titleProperty)
            scanProfileScanPathField.bind(scanProfile.scanPathProperty)

            scanProfileImgCheckbox.bind((scanProfile.imgFilename != "").toProperty())

            scanProfileFormatCombo.bind(scanProfile.scanFormatProperty)
            scanProfileNAPSCombo.bind(scanProfile.napsProfileProperty)
            scanProfileImgCombobox.bind(scanProfile.imgFilenameProperty)
            scanProfileSplitScan.bind(scanProfile.splitScanProperty)


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
    }

}