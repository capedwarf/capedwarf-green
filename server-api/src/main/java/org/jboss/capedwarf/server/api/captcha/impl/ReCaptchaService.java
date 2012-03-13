/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.capedwarf.server.api.captcha.impl;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaFactory;
import net.tanesha.recaptcha.ReCaptchaResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Alternative
@ApplicationScoped
public class ReCaptchaService extends AbstractCaptchaService {
    private static final long serialVersionUID = 1L;

    private String publicKey;
    private String privateKey;
    private volatile ReCaptcha captcha;

    private volatile HttpServletRequest request;

    protected synchronized ReCaptcha getCaptcha() {
        if (captcha == null)
            captcha = ReCaptchaFactory.newReCaptcha(publicKey, privateKey, false);
        return captcha;
    }

    public void serveCaptcha(String id, Locale locale, String format, OutputStream out) throws IOException {
        String error = request.getParameter("error");
        String captchaScript = getCaptcha().createRecaptchaHtml(error, null);
        out.write(captchaScript.getBytes());
    }

    public boolean verifyCaptcha(String id, String value) {
        ReCaptchaResponse response = getCaptcha().checkAnswer(
                request.getRemoteAddr(),
                request.getParameter("recaptcha_challenge_field"),
                request.getParameter("recaptcha_response_field")
        );
        return response.isValid();
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    @Inject
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
}
