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
    override var root: Parent = vbox() {}

    init {
        root.add(button("ADMIN") {
            action {
                println("admin pressed")
                controller.showAdminView()
            }
            minWidth = 100.0

        }.vboxConstraints {
            marginTopBottom(5.0)
            marginLeftRight(5.0)
        })
        root.add(generateUserButtonsGridpane(userController.getUsernames()))
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

    fun generateUserButtonsGridpane(userList: List<String>): GridPane {
        val buttons = generateUserButtons(userList)
        val gridPane = GridPane()

        for (button in buttons) {
            gridPane.add(button)
        }
        return gridPane
    }

}
