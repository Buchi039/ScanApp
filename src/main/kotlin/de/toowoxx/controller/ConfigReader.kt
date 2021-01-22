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
                val value = String(prop.getProperty(searchElement).toByteArray(charset("windows-1252")))
                return value
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

        val napsPath = readConfig("napsPath")

        val xmlFile = "$napsPath/Data/profiles.xml"
        val xmlStr = File(xmlFile).readText()
        val jsonObj = XML.toJSONObject(xmlStr)

        var arrayOfScanProfile = jsonObj.getJSONObject("ArrayOfScanProfile").getJSONArray("ScanProfile")
        val displayNames = mutableListOf<String>()
        arrayOfScanProfile.forEach { profile ->
            displayNames.add(JSONObject(profile.toString()).getString("DisplayName"))
        }
        return displayNames
    }
}