
import scala.collection.mutable
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

  def allResolved(futures: List[Future[_]]):Future[List[Any]] = {
    val promise = Promise[List[Any]]
    var results = new mutable.MutableList[(Int, Any)]()
    for ((future, index) <- futures.zipWithIndex) {
      future onComplete  {
        case Failure(e) => promise.failure(e)
        case Success(result) =>
          synchronized {
            results+=((index, result))
            if (results.length == futures.length) {
              val (_, innerResults) = results.sortBy(_._1).unzip
              promise.trySuccess(innerResults.toList)
            }
          }
      }
    }
    promise.future
  }

  val f = allResolved(List(f3,f2,f1))
  f onComplete {
    case Success(results) => results foreach println
    case Failure(e) => println(s"error ${e.getLocalizedMessage}")
  }

  val dontDie = Future {
    Thread.sleep(10000)
  }
  Await.result(dontDie, Duration.Inf)
}
