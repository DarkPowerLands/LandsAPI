
package ru.landsproject.api.configuration.abstractconfigure.transformation;

import java.util.Comparator;

import static ru.landsproject.api.configuration.abstractconfigure.transformation.ConfigurationTransformation.WILDCARD_OBJECT;

class NodePathComparator implements Comparator<Object[]> {
    @Override
    public int compare(Object[] a, Object[] b) {
        for (int i = 0; i < Math.min(a.length, b.length); ++i) {
            if (a[i] == WILDCARD_OBJECT || b[i] == WILDCARD_OBJECT) {
                if (a[i] != WILDCARD_OBJECT || b[i] != WILDCARD_OBJECT) {
                    return a[i] == WILDCARD_OBJECT ? 1 : -1;
                }

            } else if (a[i] instanceof Comparable) {
                @SuppressWarnings("unchecked")
                final int comp = ((Comparable) a[i]).compareTo(b[i]);
                switch (comp) {
                    case 0:
                        break;
                    default:
                        return comp;
                }
            } else {
                return a[i].equals(b[i]) ? 0 : Integer.valueOf(a[i].hashCode()).compareTo(b[i].hashCode());
            }
        }
        if (a.length > b.length) {
            return -1;
        } else if (b.length > a.length) {
            return 1;
        } else {
            return 0;
        }

    }
}
