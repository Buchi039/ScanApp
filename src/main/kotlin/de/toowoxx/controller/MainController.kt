package de.toowoxx.controller

import ConfigReader
import tornadofx.Controller
import java.io.File
import java.net.InetAddress
import java.security.MessageDigest

class MainController : Controller() {


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

    /**
     * Gibt eine Liste mit den verfügbaren Dateiformaten zurück
     *
     * @return Liste mit Dateiformaten
     */
    fun getAvailableFormats(): List<String> {
        return listOf("pdf", "bmp", "emf", "exif", "gif", "jpg", "png", "tif")
    }

    /**
     * Prüft ob Hash von Hostname mit Hash aus der
     * config.properties (activationkey) überein stimmt
     * @return
     */
    fun checkLicence(): Boolean {
        val key = ConfigReader().readConfig("activationkey")
        return key == getHostnameHash()
    }

    /**
     * Generiert SHA-256 Hash aus Hostname des PCs
     *
     * @return
     */
    private fun getHostnameHash(): String? {
        // https://hashgenerator.de
        try {
            var hostname = InetAddress.getLocalHost().hostName
            return getHash(hostname)
        } catch (E: Exception) {
            System.err.println("System Name Exp : " + E.message)
        }
        return ""
    }

    fun getHash(text: String): String? {
        val bytes = MessageDigest
            .getInstance("SHA-256")
            .digest(text.toByteArray())

        return bytesToHex(bytes)
    }

    /**
     * Wandelt ByteArray in Hex-String um (Für Hash-generierung)
     *
     * @param hash
     * @return
     */
    private fun bytesToHex(hash: ByteArray): String? {
        val hexString = StringBuilder(2 * hash.size)
        for (i in hash.indices) {
            val hex = Integer.toHexString(0xff and hash[i].toInt())
            if (hex.length == 1) {
                hexString.append('0')
            }
            hexString.append(hex)
        }
        return hexString.toString()

    }
}