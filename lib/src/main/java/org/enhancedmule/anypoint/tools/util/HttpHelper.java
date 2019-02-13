package org.enhancedmule.anypoint.tools.util;

import com.kloudtek.util.Base64;
import com.kloudtek.util.ThreadUtils;
import com.kloudtek.util.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.enhancedmule.anypoint.tools.AnypointClient;
import org.enhancedmule.anypoint.tools.Environment;
import org.enhancedmule.anypoint.tools.HttpException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpHelper implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(HttpHelper.class);
    private static final String HEADER_AUTH = "Authorization";
    private transient CloseableHttpClient httpClient;
    private String auth;
    private AnypointClient client;
    private String username;
    private String password;
    private int maxRetries = 4;
    private long retryDelay = 1000L;

    public HttpHelper() {
    }

    public HttpHelper(AnypointClient client, String username, String password) {
        this(HttpClients.createMinimal(), client, username, password);
    }

    public HttpHelper(CloseableHttpClient httpClient, AnypointClient client, String username, String password) {
        this.client = client;
        this.username = username;
        this.password = password;
        this.httpClient = httpClient;
    }

    @Override
    public void close() throws IOException {
        httpClient.close();
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        httpClient = HttpClients.createMinimal();
    }

    public void httpGetBasicAuth(String path, OutputStream outputStream) throws HttpException {
        logger.debug("HTTP GET W/ BASIC AUTH: " + path);
        HttpGet request = new HttpGet(convertPath(path));
        setBasicAuthHeader(request);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            verifyStatusCode(request, response);
            if (response.getEntity() == null) {
                throw new HttpException("Not body returned by url " + request.getURI());
            }
            IOUtils.copy(response.getEntity().getContent(), outputStream);
        } catch (IOException e) {
            throw new HttpException(e.getMessage(), e);
        }
    }

    public String httpGet(String path, Environment env) throws HttpException {
        logger.debug("HTTP GET " + path + " env=" + env);
        return executeWithEnv(new HttpGet(convertPath(path)), env);
    }

    public String httpGet(String path) throws HttpException {
        logger.debug("HTTP GET " + path);
        return executeWrapper(new HttpGet(convertPath(path)), null);
    }

    public String httpGet(String path, Map<String, String> headers) throws HttpException {
        logger.debug("HTTP GET " + path);
        HttpGet method = new HttpGet(convertPath(path));
        setHeader(headers, method);
        return executeWrapper(method, null);
    }

    public String httpGetWithOrgAndOwner(String path, String orgId, String ownerId) throws HttpException {
        logger.debug("HTTP GET " + path);
        return httpGet(path, createOrgAndOwnerHeaders(orgId, ownerId));
    }

    public String httpPost(String path, Object data, Environment env) throws HttpException {
        logger.debug("HTTP POST " + path + " env=" + env + " data=" + data);
        return executeWithDataAndEnv(new HttpPost(convertPath(path)), data, env);
    }

    public String httpPostWithOrgAndOwner(String path, Object data, String orgId, String ownerId) throws HttpException {
        return httpPost(path, data, createOrgAndOwnerHeaders(orgId, ownerId));
    }

    public String httpPost(String path, Object data, Map<String, String> headers) throws HttpException {
        logger.debug("HTTP POST " + path + " headers=" + headers + " data=" + data);
        HttpPost method = new HttpPost(convertPath(path));
        setHeader(headers, method);
        return execute(method, data);
    }

    public String httpPost(String path, Object data) throws HttpException {
        logger.debug("HTTP POST " + path + " data=" + data);
        return execute(new HttpPost(convertPath(path)), data);
    }

    public String httpPatch(String path, Object data) throws HttpException {
        logger.debug("HTTP PATCH " + path + " data=" + data);
        return execute(new HttpPatch(convertPath(path)), data);
    }

    public String httpPut(String path, Object data) throws HttpException {
        logger.debug("HTTP PUT " + path + " data=" + data);
        return execute(new HttpPut(convertPath(path)), data);
    }

    public String httpPut(String path, Object data, Environment environment) throws HttpException {
        logger.debug("HTTP PUT " + path);
        return executeWithDataAndEnv(new HttpPut(convertPath(path)), data, environment);
    }

    public String httpDelete(String path) throws HttpException {
        logger.debug("HTTP DELETE " + path);
        return executeWrapper(new HttpDelete(convertPath(path)), null);
    }

    public String httpDelete(String path, Object data) throws HttpException {
        logger.debug("HTTP DELETE " + path + " data=" + data);
        return execute(new HttpDeleteWithBody(convertPath(path)), data);
    }

    public String httpDelete(@NotNull String path, @NotNull Environment env) throws HttpException {
        logger.debug("HTTP DELETE " + path + " env=" + env);
        return executeWithEnv(new HttpDelete(convertPath(path)), env);
    }

    public MultiPartRequest createMultiPartPostRequest(String url, Environment environment) {
        HttpPost request = new HttpPost(convertPath(url));
        environment.addHeaders(request);
        return new MultiPartRequest(request);
    }

    public MultiPartRequest createMultiPartPatchRequest(String url, Environment environment) {
        HttpPatch request = new HttpPatch(convertPath(url));
        environment.addHeaders(request);
        return new MultiPartRequest(request);
    }


    public MultiPartRequest createMultiPartPutRequest(String url, Environment environment) {
        HttpPut request = new HttpPut(convertPath(url));
        environment.addHeaders(request);
        return new MultiPartRequest(request);
    }

    private String execute(@NotNull HttpEntityEnclosingRequestBase method, Object data) throws HttpException {
        if (data != null) {
            if (data instanceof HttpEntity) {
                method.setEntity((HttpEntity) data);
            } else {
                method.setHeader("Content-Type", "application/json");
                method.setEntity(new ByteArrayEntity(client.getJsonHelper().toJson(data)));
            }
        }
        return executeWrapper(method, null);
    }

    private String executeWithDataAndEnv(@NotNull HttpEntityEnclosingRequestBase method, @NotNull Object data, @NotNull Environment env) throws HttpException {
        env.addHeaders(method);
        return execute(method, data);
    }

    private String executeWithEnv(@NotNull HttpRequestBase method, @NotNull Environment env) throws HttpException {
        env.addHeaders(method);
        return executeWrapper(method, null);
    }

    protected String executeWrapper(@NotNull HttpRequestBase method, MultiPartRequest multiPartRequest) throws HttpException {
        return executeWrapper(method, multiPartRequest, 0);
    }

    private String executeWrapper(@NotNull HttpRequestBase method, MultiPartRequest multiPartRequest, int attempt) throws HttpException {
        boolean authenticating = method.getURI().getPath().equals(AnypointClient.LOGIN_PATH);
        if (auth == null && !authenticating) {
            auth = client.authenticate(username, password);
        }
        try {
            if (multiPartRequest != null) {
                ((HttpEntityEnclosingRequestBase) method).setEntity(multiPartRequest.toEntity());
            }
            return doExecute(method);
        } catch (HttpException e) {
            if (e.getStatusCode() == 403 || e.getStatusCode() == 401 && !authenticating) {
                client.authenticate(username, password);
                return doExecute(method);
            } else if (e.getStatusCode() >= 500) {
                attempt++;
                if (attempt > maxRetries) {
                    throw e;
                } else {
                    ThreadUtils.sleep(retryDelay);
                    return executeWrapper(method, multiPartRequest, attempt);
                }
            } else {
                throw e;
            }
        }
    }

    @Nullable
    private String doExecute(HttpRequestBase method) throws HttpException {
        if (auth != null && method.getFirstHeader(HEADER_AUTH) == null) {
            if (auth.startsWith("bearer ")) {
                auth = "Bearer " + auth.substring(7);
            }
            method.setHeader(HEADER_AUTH, auth);
        }
        try (CloseableHttpResponse response = httpClient.execute(method)) {
            verifyStatusCode(method, response);
            if (response.getEntity() != null && response.getEntity().getContent() != null) {
                String resStr = IOUtils.toString(response.getEntity().getContent());
                logger.debug("RESULT CONTENT: " + resStr);
                return resStr;
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    private void verifyStatusCode(HttpRequestBase method, CloseableHttpResponse response) throws IOException, HttpException {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode < 200 || statusCode > 299) {
            String errMsg;
            if (response.getEntity() != null && response.getEntity().getContent() != null) {
                errMsg = " : " + IOUtils.toString(response.getEntity().getContent());
            } else {
                errMsg = "";
            }
            throw new HttpException("Anypoint returned status code " + statusCode + " - url: " + method.getURI() + " - err: " + errMsg, statusCode);
        }
    }

    public synchronized void setProxy(@NotNull String scheme, @NotNull String host, int port,
                                      @Nullable String username, @Nullable String password) {
        if (httpClient != null) {
            IOUtils.close(httpClient);
        }
        HttpClientBuilder builder = HttpClients.custom();
        HttpHost proxyHost = new HttpHost(host, port, scheme);
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxyHost);
        builder = builder.setRoutePlanner(routePlanner);
        if (username != null && password != null) {
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope(proxyHost), new UsernamePasswordCredentials(username, password));
            builder = builder.setDefaultCredentialsProvider(credsProvider);
        }
        httpClient = builder.build();
    }

    public synchronized void unsetProxy() {
        if (httpClient != null) {
            IOUtils.close(httpClient);
        }
        httpClient = HttpClients.createMinimal();
    }

    public class MultiPartRequest {
        private Map<String, Object> parts = new HashMap<>();
        private HttpEntityEnclosingRequestBase request;

        MultiPartRequest(HttpEntityEnclosingRequestBase request) {
            this.request = request;
        }

        public MultiPartRequest addText(@NotNull String name, @NotNull String value) {
            parts.put(name, value);
            return this;
        }

        public MultiPartRequest addBinary(@NotNull String name, @NotNull StreamSource streamSource) {
            parts.put(name, streamSource);
            return this;
        }

        HttpEntity toEntity() throws HttpException {
            try {
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                for (Map.Entry<String, Object> e : parts.entrySet()) {
                    if (e.getValue() instanceof String) {
                        builder.addTextBody(e.getKey(), (String) e.getValue());
                    } else if (e.getValue() instanceof StreamSource) {
                        builder.addBinaryBody(e.getKey(), ((StreamSource) e.getValue()).createInputStream(),
                                ContentType.APPLICATION_OCTET_STREAM, ((StreamSource) e.getValue()).getFileName());
                    }
                }
                return builder.build();
            } catch (IOException e) {
                throw new HttpException("Failed to read data to send: " + e.getMessage(), e);
            }
        }

        public String execute() throws HttpException, IOException {
            try {
                logger.debug("HTTP {}", request);
                return HttpHelper.this.executeWrapper(request, this);
            } catch (RuntimeIOException e) {
                throw e.getIOException();
            }
        }
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public long getRetryDelay() {
        return retryDelay;
    }

    public void setRetryDelay(long retryDelay) {
        this.retryDelay = retryDelay;
    }

    public class RuntimeIOException extends RuntimeException {
        RuntimeIOException(@NotNull IOException ioException) {
            super(ioException.getMessage(), ioException);
        }

        @NotNull
        IOException getIOException() {
            return (IOException) getCause();
        }
    }

    private static void setHeader(Map<String, String> headers, HttpRequestBase method) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            method.setHeader(entry.getKey(), entry.getValue());
        }
    }

    public void setClient(AnypointClient client) {
        this.client = client;
    }

    protected static String convertPath(String path) {
        return path.startsWith("/") ? "https://anypoint.mulesoft.com" + path : path;
    }

    @NotNull
    private static HashMap<String, String> createOrgAndOwnerHeaders(String orgId, String ownerId) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-organization-id", orgId);
        headers.put("x-owner-id", ownerId);
        return headers;
    }

    private HttpRequestBase setBasicAuthHeader(HttpRequestBase request) {
        String authStr = username + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(authStr.getBytes(StandardCharsets.ISO_8859_1));
        String authHeader = "Basic " + new String(encodedAuth);
        request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
        return request;
    }

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }
}
