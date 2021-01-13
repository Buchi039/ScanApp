package de.toowoxx.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*
import javax.json.JsonObject


class UserModel(
    id: Int, username: String, userScanButtons: ObservableList<ScanButtonModel>
) : JsonModel {

    private val idProperty = SimpleIntegerProperty()
    var id by idProperty

    val usernameProperty = SimpleStringProperty()
    var username by usernameProperty


    var userButtons = FXCollections.observableArrayList<ScanButtonModel>()


    override fun updateModel(json: JsonObject) {
        with(json) {
            id = this.int("id")!!
            username = string("username")
            userButtons.setAll(getJsonArray("userButtons").toModel())
        }
    }

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("id", id)
            add("username", username)
            add("userButtons", userButtons.toJSON())
        }
    }

    fun toUserModelJson(): UserModelJson {

        var buttons = mutableListOf<ButtonDataJson>()
        for (it in userButtons) {
            buttons.add(it.toButtonDataJson())
        }
        var user = UserModelJson(id, username, buttons)
        return user
    }
}

data class UserModelJson(

    var id: Int,
    var username: String,
    var userButtons: MutableList<ButtonDataJson>

) {

    fun toUserModel(): UserModel {
        var buttons = observableListOf<ScanButtonModel>()

        for (it in userButtons) {
            buttons.add(it.toButtonData())
        }
        var userModel = UserModel(id, username, buttons)
        userModel.id = id
        userModel.username = username
        userModel.userButtons = buttons
        return userModel
    }
}






