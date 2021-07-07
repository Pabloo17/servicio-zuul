package com.servicio.zuul.oauth;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@RefreshScope
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

  @Value("${config.security.oauth.jwt.key}")
  private String jwtKey;

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
        .authenticated()
        .and()
        .cors()
        .configurationSource(corsConfigurationSource());
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration corsConfig = new CorsConfiguration();
    corsConfig.addAllowedOrigin("*");
    corsConfig.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "OPTIONS"));
    corsConfig.setAllowCredentials(true);
    corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

    // Para que se aplique a todos los endpoints
    source.registerCorsConfiguration("/**", corsConfig);

    return source;
  }

  // Para que se aplique de forma global, no solo en spring security sino en toda la aplicacion
  @Bean
  public FilterRegistrationBean<CorsFilter> corsFilter() {
    FilterRegistrationBean<CorsFilter> bean =
        new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource()));

    bean.setOrder(Ordered.HIGHEST_PRECEDENCE);

    return bean;
  }

  // copiados de la clase AuthorizationServerConfig del servicio oauth
  @Bean
  public JwtTokenStore tokenStore() {

    return new JwtTokenStore(accessTokenConverter());
  }

  @Bean
  public JwtAccessTokenConverter accessTokenConverter() {
    JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
    tokenConverter.setSigningKey(jwtKey);

    return tokenConverter;
  }
}
