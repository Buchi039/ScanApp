package de.toowoxx.model

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import javax.json.JsonObject


class ScanProfileModel() : JsonModel {
    val idProperty = SimpleIntegerProperty()
    var id by idProperty

    val napsProfileProperty = SimpleStringProperty()
    var napsProfile by napsProfileProperty

    val titleProperty = SimpleStringProperty()
    var title by titleProperty

    val imgFilenameProperty = SimpleStringProperty()
    var imgFilename by imgFilenameProperty

    var scanPathProperty = SimpleStringProperty()
    var scanPath by scanPathProperty

    val scanFormatProperty = SimpleStringProperty()
    var scanFormat by scanFormatProperty

    val splitScanProperty = SimpleBooleanProperty()
    var splitScan by splitScanProperty


    override fun updateModel(json: JsonObject) {
        with(json) {
            id = int("id")!!
            napsProfile = string("napsProfile")
            title = string("title")
            imgFilename = string("imgFilename")
            scanPath = string("scanPath")
            scanFormat = string("stringFormat")
            splitScan = boolean("splitScan") == true
        }
    }

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("id", id)
            add("napsProfile", napsProfile)
            add("title", title)
            add("imgFilename", imgFilename)
            add("scanPath", scanPath)
            add("scanFormat", scanFormat)
            add("splitScan", splitScan)
        }
    }

    fun toScanProfileJson(): ScanProfileJson {
        var buttonDataJson =
            ScanProfileJson(id, napsProfile, title, imgFilename, scanPath, scanFormat, splitScan)
        return buttonDataJson
    }
}


data class ScanProfileJson(
    val id: Int,
    val napsProfile: String,
    val title: String,
    val imgPath: String,
    val scanPath: String,
    val scanFormat: String,
    val splitScan: Boolean
) {

    fun toScanProfileData(): ScanProfileModel {
        var profile = ScanProfileModel()
        profile.id = id
        profile.napsProfile = napsProfile
        profile.title = title
        profile.imgFilename = imgPath
        profile.scanPath = scanPath
        profile.scanFormat = scanFormat
        profile.splitScan = splitScan
        return profile
    }

}