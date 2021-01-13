package de.toowoxx.controller

import de.toowoxx.view.AdminView
import de.toowoxx.view.Editor
import tornadofx.Controller

class LoginController : Controller() {

    val adminView: AdminView by inject()
    val editor: Editor by inject()

    fun tryLogin(username: String, password: String) {
        runAsync {
            (username == "admin" && password == "secret") || (username == "a" && password == "a")
        } ui { successfulLogin ->

            if (successfulLogin) {
                println("Success")
                adminView.clear()
                adminView.replaceWith(Editor(), sizeToScene = true, centerOnScreen = true)

            } else {
                println("Wrong!")
            }
        }
    }

    fun showLoginScreen(message: String) {
        editor.replaceWith(adminView, sizeToScene = true, centerOnScreen = true)
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