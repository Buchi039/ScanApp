import org.json.JSONException
import org.json.JSONObject
import org.json.XML
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*

class ConfigReader {

    /**
     * Liest Wert aus config.properties
     *
     * @param searchElement Bezeichnung des Werts
     * @return Der gesuchte Wert
     */
    fun readConfig(searchElement: String): String {

        try {
            FileInputStream("config/config.properties").use { input ->
                val prop = Properties()
                prop.load(input)
                return prop.getProperty(searchElement)
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            return ""
        }
    }

    /**
     * Liest das profiles.xml von NAPS2 aus und gibt alle verfügbaren ScanProfile zurück, welche in NAPS erstellt wurden
     *
     * @return Liste mit NAPS Scan-Profilen
     */
    fun readNAPSProfiles(): List<String> {

        val napsPath = readConfig("helperPath")

        // Pfad zu dem XML File, in dem die Profile von NAPS2 gespeichert sind
        val xmlFilePath = "$napsPath/Data/profiles.xml"
        val xmlFile = File(xmlFilePath)
        if (xmlFile.exists()) {
            val xmlStr = xmlFile.readText()
            val jsonObj = XML.toJSONObject(xmlStr)  // XML zu JSON umwandeln

            // JSON durchgehen um an DisplayName des NAPS Profil zu kommen
            var arrayOfScanProfile = jsonObj.getJSONObject("ArrayOfScanProfile")
            try {
                val displayNames = mutableListOf<String>()

                // Sind mehr als ein Profil gespeichert ist SCanProfile ein Array
                // Ist nur ein Profil eingerichtet ist ScanProfile ein Object
                var scanProfiles = arrayOfScanProfile.getJSONArray("ScanProfile")
                scanProfiles.forEach { profile ->
                    displayNames.add(JSONObject(profile.toString()).getString("DisplayName"))
                }
                return displayNames
                // Exception weil DisplayName kein Array ist
            } catch (ex: JSONException) {
                val displayNames = mutableListOf<String>()
                var scanProfile = arrayOfScanProfile.getJSONObject("ScanProfile")
                displayNames.add(JSONObject(scanProfile.toString()).getString("DisplayName"))
                return displayNames
            }
        } else
            return listOf()

    }
}