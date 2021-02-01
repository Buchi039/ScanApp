package de.toowoxx.view

import de.toowoxx.controller.CommandController
import de.toowoxx.controller.MainController
import de.toowoxx.controller.UserController
import de.toowoxx.model.ScanProfileModel
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TextArea
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.text.Font
import tornadofx.*
import kotlin.math.ceil


class MainView : View() {

    val adminView: AdminView by inject()

    private val mainController: MainController by inject()
    private val cmdController: CommandController by inject()
    val userController: UserController by inject()

    var menubar = menubar()       // Menubar am oberen Rand des Fensters
    private var borderPane: BorderPane = borderpane()

    override var root: Parent = vbox {}

    // maximale Anzahl an Buttons in einer Reihe
    val maxButtonColumns = 6

    // Breite des Buttons
    val buttonWidth = 120.0

    // Höhe des Buttons
    val buttonHeight = buttonWidth

    // Abstände zwischen den Buttons
    val buttonMargin = 5.0


    private var buttonList = mutableListOf<Button>()

    /**
     *  Baut Oberfläche der MainView
     */
    init {
        title = "Scan App"
        
        // Prüfen ob Software auf dem PC aktiviert ist. Wenn nicht -> Fenster mit Nachricht
        if (!mainController.checkLicence()) {
            root.add(vbox {
                minWidth = 250.0
                maxWidth = minWidth
                minHeight = 50.0
                maxHeight = minHeight
                text {
                    text = "Software nicht aktiviert."
                    font = Font.font(20.0)
                    alignment = Pos.CENTER
                }
            })
        } else {

            //UserController initiieren
            userController.init()

            //Menubar -> Leiste am oberen Rand des Fensters
            menubar {
                menubar = this
                menu("Bearbeiten") {
                    item("Admin").action {
                        adminView.openWindow()
                    }
                    item("Beenden").action {
                        close()
                    }
                }
            }


            borderpane {
                borderPane = this
                minWidth = 500.0
                top {
                    hbox {
                        minHeight = 70.0
                        text {
                            text = "Scan-Profil auswählen"
                            font = Font.font(25.0)
                            alignment = Pos.CENTER
                        }
                    }
                }
                val buttonGridpane = genScanButtonGridpane()
                center = buttonGridpane
            }

            root.add(borderPane)
            refreshView()
        }
    }

    /**
     * Generiert ein TornadoFX Gridpane mit allen Scan Buttons für User
     *
     * @return GridPane mit allen Buttons des Users
     */
    private fun genScanButtonGridpane(): GridPane {
        val buttonGrid = gridpane() {
            alignment = Pos.CENTER
        }
        val user = userController.getDefaultUser()
        buttonList.clear()
        val maxCols = maxButtonColumns     // Anzahl der maximalen Spalten
        var rIndex = 1      // Index der Reihen Position
        var cIndex = 1      // Index der Spaltenposition
        for (it in user.userButtons) {
            val button = genScanButton(it)      // Button generieren
            buttonList.add(button)
            buttonGrid.add(button, cIndex, rIndex)      // Button dem Gridpane hinzufügen Column|Row
            cIndex++                                    // Nächster Button eine Spalte weiter rechts
            if (cIndex > maxCols) {                     // Wenn maximale Spaltenanzahl erreicht
                rIndex++                                // Nächste Reihe
                cIndex = 1                              // Und wieder erste Spalte

            }
        }
        return buttonGrid
    }

