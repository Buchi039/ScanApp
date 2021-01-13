package de.toowoxx.controller

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.toowoxx.model.ButtonDataJson
import de.toowoxx.model.UserModel
import de.toowoxx.model.UserModelJson
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.Controller
import tornadofx.asObservable
import tornadofx.observableListOf
import java.io.File

class UserController : Controller() {

    private val path = "users.json"
    var userList = observableListOf<UserModel>()

    fun saveUsersToJson(filePath: String, users: MutableList<UserModelJson>) {
        val file = File(filePath)
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

        val button1 = ButtonDataJson(1, "Keka", 1, "Keka")
        val button2 = ButtonDataJson(2, "Terminal", 2, "Terminal")
        val button3 = ButtonDataJson(3, "CotEditor", 3, "CotEditor")


        var buttonList = mutableListOf(button1, button2, button3)
        val user = UserModelJson(1, "Michael", buttonList)

        buttonList = mutableListOf(button2, button3)
        val user2 = UserModelJson(2, "Stefan", buttonList)

        return mutableListOf(user, user2)
    }


    fun getUsernames(): ObservableList<String> {
        val usernames = FXCollections.observableArrayList<String>()
        for (it in userList) {
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