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


class MainView : View() {

    val adminView: AdminView by inject()

    private val mainController: MainController by inject()
    private val cmdController: CommandController by inject()
    val userController: UserController by inject()

    var menubar = menubar()       // Menubar am oberen Rand des Fensters
    private var buttonGridpane: GridPane = gridpane()
    private var borderPane: BorderPane = borderpane()

    override var root: Parent = vbox() {}

    private var buttonList = mutableListOf<Button>()

    /**
     *  Baut Oberfäche der MainView
     */
    init {

        if (!mainController.checkLicence()) {
            root.add(vbox {
                minWidth = 250.0
                minHeight = 50.0
                text {
                    text = "Software nicht aktiviert"
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
                        println("admin pressed")
                        adminView.openWindow()
                    }
                    item("Refresh").action {
                        refreshView()
                    }
                    item("Beenden").action {
                        close()
                    }
                }
            }


            borderpane() {
                borderPane = this
                minWidth = 500.0
                top {
                    hbox {
                        minHeight = 70.0
                        text() {
                            text = "Scan-Profil auswählen"
                            font = Font.font(25.0)
                            alignment = Pos.CENTER
                        }
                    }
                }
                var buttonGridpane = genScanButtonGridpane()
                center = buttonGridpane
            }

            root.add(borderPane)
        }
    }


    /**
     * Generiert ein TornadoFX Gridpane mit allen Scan Buttons für User
     *
     * @param username Name des Users
     * @return GridPane mit allen Buttons des Users
     */
    private fun genScanButtonGridpane(): GridPane {
        val buttonGrid = gridpane() {
            alignment = Pos.CENTER
        }
        val user = userController.getDefaultUser()

        val maxCols = 8     // Anzahl der maximalen Spalten
        var rIndex = 1      // Index der Reihen Position
        var cIndex = 1      // Index der Spaltenposition
        if (user != null) {
            for (it in user.userButtons) {
                var button = genScanButton(it)
                buttonList.add(button)
                buttonGrid.add(button, cIndex, rIndex)
                cIndex++
                if (cIndex > maxCols) {
                    cIndex = 1
                    rIndex++
                }
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

        val button = button() {

            /** Wenn das Scanprofile ein Image hinterlegt hat wird ein Icon auf den Button gelegt */
            minHeight = 100.0
            minWidth = 100.0

            maxHeight = 100.0
            maxWidth = 100.0

            isWrapText = true

            if (scanProfileModel.imgFilename != "") {
                graphic = getGraphicForButton(scanProfileModel)
            } else {
                text = scanProfileModel.title
            }



            action {
                var progressIndicator = ProgressIndicator()    // Wenn Button gedrückt -> Ladebalken einblenden
                text = ""       // Text ausblenden, damit nur ProgressIndicator angezeigt wird
                add(progressIndicator)

                // Alle Button bis auf den aktuellen deaktivieren
                buttonList
                    .filter { it != this }
                    .forEach { it.isDisable = true }


                var execLog = ""
                // Scan Befehl ausführen (Asynchron)
                runAsync {
                    var exec = cmdController.runScanCmd(scanProfileModel)
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
                        progressIndicator.progress = 100.0     // Progress auf 100% setzen um Haken anzuzeigen
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
        }.gridpaneConstraints {
            marginTopBottom(5.0)
            marginLeftRight(5.0)
            fillHeight = true
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
        imgView.fitHeight = 70.0
        imgView.fitWidth = 70.0
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

    fun refreshView() {
        return

        val genScanButtonGridpane = genScanButtonGridpane()
        borderPane.center = genScanButtonGridpane
        borderPane.center.autosize()


        if (borderPane.minWidth > genScanButtonGridpane.columnCount * 100.0)
            primaryStage.minWidth = borderPane.minWidth
        else
            primaryStage.minWidth = genScanButtonGridpane.columnCount * 100.0


        primaryStage.minHeight = (genScanButtonGridpane.rowCount * 100.0) + 70.0
        // primaryStage.minWidth = genScanButtonGridpane.columnCount * 100.0

        primaryStage.maxWidth = primaryStage.minWidth
        primaryStage.maxHeight = primaryStage.minHeight
    }

}
