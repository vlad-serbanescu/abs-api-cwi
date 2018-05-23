package Run;

import abs.api.cwi.Functions._;
import abs.api.cwi._;
import java.util.Objects;
import java.util.concurrent._;
import java.util.concurrent.atomic._;
import java.util.concurrent.locks._;
import java.util.function._;
import Run.Functions._;
import Train._;
import Train.Functions._;
import Graph._;
import Graph.Functions._;
import Util._;
import Util.Functions._;
import SwElements._;
import SwElements.Functions._;
import TrackElements._;
import TrackElements.Functions._;
import Stellwerk._;
import Stellwerk.Functions._;
import ABS.StdLib._;
import ABS.StdLib.Functions._;

class Application(var destCOG : LocalActor) extends LocalActor with App {
  private final var serialVersionUID : java.lang.Long =  1L;


  var i : Int =  -1;

  var f : ABSFuture[Int] =  null;

  def next(): ABSFuture[Void]= {
    this.i = 100;
    if(Array(this.i,this.i)(0)<=0) {
      return this.ApplicationnextAwait0();
    }
    else {
      return spawn(Guard.convert(Array(this.i,this.i)),()=>this.ApplicationnextAwait0());
    }
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def advance( r : Double): ABSFuture[Void]= {
    var f_w1r : Double = r;
    if(new Supplier[Boolean] {
  def get(): Boolean= {
    return (i > 0);
  }
}
.get()) {
      return this.ApplicationadvanceAwait0(f_w1r);
    }
    else {
      return spawn(Guard.convert(new Supplier[Boolean] {
            def get(): Boolean= {
              return (i > 0);
            }
          }
          ),()=>this.ApplicationadvanceAwait0(f_w1r));
    }
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def getApplication(): ABSFuture[App]= {
    return ABSFuture.done(null);
  }

  def addBreak( delay : Int,  s : Signal): ABSFuture[Void]= {
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def run(): ABSFuture[Void]= {
println("choose an example!");
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def insertTrainInStation( n : Node,  e : Edge,  t : Int,  v : Int,  zug : Train,  resp : ZugMelde): ABSFuture[Void]= {
    var f_a00 : Int = 0;
    var msg_120972104 : Callable[ABSFuture[Void]] = () => zug.goResp(e, n, f_a00, v, resp);
    zug.send(msg_120972104);
    return ABSFuture.done();
    return ABSFuture.done();
  }


def ApplicationnextAwait0(): ABSFuture[Void]= {
  this.i = -1;
  return ABSFuture.done();
}
def ApplicationadvanceAwait0( r : Double): ABSFuture[Void]= {
  var w_1$r : Double =  r;
  var f_w2w_1$r : Double = w_1$r;
  if(Array(w_1$r,w_1$r)(0)<=0) {
    return this.ApplicationadvanceAwait1(f_w2w_1$r);
  }
  else {
    return spawn(Guard.convert(Array(w_1$r,w_1$r)),()=>this.ApplicationadvanceAwait1(f_w2w_1$r));
  }
  return ABSFuture.done();
}
def ApplicationadvanceAwait1( r : Double): ABSFuture[Void]= {
  var w_2$r : Double =  r;
  return ABSFuture.done();
}
{
    moveToCOG(destCOG);
  }

}
