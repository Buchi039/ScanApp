package de.toowoxx.controller

import de.toowoxx.view.AdminView
import de.toowoxx.view.ScanbuttonView
import tornadofx.Controller
import java.io.File

class MainController : Controller() {
    val adminView: AdminView by inject()

    val iconDir = "img/"

    fun buttonClicked(text: String = "Button pressed") {
        println(text)
    }

    fun showScanbuttonView(username: String) {
        ScanbuttonView(username).openWindow()
    }


    fun getAvailableIconNames(): ArrayList<String> {
        var iconNameList = arrayListOf<String>()
        File("img/").walkBottomUp().forEach {
            if (it.isFile)
                iconNameList.add(it.name)
        }
        return iconNameList
    }

    fun getIconPath(iconName: String): String {
        return iconDir + iconName
    }

}