package de.toowoxx.controller

import de.toowoxx.view.AdminView
import de.toowoxx.view.ScanbuttonView
import tornadofx.Controller

class MainController : Controller() {


    fun buttonClicked(text: String = "Button pressed") {
        println(text)
    }

    fun showScanbuttonView(username: String) {
        ScanbuttonView(username).openWindow()
    }

    fun showAdminView() {
        AdminView().openWindow()
    }


}