package de.toowoxx.view

import de.toowoxx.controller.CommandController
import de.toowoxx.controller.MainController
import de.toowoxx.controller.UserController
import de.toowoxx.model.ScanProfileModel
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane
import tornadofx.*

class ScanbuttonView(username: String) : View() {

    private val mainController: MainController by inject()
    private val userController: UserController by inject()
    private val cmdController: CommandController by inject()


    private var buttonGridpane: GridPane = gridpane()
    override var root: Parent = borderpane {}

    /**
     * Erstellt die View des Users
     */
    init {
        buttonGridpane = genScanButtonGridpane(username)
        root = buttonGridpane
    }

    /**
     * Generiert ein TornadoFX Gridpane mit allen Scan Buttons für User
     *
     * @param username Name des Users
     * @return GridPane mit allen Buttons des Users
     */
    private fun genScanButtonGridpane(username: String?): GridPane {
        val buttonGrid = gridpane()
        val user = userController.getUserByUsername(username)

        if (user != null) {
            for (it in user.userButtons) {
                buttonGrid.add(genScanButton(it))
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

            action {
                cmdController.runScanCmd(scanProfileModel)
            }
        }.gridpaneConstraints {
            columnRowIndex(scanProfileModel.buttonNumber.toInt(), 0)
            marginTopBottom(5.0)
            marginLeftRight(5.0)
            fillHeight = true
        }

        /** Wenn das Sconprofile ein Image hinterlegt hat wird ein Icon auf den Button gelegt */
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
}
