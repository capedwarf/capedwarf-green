package org.jboss.capedwarf.validation.api;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * javax.validation email validator.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class PatternConstraintValidator extends AbstractPatternConstraintValidator<javax.validation.constraints.Pattern> {
    protected Pattern createPattern(javax.validation.constraints.Pattern parameters) {
        javax.validation.constraints.Pattern.Flag flags[] = parameters.flags();
        int intFlag = 0;
        for (javax.validation.constraints.Pattern.Flag flag : flags) {
            intFlag = intFlag | flag.getValue();
        }

        try {
            return java.util.regex.Pattern.compile(parameters.regexp(), intFlag);
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Invalid regular expression.", e);
        }
    }
}
