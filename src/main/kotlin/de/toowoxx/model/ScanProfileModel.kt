package de.toowoxx.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import javax.json.JsonObject


class ScanProfileModel() : JsonModel {
    val idProperty = SimpleIntegerProperty()
    var id by idProperty

    val napsProfileProperty = SimpleStringProperty()
    var napsProfile by napsProfileProperty

    val buttonNumberProperty = SimpleStringProperty()
    var buttonNumber by buttonNumberProperty

    val titleProperty = SimpleStringProperty()
    var title by titleProperty

    val imgFilenameProperty = SimpleStringProperty()
    var imgFilename by imgFilenameProperty

    var scanPathProperty = SimpleStringProperty()
    var scanPath by scanPathProperty

    val scanFormatProperty = SimpleStringProperty()
    var scanFormat by scanFormatProperty


    override fun updateModel(json: JsonObject) {
        with(json) {
            id = int("id")!!
            napsProfile = string("napsProfile")
            buttonNumber = string("buttonNumber")!!
            title = string("title")
            imgFilename = string("imgFilename")
            scanPath = string("scanPath")
            scanFormat = string("stringFormat")
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
            add("scanFormat", scanFormat)
        }
    }

    fun toScanProfileJson(): ScanProfileJson {
        var buttonDataJson =
            ScanProfileJson(id, napsProfile, buttonNumber.toInt(), title, imgFilename, scanPath, scanFormat)
        return buttonDataJson
    }
}


data class ScanProfileJson(
    val id: Int,
    val napsProfile: String,
    val buttonNumber: Int,
    val title: String,
    val imgPath: String,
    val scanPath: String,
    val scanFormat: String
) {

    fun toScanProfileData(): ScanProfileModel {
        var profile = ScanProfileModel()
        profile.id = id
        profile.napsProfile = napsProfile
        profile.buttonNumber = buttonNumber.toString()
        profile.title = title
        profile.imgFilename = imgPath
        profile.scanPath = scanPath
        profile.scanFormat = scanFormat
        return profile
    }

}