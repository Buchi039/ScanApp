package de.toowoxx.view

import de.toowoxx.controller.MainController
import de.toowoxx.controller.UserController
import de.toowoxx.model.UserModel
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.layout.GridPane
import javafx.scene.text.Font
import tornadofx.*


class MainView : View() {


    val controller: MainController by inject()
    val userController: UserController by inject()
    val adminView: AdminView by inject()

    var userbuttons = GridPane()  // Gridpane mit allen Userbuttons
    var menubar = menubar()       // Menubar am oberen Rand des Fensters
    override var root: Parent = vbox() {}

    /**
     *  Baut Oberfäche der MainView
     */
    init {
        //UserController initiieren
        userController.init()

        //Menubar -> Leiste am oberen Rand des Fensters
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
        // Gridpane mit den Userbuttons erstellen und der View hinzufügen
        userbuttons = genUserButtonsGridpane(userController.userList)


        var borderp = borderpane() {
            top {
                hbox {
                    text() {
                        text = "Benutzer wählen"
                        font = Font.font(20.0)
                        alignment = Pos.CENTER
                    }
                }
            }
            center = userbuttons
        }
        root.add(borderp)
    }

    /**
     * Aktualisiert die Übersicht mit allen Userbuttons
     *
     */
    fun refreshUserbuttons() {
        userbuttons.clear()
        userbuttons.add(genUserButtonsGridpane(userController.userList))

        userbuttons.autosize()
        root.autosize()

        this.setWindowMaxSize(userbuttons.width, userbuttons.height + menubar.height + 99.0)
        this.setWindowMinSize(userbuttons.width, userbuttons.height + menubar.height + 99.0)
    }

    /**
     * Erstellt aus Liste mit Usernamen eine Liste aus Buttons für jeden User
     *
     * @param userList
     * @return
     */
    private fun generateUserButtons(userList: ObservableList<UserModel>): List<Button> {
        val buttonList = mutableListOf<Button>()
        for ((buttonCount, user) in userList.withIndex()) {
            val button = button(user.username) {
                minWidth = 100.0
                minHeight = 50.0
                // Button um die Übersicht (mit allen Scan Profilen) zu öffnen
                action {
                    controller.showScanbuttonView(user.id)
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

    /**
     * Erstellt ein TornadoFX Gridpane mit den Userbuttons aus einer Liste der Usernamen
     *
     * @param userList
     * @return
     */
    private fun genUserButtonsGridpane(userList: ObservableList<UserModel>): GridPane {
        val buttons = generateUserButtons(userList)
        val gridPane = GridPane()


        val maxCols = 8     // Anzahl der maximalen Spalten
        var rowIndex = 1    // Index der Reihen Position
        var colIndex = 1    // Index der Spaltenposition

        // Jeden Button dem generierten Gridpane hinzufügen
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
