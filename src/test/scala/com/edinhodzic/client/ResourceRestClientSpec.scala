package com.edinhodzic.client

import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response.Status
import javax.ws.rs.core.Response.Status.{CREATED, INTERNAL_SERVER_ERROR, NOT_FOUND, OK}

import com.edinhodzic.client.domain.Resource
import com.sun.jersey.api.client.{Client, ClientResponse, WebResource}
import org.specs2.mock.Mockito
import org.specs2.mutable.SpecificationWithJUnit

import scala.language.postfixOps

class ResourceRestClientSpec extends SpecificationWithJUnit with Mockito {
  isolated

  private val jerseyClient: Client = mock[Client]
  private val jerseyClientResponse: ClientResponse = mock[ClientResponse]

  private val abstractRestClient: AbstractRestClient[Resource] =
    new AbstractRestClient[Resource]("http://api.example.com:9001", "us3rn4m3", "p4s5w0rd") {
      override private[client] val client: Client = jerseyClient
    }

  private val resource: Resource = new Resource("data")
  private val resourceId: String = "5627a1764568cdf041e0996e"

  private val webResourceBuilder: WebResource#Builder = mock[WebResource#Builder]

  def mockJerseyClient: WebResource#Builder = {
    val webResource: WebResource = mock[WebResource]
    jerseyClient resource anyString returns webResource
    webResource `type` anyString returns webResourceBuilder
    webResourceBuilder accept APPLICATION_JSON returns webResourceBuilder
    webResourceBuilder entity any returns webResourceBuilder

    webResourceBuilder post classOf[ClientResponse] returns jerseyClientResponse
    webResourceBuilder get classOf[ClientResponse] returns jerseyClientResponse

    webResourceBuilder
  }

  def mockClientResponse(status: Status, resource: Resource = null) = {
    mockJerseyClient
    jerseyClientResponse getStatus() returns status.getStatusCode
    jerseyClientResponse getEntity classOf[Resource] returns resource
  }

  "Client post function" should {

    "invoke jersey client post function" in {
      mockClientResponse(CREATED, resource)

      abstractRestClient post resource
      there was one(jerseyClient).resource("http://api.example.com:9001/resource")
      there was one(webResourceBuilder).post(classOf[ClientResponse])
    }

    "return a success and resource when jersey client post returns http created" in {
      mockClientResponse(CREATED, resource)
      abstractRestClient post resource must beSuccessfulTry(resource)
    }

    "return a failure when jersey client post does not return a http created" in {
      mockClientResponse(INTERNAL_SERVER_ERROR)
      abstractRestClient post resource must beFailedTry
    }
  }

  "Client get function" should {

    "invoke jersey client get function" in {
      mockClientResponse(OK)

      abstractRestClient get resourceId
      there was one(jerseyClient).resource(s"http://api.example.com:9001/resource/$resourceId")
      there was one(webResourceBuilder).get(classOf[ClientResponse])
    }

    "return a success with some when jersey client get returns http ok" in {
      mockClientResponse(OK, resource)
      abstractRestClient get resourceId must beSuccessfulTry(Some(resource))
    }

    "return a success with none when jersey client get returns http not found" in {
      mockClientResponse(NOT_FOUND)
      abstractRestClient get resourceId must beSuccessfulTry(None)
    }

    "return a failure when jersey client get does not return http ok or not found" in {
      mockClientResponse(INTERNAL_SERVER_ERROR)
      abstractRestClient get resourceId must beFailedTry
    }
  }

  "Client put function" should {
    // the implementation here depends on whether it's whole or partial updates which the client supports
    "invoke jersey client put function" in {
      skipped("to be implemented") // TODO
    }
  }

  "Client delete function" should {
    "invoke jersey client delete function" in {
      skipped("to be implemented") // TODO
    }
    "return a success with some when jersey client delete returns http no content" in {
      skipped("to be implemented") // TODO
    }

    "return a success with none when jersey client delete returns http not found" in {
      skipped("to be implemented") // TODO
    }

    "return a failure when jersey client delete does not return http no content or not found" in {
      skipped("to be implemented") // TODO
    }
  }

  "Client query function" should {
    "do something" in {
      skipped("to be implemented") // TODO
    }
  }

}
