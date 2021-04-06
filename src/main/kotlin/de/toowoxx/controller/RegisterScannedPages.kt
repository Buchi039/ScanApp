package de.toowoxx.controller

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.random.Random

class RegisterScannedPages {

    /**
     * Sendet die Zahl der gescannten Seite an die scanpages.php zusammen mit der Kundennummer
     * Dient zur realisierung der Abrechnung gegenüber dem Kunden
     */
    fun register(kundenNr: String, numberOfPages: Int): Boolean {

        // Zufällige Zahl generieren für den GET-Call
        // Wird an Aufruf gehangen, damit die Funktion auf PHP-Seite jedes mal ausgeführt wird
        // Wird auf PHP Seite (vermutlich Apache Server) gecachest
        // Mit Random Zahl wird das umgangen (Zahl ansich unwichtig)
        val randomInt = Random.nextInt()

        //val phpUrl = "https://erp-pro.org/fetcher/scanpages.php" //URL zum Testen
        val phpUrl = "https://scanapp.heinlein.de/scanpages.php"
        val url = URL("$phpUrl?data=$kundenNr&z=$numberOfPages&random=$randomInt")
        var readLines = ""

        with(url.openConnection() as HttpURLConnection) {

            requestMethod = "GET"  // optional default is GET
            println("\nSent 'GET' request to URL : $url; Response Code : $responseCode")

            inputStream.bufferedReader().use {
                it.lines().forEach { line ->
                    readLines = line
                }
            }
        }


        val obj = JSONObject(readLines)
        val status = obj.getString("status")
        if (status == "true") {
            println("Gescannte Seiten: $numberOfPages -> Erfolgreich übermittelt")
            return true
        } else {
            println("HTTP übermittlung fehlgeschlagen")
            return false
        }
    }
}