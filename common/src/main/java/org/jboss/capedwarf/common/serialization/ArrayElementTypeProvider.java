package org.jboss.capedwarf.common.serialization;

import java.util.Arrays;
import java.util.List;

/**
 * Array / list based element type provider.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class ArrayElementTypeProvider implements ElementTypeProvider {
    private List<Class<? extends JSONAware>> types;

    public ArrayElementTypeProvider(Class<? extends JSONAware>[] types) {
        if (types == null || types.length == 0)
            throw new IllegalArgumentException("Null or empty types: " + Arrays.toString(types));

        this.types = Arrays.asList(types);
    }

    public ArrayElementTypeProvider(List<Class<? extends JSONAware>> types) {
        if (types == null || types.isEmpty())
            throw new IllegalArgumentException("Null or empty types: " + types);

        this.types = types;
    }

    public Class<? extends JSONAware> getType(int index) {
        return types.get(index);
    }
}
