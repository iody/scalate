package org.fusesource.scalate.jersey

import java.io.{OutputStream}
import java.net.MalformedURLException
import javax.servlet.{ServletContext}
import com.sun.jersey.api.view.Viewable
import com.sun.jersey.spi.template.ViewProcessor
import com.sun.jersey.server.impl.container.servlet.RequestDispatcherWrapper
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import com.sun.jersey.api.core.{HttpContext, ResourceConfig}
import org.fusesource.scalate.util.Logging
//import org.apache.log4j.Logger
import com.sun.jersey.api.container.ContainerException
import javax.ws.rs.core.Context

/**
 * @version $Revision : 1.1 $
 */
class SSPTemplateProcessor(@Context resourceConfig: ResourceConfig) extends ViewProcessor[String] with Logging {
  @Context
  var servletContext: ServletContext = _
  @Context
  var hc: HttpContext = _
  @Context
  var request: HttpServletRequest = _
  @Context
  var response: HttpServletResponse = _

  val basePath = resourceConfig.getProperties().get("org.scala-tools.serverpages.config.property.SSPTemplatesBasePath") match {
    case path: String =>
      if (path(0) == '/') {
        path
      }
      else {
        "/" + path
      }
    case _ => ""
  }


  def resolve(requestPath: String): String = {
    if (servletContext == null) {
      log.warning("No servlet context")
      return null
    }

    try {
      val path = if (basePath.length > 0) {basePath + requestPath} else {requestPath}

      for (suffix <- Array("", ".ssp", ".scaml")) {
        val fullPath = path + suffix;
        
        if (servletContext.getResource(fullPath) != null)
          return fullPath;
      }
    } catch {
      case e: MalformedURLException =>
      // TODO log
    }
    null
  }

  def writeTo(resolvedPath: String, viewable: Viewable, out: OutputStream): Unit = {
    // Ensure headers are committed
    out.flush();

    val dispatcher = servletContext.getRequestDispatcher(resolvedPath);
    if (dispatcher == null) {
      throw new ContainerException("No request dispatcher for: " + resolvedPath);
    }

    val wrapper = new RequestDispatcherWrapper(dispatcher, basePath, hc, viewable);
    try {
      wrapper.forward(request, response);
      //wrapper.forward(requestInvoker.get(), responseInvoker.get());
    } catch {
      case e: Exception => throw new ContainerException(e);
    }
  }

}