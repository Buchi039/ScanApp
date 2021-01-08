package de.toowoxx

import de.toowoxx.controller.UserController
import tornadofx.launch


fun main() {
    UserController().saveUsersToJson("users.json", UserController().generateDummyUsers())
    launch<MyApp>()
}

