package com.example.view

import com.example.Styles
import tornadofx.*

class MainView : View("Hello TornadoFX 1234") {
    override val root = hbox {
        label(title) {
            addClass(Styles.heading)
        }
    }
}
