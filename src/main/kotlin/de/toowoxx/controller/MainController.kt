package de.toowoxx.controller

import de.toowoxx.view.ScanbuttonView
import tornadofx.Controller
import java.io.File

class MainController : Controller() {

    val iconDir = "img/"

    /**
     * Öffnet die View mit der Übersicht aller Scan Buttons
     *
     * @param username
     */
    fun showScanbuttonView(username: String) {
        ScanbuttonView(username).openWindow()
    }

    /**
     * Gibt eine Liste mit allen verfügbaren Icons für die Buttons zurück
     *
     * @return Liste mit Icon Namen
     */
    fun getAvailableIconNames(): ArrayList<String> {
        var iconNameList = arrayListOf<String>()
        File("img/").walkBottomUp().forEach {
            if (it.isFile)
                iconNameList.add(it.name)
        }
        return iconNameList
    }

    /**
     * Gibt den Pfad zu dem Icon-File zurück
     *
     * @param iconName
     * @return  Pfad zu dem File (iconName)
     */
    fun getIconPath(iconName: String): String {
        return iconDir + iconName
    }

}