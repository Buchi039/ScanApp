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

        val napsPath = readConfig("napsPath")

        val xmlFilePath = "$napsPath/Data/profiles.xml"
        val xmlFile = File(xmlFilePath)
        if (xmlFile.exists()) {
            val xmlStr = xmlFile.readText()
            val jsonObj = XML.toJSONObject(xmlStr)

            var arrayOfScanProfile = jsonObj.getJSONObject("ArrayOfScanProfile").getJSONArray("ScanProfile")
            val displayNames = mutableListOf<String>()
            arrayOfScanProfile.forEach { profile ->
                displayNames.add(JSONObject(profile.toString()).getString("DisplayName"))
            }
            return displayNames
        } else
            return listOf()

    }
}