package de.toowoxx.view

import de.toowoxx.controller.MainController
import de.toowoxx.controller.UserController
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.layout.GridPane
import tornadofx.*


class MainView : View() {

    val controller: MainController by inject()
    val userController: UserController by inject()
    val adminView: AdminView by inject()
    var userbuttons = GridPane()
    var menubar = menubar()
    override var root: Parent = vbox() {}


    init {

        menubar = menubar {
            menu("Bearbeiten") {
                item("Admin").action {
                    println("admin pressed")
                    adminView.openWindow()
                }
                item("Aktualisieren").action {
                    println("refresh pressed")
                    refreshUserbuttons()
                }
                item("Beenden").action {
                    close()
                }
            }
        }

        userController.init()
        userbuttons = genUserButtonsGridpane(userController.getUsernames())
        root.add(userbuttons)
    }

    fun refreshUserbuttons() {
        userbuttons.clear()
        userbuttons.add(genUserButtonsGridpane(userController.getUsernames()))

        userbuttons.autosize()
        root.autosize()

        this.setWindowMaxSize(userbuttons.width, userbuttons.height + menubar.height + 25.0)
        this.setWindowMinSize(userbuttons.width, userbuttons.height + menubar.height + 25.0)
    }

    fun generateUserButtons(userList: List<String>): List<Button> {
        val buttonList = mutableListOf<Button>()
        for ((buttonCount, user) in userList.withIndex()) {
            var button = button(user) {
                minWidth = 100.0
                minHeight = 50.0
                action {
                    controller.showScanbuttonView(user)
                }
            }.gridpaneConstraints {
                columnRowIndex(buttonCount + 1, 0)
                marginTopBottom(5.0)
                marginLeftRight(5.0)


                fillHeight = true
            }
            buttonList.add(button)
        }

        return buttonList
    }

    fun genUserButtonsGridpane(userList: List<String>): GridPane {
        val buttons = generateUserButtons(userList)
        val gridPane = GridPane()

        val maxCols = 8
        var rowIndex = 1
        var colIndex = 1
        for (button in buttons) {
            gridPane.add(button, colIndex, rowIndex)
            colIndex++
            if (colIndex > maxCols) {
                colIndex = 1
                rowIndex++
            }

        }
        return gridPane
    }

}
