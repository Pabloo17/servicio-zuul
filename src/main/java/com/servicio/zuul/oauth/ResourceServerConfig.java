package com.servicio.zuul.oauth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

  @Override
  public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
    resources.tokenStore(tokenStore());
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    // url para autenticar token, acceso global con permitAll
    http.authorizeRequests()
        .antMatchers("/api/security/oauth/**")
        .permitAll()
        .antMatchers(
            HttpMethod.GET, "/api/productos/listar", "/api/items/listar", "/api/usuarios/usuarios")
        .permitAll()
        .antMatchers(
            HttpMethod.GET,
            "/api/productos/ver/{id}",
            "/api/items/ver/{id}/cantidad{cantidad}",
            "/api/usuarios/usuarios/{id}")
        .hasAnyRole("ADMIN", "USER")
        .antMatchers("/api/productos/**", "/api/items/**", "/api/usuarios/**")
        .hasRole(
            "ADMIN") // cualquier request que no haya sido especificada, requiere autentificacion
        .anyRequest()
        .authenticated();
  }

  // copiados de la clase AuthorizationServerConfig del servicio oauth
  @Bean
  public JwtTokenStore tokenStore() {

    return new JwtTokenStore(accessTokenConverter());
  }

  @Bean
  public JwtAccessTokenConverter accessTokenConverter() {
    JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
    tokenConverter.setSigningKey("ejemplo_secret_code");

    return tokenConverter;
  }
}
