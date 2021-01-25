package de.toowoxx.view

import de.toowoxx.controller.CommandController
import de.toowoxx.controller.MainController
import de.toowoxx.controller.UserController
import de.toowoxx.model.ScanProfileModel
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.text.Font
import tornadofx.*


class ScanbuttonView(userid: Int) : View() {

    private val mainController: MainController by inject()
    private val userController: UserController by inject()
    private val cmdController: CommandController by inject()

    private var buttonGridpane: GridPane = gridpane()
    override var root: Parent = stackpane { }

    /**
     * Erstellt die View des Users
     */
    init {
        val border = borderpane() {
            top {
                hbox {
                    text() {
                        text = "Profil wählen"
                        font = Font.font(25.0)
                        alignment = Pos.CENTER
                    }
                }
            }
            center = genScanButtonGridpane(userid)
        }

        root.add(border)
    }

    /**
     * Generiert ein TornadoFX Gridpane mit allen Scan Buttons für User
     *
     * @param username Name des Users
     * @return GridPane mit allen Buttons des Users
     */
    private fun genScanButtonGridpane(userid: Int): GridPane {
        val buttonGrid = gridpane()
        val user = userController.getUserById(userid)

        var rIndex = 0
        var cIndex = 0
        if (user != null) {
            for (it in user.userButtons) {
                buttonGrid.add(genScanButton(it), cIndex, rIndex)
                cIndex++
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
            minHeight = 100.0
            minWidth = 100.0

            maxHeight = 100.0
            maxWidth = 100.0

            isWrapText = true

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
                            text = scanProfileModel.title
                            buttonGridpane.isDisable = false
                        }
                    } else {
                        println(execLog)
                        pi.hide()
                        text = scanProfileModel.title
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
        if (scanProfileModel.imgFilename != "") {
            val iconPath = mainController.getIconPath(scanProfileModel.imgFilename)
            val img = Image("file:$iconPath")
            val imgView = ImageView(img)
            imgView.fitHeight = 70.0
            imgView.fitWidth = 70.0
            button.graphic = imgView
            button.text = ""
        }
        return button
    }

    /**
     * Öffnet ein Alert-Window mit übergebener Fehlermeldung
     *
     * @param text Text der Fehlermeldung
     */
    private fun showErrorAlert(text: String) {
        val alert = Alert(AlertType.ERROR)
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

}
