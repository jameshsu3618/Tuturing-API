package com.tuturing.api.shared.string

fun String.toPnrString(): String {
    var formattedString = this.transliterate()
    formattedString = pnrSpecialCharsRegex.replace(formattedString, "") // Remove unwanted special chars
    formattedString = whiteSpaceRegex.replace(formattedString, "") // Remove white space
    formattedString = formattedString.trim()
    return formattedString
}

private val pnrSpecialCharsRegex = Regex("[^A-Za-z0-9 _\\@\\.‡\\-]")
private val whiteSpaceRegex = Regex("\\s+")
