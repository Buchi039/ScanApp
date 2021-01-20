import java.io.FileInputStream
import java.io.IOException
import java.util.*

class ConfigReader {

    fun readConfig(searchElement: String):String{

        try {

            FileInputStream("config.properties").use { input ->
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
}