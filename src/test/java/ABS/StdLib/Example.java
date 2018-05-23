package ABS.StdLib;

import abs.api.cwi.ABSFuture;
import abs.api.cwi.LocalActor;

public class Example extends LocalActor {

    ABSFuture<Integer> getX(){
        return ABSFuture.done(5);
    }


    ABSFuture<Integer> getY(){
        return ABSFuture.done(3);
    }


    ABSFuture<Integer> sumCoroutine(){
        Example e = new Example();

        ABSFuture<Integer> fx = e.send(()->e.getX());
        ABSFuture<Integer> fy = e.send(()->e.getY());

        return getSpawn(fx,(x)->{
            System.out.println("x = "+x);
            return getSpawn(fy, (y)->{
                System.out.println("y = "+y);
                return ABSFuture.done(x+y);
            });
        });
    }
}
