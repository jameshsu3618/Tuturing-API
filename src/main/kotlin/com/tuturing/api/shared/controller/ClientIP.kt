package com.tuturing.api.shared.controller

import javax.servlet.http.HttpServletRequest

fun clientIpFromRequest(request: HttpServletRequest): String? {
    var remoteAddr = request.getHeader("X-FORWARDED-FOR")
    if (remoteAddr == null || "" == remoteAddr) {
        remoteAddr = request.remoteAddr
    }
    return remoteAddr
}
