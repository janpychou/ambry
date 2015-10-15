package com.github.ambry.rest;

import com.codahale.metrics.MetricRegistry;
import com.github.ambry.config.VerifiableProperties;
import com.github.ambry.router.InMemoryRouter;
import java.io.IOException;
import java.util.Properties;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Tests functionality of {@link NettyServerFactory}.
 */
public class NettyServerFactoryTest {

  /**
   * Checks to see that getting the default {@link NioServer} (currently {@link NettyServer}) works.
   * @throws InstantiationException
   * @throws IOException
   */
  @Test
  public void getNettyServerTest()
      throws InstantiationException, IOException {
    // dud properties. server should pick up defaults
    Properties properties = new Properties();
    VerifiableProperties verifiableProperties = new VerifiableProperties(properties);
    RestRequestHandlerController requestHandlerController = getRestRequestHandlerController();

    NioServerFactory nioServerFactory =
        new NettyServerFactory(verifiableProperties, new MetricRegistry(), requestHandlerController);
    NioServer nioServer = nioServerFactory.getNioServer();
    assertNotNull("No NioServer returned", nioServer);
    assertEquals("Did not receive a NettyServer instance", NettyServer.class.getCanonicalName(),
        nioServer.getClass().getCanonicalName());
  }

  /**
   * Tests instantiation of {@link NettyServerFactory} with bad input.
   * @throws InstantiationException
   * @throws IOException
   */
  @Test
  public void getNettyServerFactoryWithBadInputTest()
      throws InstantiationException, IOException {
    // dud properties. server should pick up defaults
    Properties properties = new Properties();
    VerifiableProperties verifiableProperties = new VerifiableProperties(properties);
    MetricRegistry metricRegistry = new MetricRegistry();
    RestRequestHandlerController requestHandlerController = getRestRequestHandlerController();

    // VerifiableProperties null.
    try {
      new NettyServerFactory(null, metricRegistry, requestHandlerController);
    } catch (InstantiationException e) {
      // expected. Nothing to do.
    }

    // MetricRegistry null.
    try {
      new NettyServerFactory(verifiableProperties, null, requestHandlerController);
    } catch (InstantiationException e) {
      // expected. Nothing to do.
    }

    // RestRequestHandlerController null.
    try {
      new NettyServerFactory(verifiableProperties, metricRegistry, null);
    } catch (InstantiationException e) {
      // expected. Nothing to do.
    }
  }

  // helpers
  // general
  private RestRequestHandlerController getRestRequestHandlerController()
      throws InstantiationException, IOException {
    RestServerMetrics restServerMetrics = new RestServerMetrics(new MetricRegistry());
    VerifiableProperties verifiableProperties = new VerifiableProperties(new Properties());
    BlobStorageService blobStorageService =
        new MockBlobStorageService(verifiableProperties, new InMemoryRouter(verifiableProperties));
    return new RequestHandlerController(1, restServerMetrics, blobStorageService);
  }
}
