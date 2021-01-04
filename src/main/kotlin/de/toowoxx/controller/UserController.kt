package de.toowoxx.controller

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.toowoxx.model.ButtonData
import de.toowoxx.model.UserModel
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.Controller
import tornadofx.observableListOf
import java.io.File

class UserController : Controller() {

    fun saveUserData(filePath: String, users: List<UserModel>) {

        var file = File(filePath)
        var usersJson = Gson().toJson(users)
        file.writeText(usersJson)
    }

    fun loadAllUserData(filePath: String): List<UserModel> {

        var gson = Gson()
        var file = File(filePath)
        if (file.exists()) {
            var usersJson = file.readText()

            val sType = object : TypeToken<List<UserModel>>() {}.type
            return gson.fromJson<List<UserModel>>(usersJson, sType)
        } else
            return listOf()

    }

    fun loadUserData(filepath: String, username: String?): UserModel? {
        val allUserData = loadAllUserData(filepath)
        for (user in allUserData) {
            if (user.username == username)
                return user
        }
        return null
    }

    fun generateDummyUsers(): List<UserModel> {

        var button1: ButtonData = ButtonData("/usr/bin/open -a Keka", 1, "Keka")
        var button2: ButtonData = ButtonData("/usr/bin/open -a Terminal", 2, "Terminal")
        var button3: ButtonData = ButtonData("/usr/bin/open -a CotEditor", 3, "CotEditor")

        var buttonList: ObservableList<ButtonData> = observableListOf(mutableListOf(button1, button2, button3))


        var user = UserModel("Michael", buttonList, "1")
        var user2 = UserModel("Stefan", buttonList, "2")


        return listOf(user, user2)
    }

    fun getUsernames(): ObservableList<String> {
        val users = loadAllUserData("users.json")

        var usernames = FXCollections.observableArrayList<String>()

        for (it in users) {
            usernames.add(it.username)
        }

        return usernames

    }
}