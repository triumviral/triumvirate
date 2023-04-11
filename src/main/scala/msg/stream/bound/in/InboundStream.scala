package msg.stream.bound.in

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import akka.pattern.StatusReply
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior, Recovery, ReplyEffect}
import akka.stream.scaladsl.Sink
import akka.stream.typed.scaladsl.ActorSink
import akka.{Done, NotUsed}

import scala.util.chaining.*

object InboundStream:
  case class Config(name: String,
                    persistenceId: Config.PersistenceId)

  object Config:
    case class PersistenceId(uniqueId: String)

  sealed trait Command[In]

  object Command:
    sealed trait Stream[In] extends Command[In]

    object Stream:
      case class Pre[In](ackTo: ActorRef[StatusReply[Done]]) extends Stream[In]

      case class Peri[In](ackTo: ActorRef[StatusReply[Done]], msg: In) extends Stream[In]

      case class Post[In](status: StatusReply[NotUsed] = StatusReply.Success(value = NotUsed)) extends Stream[In]

      object Post:
        def apply[In](exception: Throwable): Post[In] = new Post(
          status = StatusReply.Error(exception = exception)
        )

    def apply[In](state: State, command: Command[In])
                 (using cfg: Config, ctx: ActorContext[Command[In]])
    : ReplyEffect[Event, State] = (state, command) match
      case (State.Streaming.Pre, Command.Stream.Pre(ackTo)) =>
        Effect
          .persist(event = Event.Streamed.Pre)
          .thenReply(replyTo = ackTo)(replyWithMessage = (_state: State) => StatusReply.Ack)
      case (State.Streaming.Peri, Command.Stream.Peri(ackTo, msg)) =>
        Effect
          .none
          .thenReply(replyTo = ackTo)(replyWithMessage = (_state: State) => StatusReply.Ack)
      case _ => Effect.unhandled.thenNoReply()

  sealed trait Event

  object Event:
    object Streamed:
      case object Pre extends Event

    def apply[In](state: State, event: Event)
                 (using cfg: Config, ctx: ActorContext[Command[In]])
    : State = (state, event) match
      case (State.Streaming.Pre, Event.Streamed.Pre) => State.Streaming.Peri
      case _ => state

  sealed trait State

  object State:
    object Streaming:
      case object Pre extends State

      case object Peri extends State

    def apply(): State = Streaming.Pre

  def apply[In](cfg: Config)(using ctx: ActorContext[_]): InboundStream[In] =
    given Config = cfg

    new InboundStream(self = ctx.spawn(behavior = apply(), name = cfg.name))

  def apply[In]()(using cfg: Config): Behavior[Command[In]] = Behaviors.setup:
    implicit ctx =>
      EventSourcedBehavior.withEnforcedReplies(
        persistenceId = PersistenceId.ofUniqueId(id = cfg.persistenceId.uniqueId),
        emptyState = State(),
        commandHandler = Command.apply[In],
        eventHandler = Event.apply[In]
      ).withRecovery(recovery = Recovery.disabled)

class InboundStream[In](self: ActorRef[InboundStream.Command[In]]):

  import InboundStream.*

  def stream(): Sink[In, NotUsed] =
    ActorSink.actorRefWithBackpressure(
      ref = self,
      messageAdapter = Command.Stream.Peri.apply,
      onInitMessage = Command.Stream.Pre.apply,
      ackMessage = StatusReply.Ack,
      onCompleteMessage = Command.Stream.Post(),
      onFailureMessage = Command.Stream.Post.apply
    )
