package de.toowoxx.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import javax.json.JsonObject


class ScanButtonModel() : JsonModel {
    val idProperty = SimpleIntegerProperty()
    var id by idProperty

    val commandProperty = SimpleStringProperty()
    var command by commandProperty

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
            command = string("command")
            buttonNumber = string("buttonNumber")!!
            title = string("title")
            imgFilename = string("imgFilename")
            scanPath = string("scanPath")
        }
    }

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("id", id)
            add("command", command)
            add("buttonNumber", buttonNumber)
            add("title", title)
            add("imgFilename", imgFilename)
            add("scanPath", scanPath)
        }
    }

    fun toButtonDataJson(): ButtonDataJson {
        var buttonDataJson = ButtonDataJson(id, command, buttonNumber.toInt(), title, imgFilename, scanPath)
        return buttonDataJson
    }
}


data class ButtonDataJson(
    val id: Int,
    val command: String,
    val buttonNumber: Int,
    val title: String,
    val imgPath: String,
    val scanPath: String
) {

    fun toButtonData(): ScanButtonModel {
        var btn = ScanButtonModel()
        btn.id = id
        btn.command = command
        btn.buttonNumber = buttonNumber.toString()
        btn.title = title
        btn.imgFilename = imgPath
        btn.scanPath = scanPath
        return btn
    }

}