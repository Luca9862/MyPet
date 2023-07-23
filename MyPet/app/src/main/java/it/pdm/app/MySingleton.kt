package it.pdm.app

import java.util.*

class MySingleton {
    companion object {
        private var instance: MySingleton? = null
        fun getInstance(): MySingleton {
            if (instance == null) {
                instance = MySingleton()
            }
            return instance!!
        }
    }
    var myValue: String = "0"
    var date : Date? = null
    var resetShared: Boolean = false
    var btnStartIsClickable: Boolean = false
    var btnStopIsClickable: Boolean = false
}
