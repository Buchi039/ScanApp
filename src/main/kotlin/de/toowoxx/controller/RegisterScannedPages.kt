import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.random.Random

class RegisterScannedPages {

    fun register(kundenNr: String, numberOfPages: Int): Boolean {

        var randomInt = Random.nextInt()

        val url =
            URL("https://erp-pro.org/fetcher/scanpages.php?data=" + kundenNr + "&z=" + numberOfPages + "&random=" + randomInt)
        var readedLines = ""

        with(url.openConnection() as HttpURLConnection) {

            requestMethod = "GET"  // optional default is GET
            println("\nSent 'GET' request to URL : $url; Response Code : $responseCode")

            inputStream.bufferedReader().use {
                it.lines().forEach { line ->
                    readedLines = line
                }
            }
        }


        val obj = JSONObject(readedLines)
        val status = obj.getString("status")
        if (status == "true") {
            println(numberOfPages.toString() + " Erfolgreich übermittelt")
            return true
        } else {
            println("HTTP übermittlung fehlgeschlagen")
            return false
        }
    }
}