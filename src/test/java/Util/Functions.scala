package Util;


import abs.api.cwi.Functions._;
import abs.api.cwi._;
import java.util.Objects;
import java.util.concurrent._;
import java.util.concurrent.atomic._;
import java.util.concurrent.locks._;
import java.util.function._;
import Util.Functions._;
import TrackElements._;
import TrackElements.Functions._;
import SwElements._;
import SwElements.Functions._;
import Stellwerk._;
import Stellwerk.Functions._;
import ABS.StdLib._;
import ABS.StdLib.Functions._;


object Functions {

  def stt( data : VorblockEv): SwElements.Strecke= {
    return data match  {
        case VEvent(_, res, _) => res;
        }
        ;
  }

  def idd( data : VorblockEv): Int= {
    return data match  {
        case VEvent(_, _, res) => res;
        }
        ;
  }

  def zff( data : VorblockEv): ZugFolge= {
    return data match  {
        case VEvent(res, _, _) => res;
        }
        ;
  }

  def spp( data : Information): SignalState= {
    return data match  {
        case StartPrepare(res) => res;
        }
        ;
  }

  def si( data : Information): SignalState= {
    return data match  {
        case Info(res) => res;
        }
        ;
  }

  def sp( data : Information): SignalState= {
    return data match  {
        case Prepare(res) => res;
        }
        ;
  }

  def ll( data : NextEvent): Double= {
    return data match  {
        case Ev(_, res, _, _, _, _, _, _, _, _) => res;
        }
        ;
  }

  def fahrCount( data : NextEvent): Int= {
    return data match  {
        case Ev(_, _, _, _, _, res, _, _, _, _) => res;
        }
        ;
  }

  def pzbOneLess( data : NextEvent): Boolean= {
    return data match  {
        case Ev(_, _, _, _, _, _, _, _, _, res) => res;
        }
        ;
  }

  def vold( data : NextEvent): Double= {
    return data match  {
        case Ev(_, _, _, _, _, _, _, _, res, _) => res;
        }
        ;
  }

  def start( data : NextEvent): Time= {
    return data match  {
        case Ev(_, _, _, _, _, _, _, res, _, _) => res;
        }
        ;
  }

  def counter( data : NextEvent): Int= {
    return data match  {
        case Ev(_, _, _, _, res, _, _, _, _, _) => res;
        }
        ;
  }

  def position( data : NextEvent): Pos= {
    return data match  {
        case Ev(_, _, _, _, _, _, res, _, _, _) => res;
        }
        ;
  }

  def vnew( data : NextEvent): Double= {
    return data match  {
        case Ev(_, _, _, res, _, _, _, _, _, _) => res;
        }
        ;
  }

  def newState( data : NextEvent): AccelState= {
    return data match  {
        case Ev(_, _, res, _, _, _, _, _, _, _) => res;
        }
        ;
  }

  def moment( data : NextEvent): Double= {
    return data match  {
        case Ev(res, _, _, _, _, _, _, _, _, _) => res;
        }
        ;
  }

  def emergNow( data : GenEvent): Boolean= {
    return data match  {
        case ChangeEv(_, res) => res;
        }
        ;
  }

  def ne2( data : GenEvent): NextEvent= {
    return data match  {
        case FrontEv(res) => res;
        }
        ;
  }

  def ne1( data : GenEvent): NextEvent= {
    return data match  {
        case ChangeEv(res, _) => res;
        }
        ;
  }

  def ne4( data : GenEvent): NextEvent= {
    return data match  {
        case PZBIntersectEv(res) => res;
        }
        ;
  }

  def ne3( data : GenEvent): NextEvent= {
    return data match  {
        case BackEv(res) => res;
        }
        ;
  }

}
