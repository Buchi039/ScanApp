package de.toowoxx

import ConfigReader
import de.toowoxx.controller.UserController
import tornadofx.launch
import java.io.File


fun main() {
    ConfigReader().readNAPSProfiles()

    if (!File(ConfigReader().readConfig("userConfigPath")).exists())
        UserController().saveUsersToJson(UserController().generateDummyUsers())
    launch<MyApp>()
}

