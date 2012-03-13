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

import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;
import com.octo.captcha.service.image.ImageCaptchaService;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

/**
 * JCaptcha service.
 * Note: it uses com.sun packages internally!
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@ApplicationScoped
@Alternative
public class JCaptchaService extends AbstractCaptchaService {
    private static final long serialVersionUID = 1L;
    private volatile ImageCaptchaService imageCaptchaService;

    protected ImageCaptchaService getImageCaptchaService() {
        if (imageCaptchaService == null)
            imageCaptchaService = new DefaultManageableImageCaptchaService();
        return imageCaptchaService;
    }

    public void serveCaptcha(String id, Locale locale, String format, OutputStream out) throws IOException {
        BufferedImage challenge = getImageCaptchaService().getImageChallengeForID(id, locale);
        renderCaptcha(challenge, format, out);
    }

    public boolean verifyCaptcha(String id, String captcha) {
        Boolean isResponseCorrect = Boolean.FALSE;
        try {
            isResponseCorrect = getImageCaptchaService().validateResponseForID(id, captcha);
        } catch (CaptchaServiceException ignored) {
        }

        if (isResponseCorrect == null)
            throw new IllegalStateException("Invalid CAPTCHA!");

        return isResponseCorrect;
    }

    public void setImageCaptchaService(ImageCaptchaService imageCaptchaService) {
        this.imageCaptchaService = imageCaptchaService;
    }
}
