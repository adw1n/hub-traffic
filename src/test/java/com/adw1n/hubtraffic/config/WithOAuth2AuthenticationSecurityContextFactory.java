/*
	Code taken from:
	* https://stackoverflow.com/a/40921028 (question https://stackoverflow.com/questions/29510759/how-to-test-spring-security-oauth2-resource-server-security/31434559#31434559)
	* https://github.com/timtebeek/resource-server-testing/tree/simplified
	Hats off to them for the idea!
*/


package com.adw1n.hubtraffic.config;

import com.adw1n.hubtraffic.models.GithubUser;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WithOAuth2AuthenticationSecurityContextFactory implements WithSecurityContextFactory<WithOAuth2Authentication> {
	@Override
	public SecurityContext createSecurityContext(final WithOAuth2Authentication oauth) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		OAuth2Request request = new OAuth2Request(null, null, null, true, null, null, null, null, null);
		Authentication auth = new OAuth2Authentication(request, new TestingAuthenticationToken(oauth.username(), null, "read"));
		context.setAuthentication(auth);
		return context;
	}
	public static Authentication getPrincipal(GithubUser user){
		OAuth2Request request = new OAuth2Request(null, null, null, true, null, null, null, null, null);
		OAuth2Authentication auth = new OAuth2Authentication(request, new TestingAuthenticationToken(user.getName(), null, "read"));
		OAuth2AuthenticationDetails details = mock(OAuth2AuthenticationDetails.class);
		when(details.getTokenValue()).thenReturn(user.getToken());
		auth.setDetails(details);
		return auth;
	}
}
