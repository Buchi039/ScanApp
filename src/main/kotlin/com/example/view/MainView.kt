package com.example.view

import com.example.Styles
import tornadofx.*

class MainView : View("Hello TornadoFX TEST") {
    override val root = hbox {
        label(title) {
            addClass(Styles.heading)
        }
    }
}
