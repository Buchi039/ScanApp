package de.toowoxx.view

import de.toowoxx.controller.CommandController
import de.toowoxx.controller.MainController
import de.toowoxx.controller.UserController
import de.toowoxx.model.ScanProfileModel
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TextArea
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font
import tornadofx.*
import kotlin.math.ceil


class MainView : View() {

    private val adminView: AdminView by inject()

    private val mainCtrl: MainController by inject()
    private val cmdCtrl: CommandController by inject()
    private val userCtrl: UserController by inject()

    private var borderPane: BorderPane = borderpane()

    override var root: Parent = vbox {}

    // maximale Anzahl an Buttons in einer Reihe
    private val maxButtonColumns = 6

    // Breite des Buttons
    private val buttonWidth = 140.0

    // Höhe des Buttons
    private val buttonHeight = buttonWidth

    // Abstände zwischen den Buttons
    private val buttonMargin = 5.0

    private var buttonList = mutableListOf<Button>()


    /**
     *  Baut Oberfläche der MainView
     */
    init {
        title = "Scan App"

        // Setzt das Icon der Anwendung
        FX.primaryStage.icons += mainCtrl.getImageFromResource("icon_print.png")

        // Prüfen ob Software auf dem PC aktiviert ist. Wenn nicht -> Fenster mit Nachricht
        if (!mainCtrl.checkLicence()) {
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
            userCtrl.init()

            //Menubar -> Leiste am oberen Rand des Fensters
            menubar {
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


                //this.style = "-fx-background-color: #FFFFFF;"
                background = Background(BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY))

                top {
                    vbox {
                        minHeight = 70.0

                        hbox {


                            val imageView =
                                ImageView(mainCtrl.getImageFromResource("diesystempartner_logo.jpg"))

                            imageView.fitHeight = 100.0
                            imageView.isPreserveRatio = true

                            alignment = Pos.CENTER
                            paddingTop = 15.0
                            paddingBottom = 45.0
                            this.add(imageView)


                        }

                        text {
                            text = "Scan-Profil auswählen"
                            font = Font.font(25.0)
                            alignment = Pos.CENTER
                        }
                    }
                }
                val buttonGridpane = genScanButtonGridpane()
                center = buttonGridpane

                bottom {
                    background = Background(BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY))
                }

            }

            root.add(borderPane)
            //refreshView()
        }
    }


    /**
     * Generiert ein TornadoFX Gridpane mit allen Scan Buttons für User
     *
     * @return GridPane mit allen Buttons des Users
     */
    private fun genScanButtonGridpane(): GridPane {
        val padding = 20.0
        val buttonGrid = gridpane() {
            alignment = Pos.CENTER
            paddingBottom = padding
            paddingLeft = padding
            paddingRight = padding
        }
        val user = userCtrl.getDefaultUser()
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
                    val exec = cmdCtrl.runScanCmd(scanProfileModel)

                    var i = 0
                    if (exec != null) {
                        while (exec.isAlive) {
                            println("Scan: ${i}s")
                            Thread.sleep(1000L)
                            i++
                        }
                        // Scan erzeugt bei Fehler einen Log
                        execLog = cmdCtrl.getExecLog(exec.inputStream)
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

                        // Prüfen ob im Log die Meldung steht, dass in der Zuführung keine Seiten
                        // vorhanden sind
                        if (execLog.contains("keine Seiten")) {
                            showInfoZufuehrungLeer()  // Alert mit Meldung anzeigen
                        } else if (execLog.contains("page(s) scanned")) {
                            var seiten = 0
                            execLog.lines().forEach { string ->
                                if (string.contains("page(s) scanned")) {
                                    seiten = string.replace("[^0-9]".toRegex(), "").toInt()
                                }
                            }
                            println("GESCANNTE SEITEN $seiten")
                            mainCtrl.addNumberOfScannedPages(seiten)
                        } else {
                            showErrorAlert(execLog)     // Log in einem Fenster ausgeben
                        }


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
        val iconPath = mainCtrl.getIconPath(scanProfileModel.imgFilename)
        val img = Image("file:$iconPath")
        val imgView = ImageView(img)
        val imgMargin = 20.0
        imgView.fitHeight = buttonHeight - imgMargin
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

    private fun showInfoZufuehrungLeer() {
        val alert = Alert(Alert.AlertType.WARNING)

        alert.title = "Fehler beim Scanvorgang"
        alert.headerText = ""
        alert.contentText = "Papiereinzug ist leer, bitte legen Sie die Dokumente in den Papiereinzug."

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
            borderPane.minWidth = borderPane.minWidth
        else
            borderPane.minWidth = (columnCount * (buttonWidth + buttonMargin * 2)) + 100.0

        // Anzahl der Reihen * (Buttonhöhe + Buttonabstand) + Mindesthöhe + Logo (Höhe + Padding)
        borderPane.minHeight = (rowCount * (buttonHeight + buttonMargin * 2)) + 200 + 160.0

        borderPane.maxWidth = borderPane.minWidth
        borderPane.maxHeight = borderPane.minHeight

        primaryStage.minWidth = borderPane.minWidth
        primaryStage.minHeight = borderPane.minHeight

        primaryStage.maxWidth = borderPane.maxWidth
        primaryStage.maxHeight = borderPane.maxHeight


    }

}
