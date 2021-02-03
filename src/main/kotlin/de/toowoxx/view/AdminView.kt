package de.toowoxx.view

import de.toowoxx.Styles.Companion.adminView
import de.toowoxx.controller.LoginController
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import javafx.util.Duration
import tornadofx.*

class AdminView : View("Login") {

    private val loginController: LoginController by inject()


    private val logindata = object : ViewModel() {
        val username = bind { SimpleStringProperty() }
        val password = bind { SimpleStringProperty() }
    }

    // Login Fenster erstellen
    override var root = form {
        addClass(adminView)
        fieldset {
            field("Username") {
                textfield(logindata.username) {
                    required()
                    whenDocked { requestFocus() }
                }
            }
            field("Password") {
                passwordfield(logindata.password) {
                    required()
                }
            }
            button("Login") {
                isDefaultButton = true

                action {    // Bei Button Click versuch mit Daten aus logindata einzuloggen
                    logindata.commit {
                        loginController.tryLogin(logindata.username.value, logindata.password.value)
                    }
                }
            }
        }
    }


    override fun onDock() {
        logindata.validate(decorateErrors = false)
    }


    // Funktion für Schütteleffekt des Fensters, wenn Login falsch
    fun shakeStage() {
        var x = 0
        var y = 0
        val cycleCount = 10
        val move = 10
        val keyframeDuration = Duration.seconds(0.04)

        val stage = FX.primaryStage

        val timelineX = Timeline(KeyFrame(keyframeDuration, EventHandler {
            if (x == 0) {
                stage.x = stage.x + move
                x = 1
            } else {
                stage.x = stage.x - move
                x = 0
            }
        }))

        timelineX.cycleCount = cycleCount
        timelineX.isAutoReverse = false

        val timelineY = Timeline(KeyFrame(keyframeDuration, EventHandler {
            if (y == 0) {
                stage.y = stage.y + move
                y = 1
            } else {
                stage.y = stage.y - move
                y = 0
            }
        }))

        timelineY.cycleCount = cycleCount
        timelineY.isAutoReverse = false

        timelineX.play()
        timelineY.play()
    }

    // Username und Password Feld zurücksetzen
    fun clearLogin() {
        logindata.username.value = ""
        logindata.password.value = ""
    }
}
