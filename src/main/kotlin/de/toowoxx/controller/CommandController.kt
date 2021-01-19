package de.toowoxx.controller

import de.toowoxx.model.ScanProfileModel
import tornadofx.Controller
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class CommandController : Controller() {


    fun runCmd(model: ScanProfileModel) {

        if (false) {
            var dateTimeFormatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd-HH:mm:ss.SSSSSS")
                .withZone(ZoneOffset.UTC)
                .format(Instant.now())
            var filename = dateTimeFormatter.toString()

            var cmd = buildNapsCmd(model.scanPath, "test1", model.napsProfile)
            println("Command: $cmd")
            Runtime.getRuntime().exec(cmd)
        } else {
            Runtime.getRuntime().exec("/usr/bin/open -a " + "Keka")
        }
    }


    fun buildNapsCmd(scanPath: String, filename: String, profileName: String): String {

        return "NAPS2.Console.exe -o ${scanPath + "\\" + filename}.pdf -p profile1"
    }
}