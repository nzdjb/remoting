/*
 * The MIT License
 *
 * Copyright (c) 2019, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jenkinsci.remoting.engine;

import hudson.remoting.Base64;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public abstract class JnlpEndpointResolver {

    public abstract JnlpAgentEndpoint resolve() throws IOException;

    public abstract void waitForReady() throws InterruptedException;

    protected RSAPublicKey getIdentity(String base64EncodedIdentity) throws InvalidKeySpecException {
        if (base64EncodedIdentity == null) return null;
        try {
            byte[] encodedKey = Base64.decode(base64EncodedIdentity);
            if (encodedKey == null) return null;
            X509EncodedKeySpec spec = new X509EncodedKeySpec(encodedKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) kf.generatePublic(spec);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("The Java Language Specification mandates RSA as a supported algorithm.", e);
        }
    }

    protected static class HostPort {
        String host;
        int port;
    }

    protected HostPort splitHostPort(String value) throws IOException {
        String[] tokens = value.split(":", 3);
        if (tokens.length != 2) {
            throw new IOException("Illegal host-port parameter: " + value);
        }
        HostPort hostPort = new HostPort();
        hostPort.host = tokens[0];
        hostPort.port = Integer.parseInt(tokens[1]);
        return hostPort;
    }

}
