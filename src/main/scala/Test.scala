
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, Promise}
import scala.util.{Failure, Success}


object Test extends App{
  val f1 = Future {
    Thread sleep 1000
    println("f1 resolved")
    "f1 done"
//    throw new Exception("alsdkfj")
  }
  val f2 = Future {
    Thread sleep 1001
    println("f2 resolved")
    "f2 done"
  }
  val f3 = Future {
    Thread sleep 1002
    println("f3 resolved")
    "f3 done"
  }

  def logFuture[T](future: Future[T]):Future[T] = {
    val promise = Promise[T]
    future onComplete {
      case Failure(e) => {
        println(e.getMessage)
        promise.tryFailure(e)
      }
      case Success(result) => {
        println(result)
        promise.trySuccess(result)
      }
    }
    promise.future
  }

  for {
    _ <- logFuture(f3)
    _ <- logFuture(f2)
    _ <- logFuture(f1)
  } yield ()

  val doneDie = Future {
    Thread.sleep(10000)
  }
  Await.result(doneDie, Duration.Inf)
}
