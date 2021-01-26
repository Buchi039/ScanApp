package de.toowoxx.controller

import ConfigReader
import de.toowoxx.model.ScanProfileModel
import tornadofx.Controller
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class CommandController : Controller() {

    /**
     * Führt den Scan aus mit dem Profil aus dem ScanProfileModel
     *
     * @param model Das Model mit den hinterlegten Scan-Einstellungen
     */
    fun runScanCmd(model: ScanProfileModel): Process? {

        /** TODO löschen */
        if (System.getProperty("os.name") == "Mac OS X") {
            return runTestCmd()

        }

        val dateTimeFormatter = DateTimeFormatter
            .ofPattern("ddMMyy_HHmmss")
            .withZone(ZoneId.systemDefault())
            .format(Instant.now())
        val filename = "scan_$dateTimeFormatter.${model.scanFormat}"
        val filename2 = "scan\$(nnnn).${model.scanFormat}"

        var napsPath = ConfigReader().readConfig("napsPath")
        napsPath += "\\App\\NAPS2.Console.exe"


        val cmd = buildNapsCmd(napsPath, model.scanPath, filename, model.napsProfile)
        println("Command: $cmd")
        return Runtime.getRuntime().exec(cmd)
    }

    fun runTestCmd(): Process? {
        return Runtime.getRuntime().exec("ping 8.8.8.8 -c 4")
    }

    /**
     * Erstellt den CMD-Befehl
     *
     * @param napsConsolePath Der Pfad zu der NAPS2.Console.exe
     * @param scanPath Der Pfad wo der Scan gespeichert werden soll
     * @param filename Der Name des Scans
     * @param profileName Der Name des NAPS2 Profil, welches für den Scan verwendet werden soll
     * @return Der ausführbare CMD String
     */
    private fun buildNapsCmd(
        napsConsolePath: String,
        scanPath: String,
        filename: String,
        profileName: String
    ): String {
        return "$napsConsolePath -o \"$scanPath\\$filename\" -p \"$profileName\""
    }

    fun getExecLog(stdInput: InputStream): String {

        val reader = BufferedReader(InputStreamReader(stdInput, Charset.forName("windows-1252")))

        var output = ""
        var line: String? = null
        while (reader.readLine().also { line = it } != null) {

            output += "$line\n\r"

        }
        return output
    }

    fun isScannerOffline(cmdOutput: String): Boolean {
        return (cmdOutput.contains("offline"))
    }
}