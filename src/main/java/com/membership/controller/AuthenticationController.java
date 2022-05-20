package com.membership.controller;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.membership.domain.Member;
import com.membership.domain.Membership;
import com.membership.domain.MembershipType;
import com.membership.service.MemberService;
import com.membership.service.NotAuthorizedException;

import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/authentications")
public class AuthenticationController {
    @Autowired
    private HttpServletRequest httpServletRequest;
    
    @Autowired
    private MemberService memberService;
    
    @GetMapping
    private  Member isAuthenticated() throws NotAuthorizedException{
        KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) httpServletRequest.getUserPrincipal();
        if(token==null)throw new NotAuthorizedException("Unauthorized member");
        @SuppressWarnings("rawtypes")
		KeycloakPrincipal principal=(KeycloakPrincipal)token.getPrincipal();
        KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
        AccessToken accessToken = session.getToken();
        String role = accessToken.getRealmAccess().getRoles().stream().filter(rol->rol.equals("checker")).findFirst().get();

        if(!role.equals("checker"))throw new NotAuthorizedException("Unauthorized member");
        String email = accessToken.getEmail();
        Member member = memberService.findByEmail(email).get();
        if(member!=null) return member;
        throw new NotAuthorizedException("Unauthorized member");
    }

}
