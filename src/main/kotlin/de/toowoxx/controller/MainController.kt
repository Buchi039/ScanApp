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

    fun getAvailableFormats(): List<String> {
        return listOf("pdf", "jpg", "png", "exif", "tif")
    }

    fun checkLicence(): Boolean {
        val key = ConfigReader().readConfig("activationkey")
        return key == getHostnameHash()
    }


    fun getHostnameHash(): String? {

        //https://hashgenerator.de
        try {
            var hostname = InetAddress.getLocalHost().hostName
            val bytes = MessageDigest
                .getInstance("SHA-256")
                .digest(hostname.toByteArray())
            return bytesToHex(bytes)
        } catch (E: Exception) {
            System.err.println("System Name Exp : " + E.message)
        }
        return ""
    }


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