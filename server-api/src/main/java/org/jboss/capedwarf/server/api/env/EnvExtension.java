package org.jboss.capedwarf.server.api.env;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.transaction.UserTransaction;
import javax.validation.ValidatorFactory;
import java.util.HashSet;
import java.util.Set;

/**
 * CapeDwarf Green GAE with CapeDwearf Blue needs to ignore built-in beans.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class EnvExtension implements Extension {

    private static final Set<Class<?>> builtInBeanTypes;
    private static final Set<String> gaeBeans;
    
    private Boolean isGAE;

    static {
        builtInBeanTypes = new HashSet<Class<?>>();
        builtInBeanTypes.add(BeanManager.class);
        builtInBeanTypes.add(ValidatorFactory.class);
        builtInBeanTypes.add(UserTransaction.class);
        // producers to ignore
        gaeBeans = new HashSet<String>();
        gaeBeans.add("org.jboss.capedwarf.server.gae.validation.CdiValidationFactoryProvider");
    }
    
    protected boolean isGAE() {
        if (isGAE == null) {
            isGAE = (System.getProperty("jboss.home.dir") == null); // TODO other containers
        }
        return isGAE;
    }

    protected boolean isBuiltInType(Class<?> clazz) {
        for (Class<?> bibt : builtInBeanTypes) {
            if (bibt.isAssignableFrom(clazz))
                return true;
        }
        final String className = clazz.getName();
        for (String gb : gaeBeans) {
            if (gb.equals(className))
                return true;
        }
        return false;
    }
    
    public void processBeans(@Observes ProcessAnnotatedType pat) {
        if (isGAE() == false) {
            AnnotatedType annotatedType = pat.getAnnotatedType();
            if (isBuiltInType(annotatedType.getJavaClass()))
                pat.veto(); // use built-in impls
        }
    }

}
