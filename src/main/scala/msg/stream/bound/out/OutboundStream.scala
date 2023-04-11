package msg.stream.bound.out

import akka.NotUsed
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import akka.pattern.StatusReply
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior, Recovery}
import akka.persistence.typed.{PersistenceId, RecoveryCompleted}
import akka.stream.{CompletionStrategy, Materializer}
import akka.stream.scaladsl.Source
import akka.stream.typed.scaladsl.ActorSource

import scala.concurrent.Promise
import scala.util.chaining.*
import scala.util.{Success, Try}

object OutboundStream:
  case class Config(name: String,
                    persistenceId: Config.PersistenceId)

  object Config:
    case class PersistenceId(uniqueId: String)

  sealed trait Message[Out]

  object Message:
    case class Peri[Out](msg: Out) extends Message[Out]

    case class Post(status: StatusReply[CompletionStrategy]) extends Message[Nothing]

    object Post:
      object Pre:
        def unapply[Out](message: Message[Out]): Option[Throwable] = PartialFunction.condOpt(x = message):
          case Message.Post(StatusReply.Error(exception)) => exception

      object Post:
        def unapply[Out](message: Message[Out]): Option[CompletionStrategy] = PartialFunction.condOpt(x = message):
          case Message.Post(StatusReply.Success(value: CompletionStrategy)) => value

  sealed trait Command[Out]

  object Command:
    case class Acknowledge[Out]() extends Command[Out]

    object Stream:
      case class Pre[Out](ref: ActorRef[Message[Out]]) extends Command[Out]

    def apply[Out](state: State, command: Command[Out])
                  (using cfg: Config, ctx: ActorContext[Command[Out]])
    : Effect[Event, State] = (state, command) match
      case _ => Effect.unhandled

  sealed trait Event

  object Event:
    object Streamed:
      case object Pre extends Event

    def apply[Out](state: State, event: Event)
                  (using cfg: Config, ctx: ActorContext[Command[Out]]): State = (state, event) match
      case _ => state

  sealed trait State

  case object State extends State:
    def apply(): State = State

  def apply[Out](cfg: Config)(using ctx: ActorContext[_]): OutboundStream[Out] =
    given Config = cfg

    new OutboundStream(self = ctx.spawn(behavior = apply(), name = cfg.name))

  def apply[Out]()(using cfg: Config): Behavior[Command[Out]] = Behaviors.setup:
    implicit ctx =>
      EventSourcedBehavior(
        persistenceId = PersistenceId.ofUniqueId(id = cfg.persistenceId.uniqueId),
        emptyState = State(),
        commandHandler = Command.apply[Out],
        eventHandler = Event.apply
      ).withRecovery(recovery = Recovery.disabled)

class OutboundStream[Out](self: ActorRef[OutboundStream.Command[Out]]):

  import OutboundStream.*

  def stream()(using Materializer): Source[Out, NotUsed] =
    val source: Source[Out, ActorRef[Message[Out]]] =
      ActorSource.actorRefWithBackpressure(
        ackTo = self,
        ackMessage = Command.Acknowledge(),
        completionMatcher = Message.Post.Post.unapply[Out].unlift,
        failureMatcher = Message.Post.Pre.unapply[Out].unlift,
      ).collect:
        case Message.Peri(msg) => msg
    val (ref: ActorRef[Message[Out]], source_ : Source[Out, NotUsed]) = source.preMaterialize()
    self.tell(msg = Command.Stream.Pre(ref = ref))
    source_
