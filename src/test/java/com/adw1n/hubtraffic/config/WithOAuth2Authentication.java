/*
	Code taken from:
	* https://stackoverflow.com/a/40921028 (question https://stackoverflow.com/questions/29510759/how-to-test-spring-security-oauth2-resource-server-security/31434559#31434559)
	* https://github.com/timtebeek/resource-server-testing/tree/simplified
	Hats off to them for the idea!
*/


package com.adw1n.hubtraffic.config;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithSecurityContext(factory = WithOAuth2AuthenticationSecurityContextFactory.class)
public @interface WithOAuth2Authentication {
	String username() default "user";
}
