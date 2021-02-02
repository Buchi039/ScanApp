package de.toowoxx.controller

import de.toowoxx.view.AdminView
import de.toowoxx.view.Editor
import tornadofx.Controller
import tornadofx.runLater

class LoginController : Controller() {

    val adminView: AdminView by inject()
    val editor: Editor by inject()
    val mainController: MainController by inject()

    fun tryLogin(username: String, password: String) {


        val hash = mainController.getHash("${username}:${password}")


        runAsync {
            // username|password
            // admin:secret!
            (hash == "c7a7904021ef433b756fed358166d963eaa5b6b44e3964b00d0de073369d2050")

        } ui { successfulLogin ->

            if (successfulLogin) {
                adminView.clearLogin()
                adminView.replaceWith(Editor(), sizeToScene = true, centerOnScreen = true)

            } else {
                showLoginScreen("Login failed. Please try again.", true)
            }
        }
    }

    fun showLoginScreen(message: String, shake: Boolean = false) {
        editor.replaceWith(adminView, sizeToScene = true, centerOnScreen = true)
        runLater {
            if (shake) adminView.shakeStage()
        }
    }

    fun logout() {
        with(config) {
            remove(USERNAME)
            remove(PASSWORD)
            save()
        }
        showLoginScreen("Log in as another user")
    }

    companion object {
        val USERNAME = "username"
        val PASSWORD = "password"
    }

}