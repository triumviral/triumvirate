package serv.server

import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.typed.scaladsl.ActorFlow
import akka.util.Timeout
import serv.service.grpc.{GraphQLService, QueryRequest, QueryResponse}

import scala.concurrent.{ExecutionContext, Future}

class GraphQLServiceImpl(using materializer: Materializer, timeout: Timeout) extends GraphQLService:
  given ExecutionContext = materializer.executionContext

  override def query(request: QueryRequest): Future[QueryResponse] = ???
