package main.java;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import main.java.GreeterGrpc;
import java.io.IOException;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.vertx.circuitbreaker.*;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
	    
	  CircuitBreaker breaker = CircuitBreaker.create("my-circuit-breaker", vertx,
			    new CircuitBreakerOptions()
			        .setMaxFailures(2) // number of failure before opening the circuit
			        .setTimeout(2000) // consider a failure if the operation does not succeed in time
			        .setFallbackOnFailure(false) // do we call the fallback on failure
			        .setResetTimeout(1000) // time spent in open state before attempting to re-try
			);

		// ---
		// Store the circuit breaker in a field and access it as follows
		// ---
	  
		breaker.execute(promise -> {
			System.out.println("Start grpc in circuit breaker");
			
			 Server server = ServerBuilder
			          .forPort(8181)
			          .addService(new GreeterServerImpl()).build();

			        try {
						server.start();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

		}).onComplete(ar -> {
			System.out.println("Complete operation.");
		});
  }
}
