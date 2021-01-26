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
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.text.Font
import tornadofx.*


class MainView : View() {

    val userController: UserController by inject()
    val adminView: AdminView by inject()
    var menubar = menubar()       // Menubar am oberen Rand des Fensters
    
    override var root: Parent = vbox() {}

    /**
     *  Baut Oberfäche der MainView
     */
    init {

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

                item("Beenden").action {
                    close()
                }
            }
        }


        val border = borderpane() {
            minWidth = 500.0
            top {
                hbox {
                    minHeight = 70.0
                    text() {
                        text = "Profil wählen"
                        font = Font.font(25.0)
                        alignment = Pos.CENTER
                    }
                }
            }
            var buttons = genScanButtonGridpane(1)
            buttons.alignment = Pos.CENTER
            center = buttons
        }

        root.add(border)


    }


    // ----------------------------------->


    private val mainController: MainController by inject()
    private val cmdController: CommandController by inject()

    private var buttonGridpane: GridPane = gridpane()

    /**
     * Generiert ein TornadoFX Gridpane mit allen Scan Buttons für User
     *
     * @param username Name des Users
     * @return GridPane mit allen Buttons des Users
     */
    private fun genScanButtonGridpane(userid: Int): GridPane {
        val buttonGrid = gridpane()
        val user = userController.getUserById(userid)

        val maxCols = 8     // Anzahl der maximalen Spalten
        var rIndex = 1      // Index der Reihen Position
        var cIndex = 1      // Index der Spaltenposition
        if (user != null) {
            for (it in user.userButtons) {
                buttonGrid.add(genScanButton(it), cIndex, rIndex)
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

        val button = button(scanProfileModel.title) {

            val buttonStackpane = stackpane()
            add(buttonStackpane)

            action {
                var pi = ProgressIndicator()    // Wenn Button gedrückt -> Ladebalken einblenden
                text = ""
                buttonStackpane.add(pi)

                buttonGridpane.isDisable = true     // Solange Scan läuft. Buttons deaktivieren


                var execLog = ""
                runAsync {
                    var exec = cmdController.runScanCmd(scanProfileModel)

                    var i = 0
                    if (exec != null) {
                        while (exec.isAlive) {
                            println("$i alive")
                            Thread.sleep(1000L)
                            i++
                        }
                        execLog = cmdController.getExecLog(exec.inputStream)
                    }
                } ui {

                    if (execLog.isEmpty()) {
                        pi.progress = 100.0
                        runAsync {
                            Thread.sleep(2000L)
                        } ui {
                            pi.hide()
                            buttonDesign(scanProfileModel, this)
                            buttonGridpane.isDisable = false
                        }
                    } else {
                        println(execLog)
                        pi.hide()
                        buttonDesign(scanProfileModel, this)
                        showErrorAlert(execLog)

                    }
                }
            }
        }.gridpaneConstraints {
            marginTopBottom(5.0)
            marginLeftRight(5.0)
            fillHeight = true
        }

        /** Wenn das Scanprofile ein Image hinterlegt hat wird ein Icon auf den Button gelegt */
        buttonDesign(scanProfileModel, button)
        return button
    }

    private fun buttonDesign(scanProfileModel: ScanProfileModel, button: Button) {

        button.minHeight = 100.0
        button.minWidth = 100.0

        button.maxHeight = 100.0
        button.maxWidth = 100.0

        button.isWrapText = true

        if (scanProfileModel.imgFilename != "") {
            val iconPath = mainController.getIconPath(scanProfileModel.imgFilename)
            val img = Image("file:$iconPath")
            val imgView = ImageView(img)
            imgView.fitHeight = 70.0
            imgView.fitWidth = 70.0
            button.graphic = imgView
            button.text = ""
        } else {
            button.text = scanProfileModel.title
        }
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


    // <-----------------------------------

}
