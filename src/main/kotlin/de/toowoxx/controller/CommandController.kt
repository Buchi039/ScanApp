package de.toowoxx.controller

import tornadofx.Controller

class CommandController : Controller() {

    fun runCmd(cmd: String) {
        Runtime.getRuntime().exec(cmd)
    }
}