package de.toowoxx.controller

import ConfigReader
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.toowoxx.model.ScanProfileJson
import de.toowoxx.model.UserModel
import de.toowoxx.model.UserModelJson
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.Controller
import tornadofx.asObservable
import tornadofx.observableListOf
import java.io.File

class UserController : Controller() {

    private val path = ConfigReader().readConfig("userConfigPath")
    var userList = observableListOf<UserModel>()

    fun saveUsersToJson(users: MutableList<UserModelJson>) {

        val file = File(path)
        val usersJson = Gson().toJson(users)
        file.writeText(usersJson)
    }

    private fun loadUsersFromJson(filePath: String = path): List<UserModel> {

        val gson = Gson()
        val file = File(filePath)
        return if (file.exists()) {
            val usersJson = file.readText()

            val sType = object : TypeToken<List<UserModelJson>>() {}.type
            val list = gson.fromJson<List<UserModelJson>>(usersJson, sType)
            jsonDataToData(list)
        } else
            listOf()
    }

    fun getUserByUsername(username: String?): UserModel? {

        for (user in userList) {
            if (user.username == username)
                return user
        }
        return null
    }

    fun generateDummyUsers(): MutableList<UserModelJson> {

        val button1 = ScanProfileJson(1, "Keka", 1, "Keka", "save.png", "scans/")
        val button2 = ScanProfileJson(2, "Terminal", 2, "Terminal", "", "scans/")
        val button3 = ScanProfileJson(3, "CotEditor", 3, "CotEditor", "testicon.png", "scans/")


        var buttonList = mutableListOf(button1, button2, button3)
        val user = UserModelJson(1, "Michael", buttonList)

        buttonList = mutableListOf(button2, button3)
        val user2 = UserModelJson(2, "Stefan", buttonList)

        return mutableListOf(user, user2)
    }

    fun getUsernames(): ObservableList<String> {

        val usernames = FXCollections.observableArrayList<String>()
        for (it in loadUsersFromJson()) {
            usernames.add(it.username)
        }
        return usernames
    }

    fun dataToJsonData(list: ObservableList<UserModel>): MutableList<UserModelJson> {

        val userJson = mutableListOf<UserModelJson>()
        for (it in list) {
            userJson.add(it.toUserModelJson())
        }
        return userJson
    }

    private fun jsonDataToData(list: List<UserModelJson>): ObservableList<UserModel> {

        val userModelList = observableListOf<UserModel>()
        for (it in list) {
            userModelList.add(it.toUserModel())
        }
        return userModelList
    }

    fun init() {
        userList = loadUsersFromJson(path).asObservable()
    }
}