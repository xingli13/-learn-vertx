package io.vertx.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Launcher;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.starter.verticle.WikiDatabaseVerticle;

public class MainVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    Future<String> dbVerticleDepolyment = Future.future();
    vertx.deployVerticle(new WikiDatabaseVerticle(), dbVerticleDepolyment.completer());

    dbVerticleDepolyment.compose(id -> {
      Future<String> httpVerticleDepolyment = Future.future();
      vertx.deployVerticle("io.vertx.starter.verticle.HttpServerVerticle",
        new DeploymentOptions().setInstances(2),
        httpVerticleDepolyment.completer());
      return httpVerticleDepolyment;
    }).setHandler(ar -> {
      if (ar.succeeded()) {
        startFuture.complete();
      } else {
        startFuture.fail(ar.cause());
      }
    });
  }
  public static void main(String[] args) {
    Launcher.executeCommand("run", MainVerticle.class.getName());
  }
}
