package com.edinhodzic.client

import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status.{CREATED, NOT_FOUND, NO_CONTENT, OK, fromStatusCode}

import com.edinhodzic.client.AbstractRestClient._
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter
import com.sun.jersey.api.client.{Client, ClientResponse, WebResource}
import org.slf4j.{Logger, LoggerFactory}

import scala.language.postfixOps
import scala.reflect._
import scala.util.{Failure, Success, Try}

/**
  * Client for a RESTful web service.
  * @param url web service url (scheme, host and port) e.g. http://localhost:9000
  * @param username basic HTTP authentication username
  * @param password basic HTTP authentication password
  */
abstract class AbstractRestClient[T: Manifest]
(url: String, username: String, password: String)
(implicit manifest: Manifest[T]) {

  private val collectionUrl: String = s"$url/${manifest.runtimeClass.getSimpleName.toLowerCase}"

  private[client] val client: Client = {
    val client: Client = Client.create()
    client addFilter new HTTPBasicAuthFilter(username, password)
    client
  }

  def post(resource: T): Try[T] = {
    implicit val clientResponse: ClientResponse = webResource(collectionUrl, Option(resource)) post classOf[ClientResponse]
    clientResponseStatus match {
      case CREATED =>
        logInfo("POST", collectionUrl)
        Success(getEntity(clientResponse))
      case _ =>
        logError("POST", collectionUrl)
        Failure(raiseExceptionFor("POST", collectionUrl))
    }
  }

  def get(resourceId: String): Try[Option[T]] = {
    val resourceUrl: String = collectionResourceUrl(resourceId)
    implicit val clientResponse: ClientResponse = webResource(resourceUrl) get classOf[ClientResponse]
    clientResponseStatus match {
      case OK =>
        logInfo("GET", resourceUrl)
        Success(Some(getEntity(clientResponse)))
      case NOT_FOUND =>
        logInfo("GET", resourceUrl)
        Success(None)
      case _ =>
        logError("GET", resourceUrl)
        Failure(raiseExceptionFor("GET", resourceUrl))
    }
  }

  // TODO implement updating (not necessarily in this class); again this can be a whole or partial update

  def delete(resourceId: String): Try[Option[Unit]] = {
    val resourceUrl: String = collectionResourceUrl(resourceId)
    implicit val clientResponse: ClientResponse = webResource(resourceUrl) delete classOf[ClientResponse]
    clientResponseStatus match {
      case NO_CONTENT =>
        logInfo("DELETE", resourceUrl)
        Success(Some())
      case NOT_FOUND =>
        logInfo("DELETE", resourceUrl)
        Success(None)
      case _ =>
        logError("DELETE", resourceUrl)
        Failure(raiseExceptionFor("DELETE", resourceUrl))
    }
  }

  // TODO implement querying (not necessarily in this class)

  def collectionResourceUrl(id: String): String = s"$collectionUrl/$id"

  private def webResource(url: String, resource: Option[T] = None): WebResource#Builder = {
    val builder: WebResource#Builder = client resource url `type` APPLICATION_JSON accept APPLICATION_JSON
    if (resource isDefined)
      builder entity resource.get
    else builder
  }

  private def getEntity(clientResponse: ClientResponse): T =
    clientResponse getEntity manifest.runtimeClass.asInstanceOf[Class[T]]

}

object AbstractRestClient {

  private val logger: Logger = LoggerFactory getLogger getClass

  private def clientResponseStatus(implicit clientResponse: ClientResponse): Response.Status =
    fromStatusCode(clientResponse getStatus)

  private def logInfo(httpMethod: String, url: String)(implicit clientResponse: ClientResponse) =
    logger info logMessage(httpMethod, url, clientResponse)

  private def logError(httpMethod: String, url: String)(implicit clientResponse: ClientResponse) =
    logger error logMessage(httpMethod, url, clientResponse)

  private def logMessage(httpMethod: String, url: String, clientResponse: ClientResponse): String =
    s"$httpMethod $url ${clientResponse getStatus} ${clientResponse getStatusInfo}"

  private def raiseExceptionFor(httpMethod: String, url: String)(implicit clientResponse: ClientResponse) =
    new RestClientException(s"$httpMethod $url failed : ${clientResponse getStatusInfo}")

  class RestClientException(message: String) extends Exception(message)

}