package de.toowoxx

import de.toowoxx.controller.UserController
import tornadofx.launch
import java.io.File


fun main() {
    if (!File("users.json").exists())
        UserController().saveUsersToJson(UserController().generateDummyUsers())
    launch<MyApp>()
}

