package de.toowoxx

import de.toowoxx.controller.UserController
import tornadofx.launch


fun main() {
    val list = UserController().generateDummyUsers()
    UserController().saveUsersToJson("users.json", list)


    println("test")
    launch<MyApp>()
}

