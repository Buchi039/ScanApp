package de.toowoxx.controller

import ConfigReader
import de.toowoxx.view.ScanbuttonView
import tornadofx.Controller
import java.io.File

class MainController : Controller() {

    /**
     * Öffnet die View mit der Übersicht aller Scan Buttons
     *
     * @param username
     */
    fun showScanbuttonView(userid: Int) {
        ScanbuttonView(userid).openWindow()
    }

    /**
     * Gibt eine Liste mit allen verfügbaren Icons für die Buttons zurück
     *
     * @return Liste mit Icon Namen
     */
    fun getAvailableIconNames(): ArrayList<String> {
        val iconNameList = arrayListOf<String>()
        val iconPath = ConfigReader().readConfig("iconPath")
        File(iconPath).walkBottomUp().forEach {
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
        return ConfigReader().readConfig("iconPath") + iconName
    }

    fun getAvailableFormats(): List<String> {
        return listOf<String>("pdf", "jpg", "png", "exif", "tif")
    }

}