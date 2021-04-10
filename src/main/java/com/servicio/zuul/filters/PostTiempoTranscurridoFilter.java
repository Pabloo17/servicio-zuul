package com.servicio.zuul.filters;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PostTiempoTranscurridoFilter extends ZuulFilter {

  /** Si se va a ejecutar el filtro: true se ejecuta siempre */
  @Override
  public boolean shouldFilter() {
    return true;
  }

  @Override
  public Object run() throws ZuulException {

    RequestContext ctx = RequestContext.getCurrentContext();
    HttpServletRequest request = ctx.getRequest();
    log.info("Entrando a post filter");

    Long tiempoInicio = (Long) request.getAttribute("tiempoInicio");
    Long tiempoFinal = System.currentTimeMillis();
    Long tiempoTranscurrido = tiempoFinal - tiempoInicio;

    log.info(
        String.format(
            "Tiempo transcurrido en segundos %s", tiempoTranscurrido.doubleValue() / 1000.00));

    log.info(String.format("Tiempo transcurrido en mileseg %s", tiempoTranscurrido));

    request.setAttribute("tiempoInicio", tiempoInicio);

    return null;
  }

  @Override
  public String filterType() {
    return "post";
  }

  @Override
  public int filterOrder() {
    return 1;
  }
}
