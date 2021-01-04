package de.toowoxx.model

import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import tornadofx.getValue
import tornadofx.setValue

class UserModel(
    username: String, userButtons: ObservableList<ButtonData>, scanProfile: String
) {

    val usernameProperty = SimpleStringProperty(this, "username", username)
    var username by usernameProperty

    val userButtonsProperty = SimpleListProperty(this, "userButtons", userButtons)
    var userButtons by userButtonsProperty

    val scanProfileProperty = SimpleStringProperty(this, "scanProfile", scanProfile)
    var scanProfile by scanProfileProperty

}

