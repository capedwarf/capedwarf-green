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

import nl.captcha.Captcha;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;

import javax.enterprise.context.SessionScoped;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@SessionScoped
public class SimpleCaptchaService extends AbstractCaptchaService {
    private static final long serialVersionUID = 1L;

    private int widht = 200;
    private int height = 40;

    private volatile Captcha captcha;

    public void serveCaptcha(String id, Locale locale, String format, OutputStream out) throws IOException {
        captcha = new Captcha.Builder(widht, height)
                .addText()
                .addBackground(new GradiatedBackgroundProducer())
                .gimp()
                .addNoise()
                .addBorder()
                .build();
        renderCaptcha(captcha.getImage(), format, out);
    }

    public boolean verifyCaptcha(String id, String value) {
        return captcha != null && captcha.isCorrect(value);
    }

    public void setWidht(int widht) {
        this.widht = widht;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
