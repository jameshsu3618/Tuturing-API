package com.tuturing.api.shared.string

fun String.transliterate(): String {
    val charMap = mapOf(
            "á" to "a",
            "Á" to "A",
            "à" to "a",
            "À" to "A",
            "ă" to "a",
            "Ă" to "A",
            "â" to "a",
            "Â" to "A",
            "å" to "a",
            "Å" to "A",
            "ã" to "a",
            "Ã" to "A",
            "ą" to "a",
            "Ą" to "A",
            "ā" to "a",
            "Ā" to "A",
            "ä" to "ae",
            "Ä" to "AE",
            "æ" to "ae",
            "Æ" to "AE",
            "ḃ" to "b",
            "Ḃ" to "B",
            "ć" to "c",
            "Ć" to "C",
            "ĉ" to "c",
            "Ĉ" to "C",
            "č" to "c",
            "Č" to "C",
            "ċ" to "c",
            "Ċ" to "C",
            "ç" to "c",
            "Ç" to "C",
            "ď" to "d",
            "Ď" to "D",
            "ḋ" to "d",
            "Ḋ" to "D",
            "đ" to "d",
            "Đ" to "D",
            "ð" to "dh",
            "Ð" to "Dh",
            "é" to "e",
            "É" to "E",
            "è" to "e",
            "È" to "E",
            "ĕ" to "e",
            "Ĕ" to "E",
            "ê" to "e",
            "Ê" to "E",
            "ě" to "e",
            "Ě" to "E",
            "ë" to "e",
            "Ë" to "E",
            "ė" to "e",
            "Ė" to "E",
            "ę" to "e",
            "Ę" to "E",
            "ē" to "e",
            "Ē" to "E",
            "ﬀ" to "ff",
            "ḟ" to "f",
            "Ḟ" to "F",
            "ƒ" to "f",
            "Ƒ" to "F",
            "ğ" to "g",
            "Ğ" to "G",
            "ĝ" to "g",
            "Ĝ" to "G",
            "ġ" to "g",
            "Ġ" to "G",
            "ģ" to "g",
            "Ģ" to "G",
            "ĥ" to "h",
            "Ĥ" to "H",
            "ħ" to "h",
            "Ħ" to "H",
            "í" to "i",
            "Í" to "I",
            "ì" to "i",
            "Ì" to "I",
            "î" to "i",
            "Î" to "I",
            "ï" to "i",
            "Ï" to "I",
            "ĩ" to "i",
            "Ĩ" to "I",
            "į" to "i",
            "Į" to "I",
            "ī" to "i",
            "Ī" to "I",
            "ĵ" to "j",
            "Ĵ" to "J",
            "ķ" to "k",
            "Ķ" to "K",
            "ĺ" to "l",
            "Ĺ" to "L",
            "ľ" to "l",
            "Ľ" to "L",
            "ļ" to "l",
            "Ļ" to "L",
            "ł" to "l",
            "Ł" to "L",
            "ṁ" to "m",
            "Ṁ" to "M",
            "ń" to "n",
            "Ń" to "N",
            "ň" to "n",
            "Ň" to "N",
            "ñ" to "n",
            "Ñ" to "N",
            "ņ" to "n",
            "Ņ" to "N",
            "ó" to "o",
            "Ó" to "O",
            "ò" to "o",
            "Ò" to "O",
            "ô" to "o",
            "Ô" to "O",
            "ő" to "o",
            "Ő" to "O",
            "õ" to "o",
            "Õ" to "O",
            "ø" to "oe",
            "Ø" to "OE",
            "ō" to "o",
            "Ō" to "O",
            "ơ" to "o",
            "Ơ" to "O",
            "ö" to "oe",
            "Ö" to "OE",
            "ṗ" to "p",
            "Ṗ" to "P",
            "ŕ" to "r",
            "Ŕ" to "R",
            "ř" to "r",
            "Ř" to "R",
            "ŗ" to "r",
            "Ŗ" to "R",
            "ś" to "s",
            "Ś" to "S",
            "ŝ" to "s",
            "Ŝ" to "S",
            "š" to "s",
            "Š" to "S",
            "ṡ" to "s",
            "Ṡ" to "S",
            "ş" to "s",
            "Ş" to "S",
            "ș" to "s",
            "Ș" to "S",
            "ß" to "SS",
            "ť" to "t",
            "Ť" to "T",
            "ṫ" to "t",
            "Ṫ" to "T",
            "ţ" to "t",
            "Ţ" to "T",
            "ț" to "t",
            "Ț" to "T",
            "ŧ" to "t",
            "Ŧ" to "T",
            "ú" to "u",
            "Ú" to "U",
            "ù" to "u",
            "Ù" to "U",
            "ŭ" to "u",
            "Ŭ" to "U",
            "û" to "u",
            "Û" to "U",
            "ů" to "u",
            "Ů" to "U",
            "ű" to "u",
            "Ű" to "U",
            "ũ" to "u",
            "Ũ" to "U",
            "ų" to "u",
            "Ų" to "U",
            "ū" to "u",
            "Ū" to "U",
            "ư" to "u",
            "Ư" to "U",
            "ü" to "ue",
            "Ü" to "UE",
            "ẃ" to "w",
            "Ẃ" to "W",
            "ẁ" to "w",
            "Ẁ" to "W",
            "ŵ" to "w",
            "Ŵ" to "W",
            "ẅ" to "w",
            "Ẅ" to "W",
            "ý" to "y",
            "Ý" to "Y",
            "ỳ" to "y",
            "Ỳ" to "Y",
            "ŷ" to "y",
            "Ŷ" to "Y",
            "ÿ" to "y",
            "Ÿ" to "Y",
            "ź" to "z",
            "Ź" to "Z",
            "ž" to "z",
            "Ž" to "Z",
            "ż" to "z",
            "Ż" to "Z",
            "þ" to "th",
            "Þ" to "Th",
            "µ" to "u",
            "а" to "a",
            "А" to "a",
            "б" to "b",
            "Б" to "b",
            "в" to "v",
            "В" to "v",
            "г" to "g",
            "Г" to "g",
            "д" to "d",
            "Д" to "d",
            "е" to "e",
            "Е" to "E",
            "ё" to "e",
            "Ё" to "E",
            "ж" to "zh",
            "Ж" to "zh",
            "з" to "z",
            "З" to "z",
            "и" to "i",
            "И" to "i",
            "й" to "j",
            "Й" to "j",
            "к" to "k",
            "К" to "k",
            "л" to "l",
            "Л" to "l",
            "м" to "m",
            "М" to "m",
            "н" to "n",
            "Н" to "n",
            "о" to "o",
            "О" to "o",
            "п" to "p",
            "П" to "p",
            "р" to "r",
            "Р" to "r",
            "с" to "s",
            "С" to "s",
            "т" to "t",
            "Т" to "t",
            "у" to "u",
            "У" to "u",
            "ф" to "f",
            "Ф" to "f",
            "х" to "h",
            "Х" to "h",
            "ц" to "c",
            "Ц" to "c",
            "ч" to "ch",
            "Ч" to "ch",
            "ш" to "sh",
            "Ш" to "sh",
            "щ" to "sch",
            "Щ" to "sch",
            "ъ" to "",
            "Ъ" to "",
            "ы" to "y",
            "Ы" to "y",
            "ь" to "",
            "Ь" to "",
            "э" to "e",
            "Э" to "e",
            "ю" to "ju",
            "Ю" to "ju",
            "я" to "ja",
            "Я" to "ja"
    )
    var formattedString = this
    charMap.forEach { (oldValue, newValue) ->
        formattedString = formattedString.replace(oldValue, newValue, true)
    }
    return formattedString
}
