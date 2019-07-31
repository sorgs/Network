package com.sorgs.sorgsnet.network.exception

/**
 * description: 异常处理.
 *
 * @author Sorgs.
 * Created date: 2019/7/30.
 */
class LocalException : Exception {
    var code: Int = 0
    var displayMessage: String? = null

    constructor(code: Int, displayMessage: String?) {
        this.code = code
        this.displayMessage = displayMessage
    }

    constructor(code: Int, message: String, displayMessage: String?) : super(message) {
        this.code = code
        this.displayMessage = displayMessage
    }


}
