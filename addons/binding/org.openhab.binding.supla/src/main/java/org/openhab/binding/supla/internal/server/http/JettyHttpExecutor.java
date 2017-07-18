package org.openhab.binding.supla.internal.server.http;


import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.BytesContentProvider;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.openhab.binding.supla.internal.supla.entities.SuplaCloudServer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.google.common.base.Preconditions.checkNotNull;

public final class JettyHttpExecutor implements HttpExecutor, AutoCloseable {
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String PATCH = "PATCH";

    private final SuplaCloudServer server;
    private final HttpClient httpClient;

    public JettyHttpExecutor(SuplaCloudServer server) {
        this.server = checkNotNull(server);
        SslContextFactory sslContextFactory = new SslContextFactory();
        httpClient = new HttpClient(sslContextFactory);
        httpClient.setFollowRedirects(false);
        try {
            httpClient.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private org.eclipse.jetty.client.api.Request common(Request request, String method) {
        org.eclipse.jetty.client.api.Request jetty = httpClient.newRequest(buildUrl(request)).method(method);
        for (Header header : request.getHeaders()) {
            jetty = jetty.header(header.getKey(), header.getValue());
        }
        return jetty;
    }
    private Response noBodyRequest(Request request, String method) {
        return invoke(common(request, method));
    }

    private Response bodyRequest(Request request, String method, Body body) {
        final org.eclipse.jetty.client.api.Request jetty = common(request, method)
                .content(new BytesContentProvider(body.buildBytesToSend()), "application/json; charset=UTF-8");
        return invoke(jetty);
    }

    private Response invoke(org.eclipse.jetty.client.api.Request request) {
        try {
            final ContentResponse response = request.send();
            return new Response(response.getStatus(), response.getContentAsString());
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Response get(Request request) {
        return noBodyRequest(request, GET);
    }

    @Override
    public Response post(Request request, Body body) {
        return bodyRequest(request, POST, body);
    }

    @Override
    public Response patch(Request request, Body body) {
        return bodyRequest(request, PATCH, body);
    }

    @Override
    public void close() {
        try {
            httpClient.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String buildUrl(Request request) {
        return server.getServer() + request.getPath();
    }
}