    /**
     * Generiert den Button aus dem Model
     *
     * @param scanProfileModel
     * @return  Generierter Button
     */
    private fun genScanButton(scanProfileModel: ScanProfileModel): Button {

        val button = button {

            minHeight = buttonHeight
            minWidth = buttonWidth

            maxHeight = buttonHeight
            maxWidth = buttonWidth

            // Größe der Buttons im Gridpane
            gridpaneConstraints {
                marginTopBottom(buttonMargin)
                marginLeftRight(buttonMargin)
                fillHeight = true
            }

            // Zeilenumbruch im Text
            isWrapText = true

            /** Wenn das Scanprofile ein Image hinterlegt hat wird ein Icon auf den Button gelegt */
            if (scanProfileModel.imgFilename == "") {
                text = scanProfileModel.title
            } else {
                graphic = getGraphicForButton(scanProfileModel)
            }

            action {

                val progressIndicator = ProgressIndicator()    // Wenn Button gedrückt -> Ladebalken einblenden

                text = ""   // Text ausblenden, damit nur ProgressIndicator angezeigt wird
                add(progressIndicator)

                // Alle Button bis auf den aktuellen deaktivieren
                buttonList
                    .filter { it != this }
                    .forEach { it.isDisable = true }


                var execLog = ""
                // Scan Befehl ausführen (Asynchron)
                runAsync {
                    val exec = cmdController.runScanCmd(scanProfileModel)

                    var i = 0
                    if (exec != null) {
                        while (exec.isAlive) {
                            println("Scan: ${i}s")
                            Thread.sleep(1000L)
                            i++
                        }
                        // Scan erzeugt bei Fehler einen Log
                        execLog = cmdController.getExecLog(exec.inputStream)
                    }
                } ui {
                    // Wenn Scan ausgeführt ist Log prüfen.
                    if (execLog.isEmpty()) {                   // Wenn log leer -> erfolgreicher Scan
                        progressIndicator.progress = 100.0     // Progress auf 100% setzen um Fertig-Haken anzuzeigen
                        runAsync {
                            Thread.sleep(2000L)          // Haken X ms lang anzeigen
                        } ui {
                            progressIndicator.hide()           // Nach Wartezeit ProgressIndicator ausblenden

                            // Wenn ProgressIndicator ausgeblendet wird Text bzw. Bild wieder einblenden
                            if (scanProfileModel.imgFilename != "") {
                                graphic = getGraphicForButton(scanProfileModel)
                            } else
                                text = scanProfileModel.title
                        }


                    } else {                        // Wenn log nicht leer -> Fehler ist aufgetreten
                        println(execLog)            // Log auf Console ausgeben
                        progressIndicator.hide()    // ProgressIndicator ausblenden

                        // Wenn ProgressIndicator ausgeblendet wird Text bzw. Bild wieder einblenden
                        if (scanProfileModel.imgFilename != "") {
                            graphic = getGraphicForButton(scanProfileModel)
                        } else
                            text = scanProfileModel.title

                        showErrorAlert(execLog)     // Log in einem Fenster ausgeben
                    }

                    // Alle anderen Button wieder aktivieren
                    buttonList
                        .filter { it != this }
                        .forEach { it.isDisable = false }
                }
            }
        }
        return button
    }

    /**
     * Gibt für das scanProfil die passende Graphic zurück
     *
     * @param scanProfileModel
     * @return ImageView der Graphic
     */
    private fun getGraphicForButton(scanProfileModel: ScanProfileModel): ImageView {
        val iconPath = mainController.getIconPath(scanProfileModel.imgFilename)
        val img = Image("file:$iconPath")
        val imgView = ImageView(img)
        val imgOffset = 00.0
        imgView.fitHeight = buttonHeight - imgOffset
        imgView.fitWidth = imgView.fitHeight
        return imgView
    }


    /**
     * Öffnet ein Alert-Window mit übergebener Fehlermeldung
     *
     * @param text Text der Fehlermeldung
     */
    private fun showErrorAlert(text: String) {
        val alert = Alert(Alert.AlertType.ERROR)
        alert.title = "Fehler"
        alert.headerText = "Fehler!"
        alert.contentText = "Fehler bei dem Scanvorgang"

        val textArea = TextArea(text)
        textArea.isEditable = false
        textArea.isWrapText = true

        textArea.maxWidth = Double.MAX_VALUE
        textArea.maxHeight = Double.MAX_VALUE
        GridPane.setVgrow(textArea, Priority.ALWAYS)
        GridPane.setHgrow(textArea, Priority.ALWAYS)

        val expContent = GridPane()
        expContent.maxWidth = Double.MAX_VALUE
        expContent.add(textArea, 0, 1)

        alert.dialogPane.expandableContent = expContent
        alert.dialogPane.isExpanded = true

        alert.showAndWait()
    }

    /**
     * Erneuert die MainView und passt Breite/Größe an die Anzahl der Buttons an
     *
     */
    fun refreshView() {
        borderPane.center = genScanButtonGridpane()

        val buttonCount = buttonList.size

        // Berechne wieviele Reihen mit Buttons entstehen
        val rowCount = ceil(buttonCount.toDouble() / maxButtonColumns)

        val columnCount = if (rowCount > 1) // Wenn eine ganze Reihe entsteht -> Anzahl der Spalten = maximale Anzahl
            maxButtonColumns
        else    // Wenn keine ganze Reihe ensteht -> Anzahl der Spalten = Anzahl der Button
            buttonCount

        // Anzahl der Reihen * (Buttonbreite + Buttonabstand)
        // Wenn berechnete Breite kleiner als Mindestbreite -> Mindestbreite verwenden
        if (borderPane.minWidth > columnCount * (buttonWidth + buttonMargin * 2))
            primaryStage.minWidth = borderPane.minWidth
        else
            primaryStage.minWidth = (columnCount * (buttonWidth + buttonMargin * 2)) + 100.0

        // Anzahl der Reihen * (Buttonhöhe + Buttonabstand) + Mindesthöhe
        primaryStage.minHeight = (rowCount * (buttonHeight + buttonMargin * 2)) + 160

        primaryStage.maxWidth = primaryStage.minWidth
        primaryStage.maxHeight = primaryStage.minHeight
    }

}
