package de.toowoxx.view

import de.toowoxx.controller.CommandController
import de.toowoxx.controller.UserController
import de.toowoxx.model.ScanButtonModel
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.layout.GridPane
import tornadofx.*

class ScanbuttonView(username: String) : View() {

    val userController: UserController by inject()
    val cmdController: CommandController by inject()
    var buttonGridpane: GridPane = gridpane()


    override var root: Parent = borderpane {}

    init {
        buttonGridpane = genScanButtonGridpane(username)
        root = buttonGridpane
    }


    private fun genScanButtonGridpane(username: String?): GridPane {
        var buttonGrid = gridpane()
        val user = userController.getUserByUsername(username)

        if (user != null) {
            for (it in user.userButtons) {
                buttonGrid.add(genScanButton(it))
            }
        }
        return buttonGrid
    }

    private fun genScanButton0(buttonData: ScanButtonModel): Button {

        val button = button(buttonData.title) {
            minHeight = 50.0
            minWidth = 100.0
            action {
                println("Run Command: " + buttonData.command)
                cmdController.runCmd(buttonData.command)
            }
        }.gridpaneConstraints {
            columnRowIndex(buttonData.buttonNumber.toInt(), 0)
            marginTopBottom(5.0)
            marginLeftRight(5.0)
            fillHeight = true
        }
        return button
    }

    private fun genScanButton(scanButtonModel: ScanButtonModel): Button {

        val button = button(scanButtonModel.title) {
            minHeight = 50.0
            minWidth = 100.0
            action {
                println("Run Command: " + scanButtonModel.command)
                cmdController.runCmd(scanButtonModel.command)
            }
        }.gridpaneConstraints {
            columnRowIndex(scanButtonModel.buttonNumber.toInt(), 0)
            marginTopBottom(5.0)
            marginLeftRight(5.0)
            fillHeight = true
        }
        return button
    }
}
