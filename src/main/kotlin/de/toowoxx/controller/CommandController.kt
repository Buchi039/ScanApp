package de.toowoxx.controller

import ConfigReader
import de.toowoxx.model.ScanProfileModel
import tornadofx.Controller
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class CommandController : Controller() {

    /**
     * Führt den Scan aus mit dem Profil aus dem ScanProfileModel
     *
     * @param model Das Model mit den hinterlegten Scan-Einstellungen
     */
    fun runScanCmd(model: ScanProfileModel) {

        /** TODO löschen */
        if (System.getProperty("os.name") == "Mac OS X") {
            runTestCmd()
            return
        }

        val dateTimeFormatter = DateTimeFormatter
            .ofPattern("ddMMyy_HHmmss")
            .withZone(ZoneId.systemDefault())
            .format(Instant.now())
        val filename = "scan_$dateTimeFormatter.${model.scanFormat}"

        val napsPath = ConfigReader().readConfig("napsPath")

        val cmd = buildNapsCmd(napsPath, model.scanPath, filename, model.napsProfile)
        println("Command: $cmd")
        Runtime.getRuntime().exec(cmd)

    }

    fun runTestCmd() {
        Runtime.getRuntime().exec("/usr/bin/open -a " + "Keka")
    }

    /**
     * Erstellt den CMD-Befehl
     *
     * @param napsPath Der Pfad zu der NAPS2.Console.exe
     * @param scanPath Der Pfad wo der Scan gespeichert werden soll
     * @param filename Der Name des Scans
     * @param profileName Der Name des NAPS2 Profil, welches für den Scan verwendet werden soll
     * @return Der ausführbare CMD String
     */
    private fun buildNapsCmd(
        napsPath: String,
        scanPath: String,
        filename: String,
        profileName: String
    ): String {
        return "$napsPath\\NAPS2.Console.exe -o $scanPath\\$filename -p $profileName"
    }
}