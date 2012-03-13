package org.jboss.capedwarf.validation;

import javax.validation.*;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * javax.validation validator.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class SimpleValidator extends ValidatorHolder<SimpleValidator> implements Validator {
    private SimpleValidatorFactory factory;

    SimpleValidator(SimpleValidatorFactory factory) {
        this.factory = factory;
    }

    SimpleValidator(SimpleValidatorFactory factory, MessageInterpolator messageInterpolator, TraversableResolver traversableResolver, ConstraintValidatorFactory constraintValidatorFactory) {
        super(messageInterpolator, traversableResolver, constraintValidatorFactory);
        this.factory = factory;
    }

    @SuppressWarnings({"unchecked"})
    protected <T> void validate(T object, SimpleBeanDescriptor sbd, String propertyName, final Object value, Set<ConstraintViolation<T>> violations) {
        SimplePropertyDescriptor spd = sbd.getConstraintsForProperty(propertyName);
        Class<?> beanClass = sbd.getElementClass();
        for (Class<? extends Annotation> ac : spd.getConstraintAnnotations()) {
            ConstraintValidator cv = factory.getConstraintValidator(ac);
            if (cv == null)
                continue;

            final Annotation annotation = spd.getAnnotation(beanClass, ac);
            cv.initialize(annotation);
            boolean isValid = cv.isValid(value, null);
            if (isValid == false) {
                String messageTemplateKey = spd.getMessageTemplateKey(beanClass);
                if (messageTemplateKey == null)
                    messageTemplateKey = "{" + ac.getName() + ".message}";
                String message = getMessageInterpolator().interpolate(messageTemplateKey, new MessageInterpolator.Context() {
                    public ConstraintDescriptor<?> getConstraintDescriptor() {
                        return new SimpleConstraintDescriptor<Annotation>(factory, annotation);
                    }

                    public Object getValidatedValue() {
                        return value;
                    }
                });
                ConstraintViolation<T> violation = new SimpleConstraintViolation(factory, message, object, beanClass, annotation, propertyName, value);
                violations.add(violation);
            }
        }
    }

    public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
        if (object == null)
            return Collections.emptySet();

        SimpleBeanDescriptor sbd = factory.getBeanDescriptor(object.getClass());
        if (sbd.isBeanConstrained() == false)
            return Collections.emptySet();

        Set<ConstraintViolation<T>> violations = new HashSet<ConstraintViolation<T>>();
        for (SimplePropertyDescriptor spd : sbd.getConstrainedSimpleProperties()) {
            Object value = spd.getValue(object);
            validate(object, sbd, spd.getPropertyName(), value, violations);
        }
        return violations;
    }

    public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups) {
        if (object == null)
            return Collections.emptySet();

        SimpleBeanDescriptor sbd = factory.getBeanDescriptor(object.getClass());
        if (sbd.isBeanConstrained() == false)
            return Collections.emptySet();

        Set<ConstraintViolation<T>> violations = new HashSet<ConstraintViolation<T>>();
        SimplePropertyDescriptor spd = sbd.getConstraintsForProperty(propertyName);
        if (spd == null)
            return Collections.emptySet();

        Object value = spd.getValue(object);
        validate(object, sbd, spd.getPropertyName(), value, violations);
        return violations;
    }

    public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups) {
        SimpleBeanDescriptor sbd = factory.getBeanDescriptor(beanType);
        if (sbd.isBeanConstrained() == false)
            return Collections.emptySet();

        Set<ConstraintViolation<T>> violations = new HashSet<ConstraintViolation<T>>();
        SimplePropertyDescriptor spd = sbd.getConstraintsForProperty(propertyName);
        if (spd == null)
            return Collections.emptySet();

        validate(null, sbd, spd.getPropertyName(), value, violations);
        return violations;
    }

    public BeanDescriptor getConstraintsForClass(Class<?> clazz) {
        return factory.getBeanDescriptor(clazz);
    }

    public <T> T unwrap(Class<T> type) {
        return null;
    }
}
