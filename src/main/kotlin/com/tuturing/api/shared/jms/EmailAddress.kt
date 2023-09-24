package com.tuturing.api.shared.jms

data class EmailAddress(
    val name: String?,
    val email: String
) {
    fun format(): String {
        return if (null != this.name) {
            (this.name + " " + "<" + this.email + ">")
        } else {
            this.email
        }
    }

    companion object {
        fun fromString(email: String): EmailAddress {
            val parts = email.split("<")
            return if (parts.size == 2) {
                EmailAddress(parts[0].trim(), parts[1].trim('<').trim('>'))
            } else {
                EmailAddress(null, email)
            }
        }
    }
}
