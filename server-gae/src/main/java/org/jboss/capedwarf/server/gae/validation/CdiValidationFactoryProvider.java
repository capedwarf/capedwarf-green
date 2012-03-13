/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
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

package org.jboss.capedwarf.server.gae.validation;

import org.jboss.capedwarf.server.api.validation.BlobSize;
import org.jboss.capedwarf.server.api.validation.BlobSizeValidator;
import org.jboss.capedwarf.server.api.validation.JpaEmail;
import org.jboss.capedwarf.server.api.validation.JpaEmailValidator;
import org.jboss.capedwarf.validation.AbstractValidatorFactoryProvider;
import org.jboss.capedwarf.validation.SimpleValidatorFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.validation.ValidatorFactory;


/**
 * javax.validation + jpa.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class CdiValidationFactoryProvider extends AbstractValidatorFactoryProvider {
    @Produces
    @ApplicationScoped
    public ValidatorFactory createFactory() {
        SimpleValidatorFactory factory = new SimpleValidatorFactory();
        factory.put(BlobSize.class, BlobSizeValidator.class);
        factory.put(JpaEmail.class, JpaEmailValidator.class);
        return factory;
    }

    /*
    protected String getFactoryClassName()
    {
       return "org.hibernate.validator.util.LazyValidatorFactory";
    }
    */
}