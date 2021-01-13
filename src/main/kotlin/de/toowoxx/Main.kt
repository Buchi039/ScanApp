package de.toowoxx

import tornadofx.launch


fun main() {
    //UserController().saveUsersToJson("users.json", UserController().generateDummyUsers())
    launch<MyApp>()
}

