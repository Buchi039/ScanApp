package de.toowoxx.controller

import ConfigReader
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.toowoxx.model.ScanProfileJson
import de.toowoxx.model.UserModel
import de.toowoxx.model.UserModelJson
import javafx.collections.ObservableList
import tornadofx.Controller
import tornadofx.asObservable
import tornadofx.observableListOf
import java.io.File

class UserController : Controller() {

    private val path = ConfigReader().readConfig("scanAppProfiles")
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


    fun getUserById(userId: Int): UserModel {
        for (user in userList) {
            if (user.id == userId)
                return user
        }
        return UserModel()
    }

    fun getDefaultUser(): UserModel {
        return getUserById(1)
    }

    fun generateDummy(): MutableList<UserModelJson> {

        var buttonList = mutableListOf<ScanProfileJson>()
        val user = UserModelJson(1, "default", buttonList)

        return mutableListOf(user)
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