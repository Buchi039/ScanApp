package de.toowoxx.view

import de.toowoxx.Styles.Companion.adminView
import de.toowoxx.controller.LoginController
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class AdminView : View("Adminpanel") {

    val loginController: LoginController by inject()


    private val model = object : ViewModel() {
        val username = bind { SimpleStringProperty() }
        val password = bind { SimpleStringProperty() }
    }

    override var root = form {
        addClass(adminView)
        fieldset {
            field("Username") {
                textfield(model.username) {
                    required()
                    whenDocked { requestFocus() }
                }
            }
            field("Password") {
                passwordfield(model.password).required()
            }
            button("Login") {
                isDefaultButton = true

                action {
                    model.commit {
                        loginController.tryLogin(model.username.value, model.password.value)

                    }
                }
            }
        }
    }


    override fun onDock() {
        model.validate(decorateErrors = false)
    }

    fun clear() {
        model.username.value = ""
        model.password.value = ""
    }
}
