package de.toowoxx.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import javax.json.JsonObject


class ButtonData(
    id: Int,
    command: String,
    buttonNumber: Int,
    title: String
) : JsonModel {
    val idProperty = SimpleIntegerProperty()
    var id by idProperty

    val commandProperty = SimpleStringProperty()
    var command by commandProperty

    val buttonNumberProperty = SimpleIntegerProperty()
    var buttonNumber by buttonNumberProperty

    val titleProperty = SimpleStringProperty()
    var title by titleProperty


    override fun updateModel(json: JsonObject) {
        with(json) {
            id = int("id")!!
            command = string("command")
            buttonNumber = int("buttonNumber")!!
            title = string("title")
        }
    }

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("id", id)
            add("command", command)
            add("buttonNumber", buttonNumber)
            add("title", title)
        }
    }

    fun toButtonDataJson(): ButtonDataJson {
        var buttonDataJson = ButtonDataJson(id, command, buttonNumber, title)
        return buttonDataJson
    }
}


data class ButtonDataJson(
    val id: Int,
    val command: String,
    val buttonNumber: Int,
    val title: String
) {

    fun toButtonData(): ButtonData {
        var btn = ButtonData(id, command, buttonNumber, title)
        btn.id = id
        btn.command = command
        btn.buttonNumber = buttonNumber
        btn.title = title
        return btn
    }

}