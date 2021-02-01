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
            // admin:secret || a:a
            (hash == "9ae6a73ab9effb8f26998120906d2fc4be644483d5d6c2ccbb4b6df70e2ee623") || (hash == "901b281c4e0c4007e8526ef27153b79330811e733976d5e65c8343a39e54ec81")

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