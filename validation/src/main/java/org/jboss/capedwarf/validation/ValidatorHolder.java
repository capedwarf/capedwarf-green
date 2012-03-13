package org.jboss.capedwarf.validation;

import javax.validation.ConstraintValidatorFactory;
import javax.validation.MessageInterpolator;
import javax.validation.TraversableResolver;

/**
 * javax.validation validator holder.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@SuppressWarnings({"unchecked"})
class ValidatorHolder<T extends ValidatorHolder> {
    private MessageInterpolator messageInterpolator = LazyMessageInterpolator.INSTANCE;
    private TraversableResolver traversableResolver = NoopTraversableResolver.INSTANCE;
    private ConstraintValidatorFactory constraintValidatorFactory = SimpleConstraintValidatorFactory.INSTANCE;

    ValidatorHolder() {
    }

    ValidatorHolder(MessageInterpolator messageInterpolator, TraversableResolver traversableResolver, ConstraintValidatorFactory constraintValidatorFactory) {
        this.messageInterpolator = messageInterpolator;
        this.traversableResolver = traversableResolver;
        this.constraintValidatorFactory = constraintValidatorFactory;
    }

    public T messageInterpolator(MessageInterpolator messageInterpolator) {
        this.messageInterpolator = messageInterpolator;
        return (T) this;
    }

    public T traversableResolver(TraversableResolver traversableResolver) {
        this.traversableResolver = traversableResolver;
        return (T) this;
    }

    public T constraintValidatorFactory(ConstraintValidatorFactory factory) {
        this.constraintValidatorFactory = factory;
        return (T) this;
    }

    public MessageInterpolator getMessageInterpolator() {
        return messageInterpolator;
    }

    public TraversableResolver getTraversableResolver() {
        return traversableResolver;
    }

    public ConstraintValidatorFactory getConstraintValidatorFactory() {
        return constraintValidatorFactory;
    }

    public MessageInterpolator getDefaultMessageInterpolator() {
        return getMessageInterpolator();
    }

    public TraversableResolver getDefaultTraversableResolver() {
        return getTraversableResolver();
    }

    public ConstraintValidatorFactory getDefaultConstraintValidatorFactory() {
        return getConstraintValidatorFactory();
    }
}
