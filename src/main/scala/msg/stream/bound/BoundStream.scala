package msg.stream.bound

import akka.NotUsed
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.ActorContext
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Keep}
import msg.stream.bound.in.InboundStream
import msg.stream.bound.out.OutboundStream

object BoundStream:
  case class Config(name: String,
                    in: InboundStream.Config,
                    out: OutboundStream.Config)

  sealed trait Command

  def apply[In, Out](cfg: Config)(using ctx: ActorContext[_]) =
    new BoundStream(
      self = ctx.spawn(behavior = ???, name = cfg.name),
      in = InboundStream(cfg = cfg.in),
      out = OutboundStream(cfg = cfg.out)
    )

class BoundStream[In, Out](self: ActorRef[BoundStream.Command],
                           in: InboundStream[In],
                           out: OutboundStream[Out]):
  def stream()(using Materializer): Flow[In, Out, NotUsed] =
    Flow.fromSinkAndSourceCoupledMat(
      sink = in.stream(),
      source = out.stream()
    )(combine = Keep.none)
