package de.toowoxx

import de.toowoxx.controller.UserController
import tornadofx.launch


fun main() {
    val uc = UserController()
    uc.init()
    if (uc.userList.isEmpty()) {
        uc.saveUsersToJson(uc.generateDummy())
    }
    launch<MyApp>()
}

