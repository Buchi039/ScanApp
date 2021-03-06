package de.toowoxx.controller

import ConfigReader
import de.toowoxx.model.ScanProfileModel
import tornadofx.Controller
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
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

        // Datums String erstellen für Scan Dateiname
        val dateTimeFormatter = DateTimeFormatter
            .ofPattern("ddMMyy_HHmmss")
            .withZone(ZoneId.systemDefault())
            .format(Instant.now())
        val filename = "scan_$dateTimeFormatter.${model.scanFormat}"

        var napsPath = ConfigReader().readConfig("helperPath")
        napsPath += "\\App\\helper.Console.exe"


        val cmd = buildNapsCmd(napsPath, model.scanPath, filename, model.napsProfile, model.splitScan)
        println("Command: $cmd")
        return Runtime.getRuntime().exec(cmd)
    }

    fun runTestCmd(): Process? {
        Thread.sleep(5000L)
        return Runtime.getRuntime().exec("ping 8.8.8.8 -c 11")
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
        profileName: String,
        splitScan: Boolean
    ): String {
        var cmd = "$napsConsolePath -o \"$scanPath\\$filename\" -p \"$profileName\" --v"
        if (splitScan) {
            cmd += " --split"
        }
        return cmd
    }

    /**
     * Liest den Consolelog aus, nachdem der CMD ausgeführt wurde
     *
     * @param stdInput
     * @return
     */
    fun getExecLog(stdInput: InputStream): String {

        val reader = BufferedReader(InputStreamReader(stdInput))

        var output = ""
        var line: String?
        while (reader.readLine().also { line = it } != null) {

            output += "$line\n\r"

        }
        reader.close()
        return output
    }

    fun isScannerOffline(cmdOutput: String): Boolean {
        return (cmdOutput.contains("offline"))
    }

}

class StreamGobbler(var `is`: InputStream) : Thread() {
    override fun run() {
        try {
            val isr = InputStreamReader(`is`)
            val br = BufferedReader(isr)
            var line: String? = null
            while (br.readLine().also { line = it } != null) println(line)
        } catch (ioe: IOException) {
            ioe.printStackTrace()
        }

    }
}