package de.toowoxx.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import tornadofx.*
import javax.json.JsonObject


class UserModel() : JsonModel {

    private val idProperty = SimpleIntegerProperty()
    var id by idProperty

    val usernameProperty = SimpleStringProperty()
    var username by usernameProperty


    var userButtons = FXCollections.observableArrayList<ScanProfileModel>()


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

        var buttons = mutableListOf<ScanProfileJson>()
        for (it in userButtons) {
            buttons.add(it.toScanProfileJson())
        }
        var user = UserModelJson(id, username, buttons)
        return user
    }
}

data class UserModelJson(

    var id: Int,
    var username: String,
    var userButtons: MutableList<ScanProfileJson>

) {

    fun toUserModel(): UserModel {
        var buttons = observableListOf<ScanProfileModel>()

        for (it in userButtons) {
            buttons.add(it.toScanProfileData())
        }
        var userModel = UserModel()
        userModel.id = id
        userModel.username = username
        userModel.userButtons = buttons
        return userModel
    }
}






