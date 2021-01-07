package de.toowoxx.controller

import de.toowoxx.view.AdminView
import de.toowoxx.view.Editor
import de.toowoxx.view.SecureScreen
import tornadofx.Controller

class LoginController : Controller() {

    val loginScreen: AdminView by inject()
    val secureScreen: SecureScreen by inject()
    val editor: Editor by inject()


    fun tryLogin(username: String, password: String) {
        runAsync {
            (username == "admin" && password == "secret") || (username == "a" && password == "a")
        } ui { successfulLogin ->

            if (successfulLogin) {
                println("Success")
                loginScreen.clear()
                //secureScreen.openModal()
                editor.openModal()


            } else {
                println("Wrong!")
            }
        }
    }


    fun tryLogin2(username: String, password: String): Boolean {
        return (username == "admin" && password == "secret")
    }

    fun showSecureScreen() {
        loginScreen.replaceWith(secureScreen, sizeToScene = true, centerOnScreen = true)
    }

    fun showLoginScreen(message: String) {
        secureScreen.replaceWith(loginScreen, sizeToScene = true, centerOnScreen = true)
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