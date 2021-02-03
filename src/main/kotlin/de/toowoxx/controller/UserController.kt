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

    /**
     * Speichert die Liste der User im JSON-File ab
     *
     * @param users
     */
    fun saveUsersToJson(users: MutableList<UserModelJson>) {

        val file = File(path)
        val usersJson = Gson().toJson(users)
        file.writeText(usersJson)
    }

    /**
     * Lädt alle gespeicherten User aus dem JSON File
     *
     * @param filePath
     * @return Liste mit UserModel
     */
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

    /**
     * Gibt User anhand der ID zurück
     *
     * @param userId
     * @return User mit ID userId
     */
    fun getUserById(userId: Int): UserModel {
        for (user in userList) {
            if (user.id == userId)
                return user
        }
        return UserModel()
    }

    /**
     * Gibt den default User zurück (ID = 1)
     *
     * @return
     */
    fun getDefaultUser(): UserModel {
        return getUserById(1)
    }

    /**
     * Erstellt den default User (Wird benutzt beim ersten Start der Anwendung)
     *
     * @return L
     */
    fun generateDummy(): MutableList<UserModelJson> {

        var buttonList = mutableListOf<ScanProfileJson>()
        val user = UserModelJson(1, "default", buttonList)

        return mutableListOf(user)
    }

    /**
     * Wandelt die User aus dem TornadoFX (Properties) Format in JSON um
     *
     * @param list
     * @return
     */
    fun dataToJsonData(list: ObservableList<UserModel>): MutableList<UserModelJson> {

        val userJson = mutableListOf<UserModelJson>()
        for (it in list) {
            userJson.add(it.toUserModelJson())
        }
        return userJson
    }

    /**
     * Wandelt die User vom JSON Format in das TornadoFX Format (Porperties)
     *
     * @param list
     * @return
     */
    private fun jsonDataToData(list: List<UserModelJson>): ObservableList<UserModel> {

        val userModelList = observableListOf<UserModel>()
        for (it in list) {
            userModelList.add(it.toUserModel())
        }
        return userModelList
    }

    /**
     * Initialisiert die gespeicherten Daten
     *
     */
    fun init() {
        userList = loadUsersFromJson(path).asObservable()
    }
}