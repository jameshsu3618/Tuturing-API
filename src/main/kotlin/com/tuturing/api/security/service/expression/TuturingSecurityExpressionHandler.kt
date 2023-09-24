package com.tuturing.api.security.service.expression

import org.aopalliance.intercept.MethodInvocation
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler

class TuturingSecurityExpressionHandler : OAuth2MethodSecurityExpressionHandler() {
    override fun createEvaluationContextInternal(authentication: Authentication, mi: MethodInvocation): StandardEvaluationContext {
        val ec = super.createEvaluationContextInternal(authentication, mi)
        ec.setVariable("tuturing", TuturingSecurityExpressionMethods(authentication))
        return ec
    }
}
