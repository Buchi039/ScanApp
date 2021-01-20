package de.toowoxx.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import javax.json.JsonObject


class ScanProfileModel() : JsonModel {
    val idProperty = SimpleIntegerProperty()
    var id by idProperty

    val naspProfileProperty = SimpleStringProperty()
    var napsProfile by naspProfileProperty

    val buttonNumberProperty = SimpleStringProperty()
    var buttonNumber by buttonNumberProperty

    val titleProperty = SimpleStringProperty()
    var title by titleProperty

    val imgFilenameProperty = SimpleStringProperty()
    var imgFilename by imgFilenameProperty

    var scanPathProperty = SimpleStringProperty()
    var scanPath by scanPathProperty


    override fun updateModel(json: JsonObject) {
        with(json) {
            id = int("id")!!
            napsProfile = string("napsProfile")
            buttonNumber = string("buttonNumber")!!
            title = string("title")
            imgFilename = string("imgFilename")
            scanPath = string("scanPath")
        }
    }

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("id", id)
            add("napsProfile", napsProfile)
            add("buttonNumber", buttonNumber)
            add("title", title)
            add("imgFilename", imgFilename)
            add("scanPath", scanPath)
        }
    }

    fun toScanProfileJson(): ScanProfileJson {
        var buttonDataJson = ScanProfileJson(id, napsProfile, buttonNumber.toInt(), title, imgFilename, scanPath)
        return buttonDataJson
    }
}


data class ScanProfileJson(
    val id: Int,
    val command: String,
    val buttonNumber: Int,
    val title: String,
    val imgPath: String,
    val scanPath: String
) {

    fun toScanProfileData(): ScanProfileModel {
        var btn = ScanProfileModel()
        btn.id = id
        btn.napsProfile = command
        btn.buttonNumber = buttonNumber.toString()
        btn.title = title
        btn.imgFilename = imgPath
        btn.scanPath = scanPath
        return btn
    }

}