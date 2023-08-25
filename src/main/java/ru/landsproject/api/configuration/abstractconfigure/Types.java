package ru.landsproject.api.configuration.abstractconfigure;

public class Types {
    private Types() {
        // Always nope
    }

    public static String asString(Object value) {
        return value == null ? null : value.toString();
    }

    public static String strictAsString(Object value) {
        return value instanceof String ? (String) value : null;

    }

    public static Float asFloat(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Float) {
            return (Float) value;
        } else if (value instanceof Integer) {
            return ((Number) value).floatValue();
        }

        try {
            return Float.parseFloat(value.toString());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public static Float strictAsFloat(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Float
                || value instanceof Integer) {
            return ((Number) value).floatValue();
        }
        return null;
    }

    public static Double asDouble(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Integer
                || value instanceof Long
                || value instanceof Float) {
            return ((Number) value).doubleValue();
        }

        try {
            return Double.parseDouble(value.toString());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public static Double strictAsDouble(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Double
                || value instanceof Float
                || value instanceof Integer
                || value instanceof Long) {
            return ((Number) value).doubleValue();
        }
        return null;

    }

    public static Integer asInt(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Float
            || value instanceof Double) {
            double val = ((Number) value).doubleValue();
            if (val == Math.floor(val)) {
                return (int) val;
            }
        }

        try {
            return Integer.parseInt(value.toString());
        } catch (IllegalArgumentException ex) {
            return null;
        }

    }

    public static Integer strictAsInt(Object value) {
        if (value == null) {
            return null;
        }

        return value instanceof Integer ? (Integer) value : null;
    }

    public static Long asLong(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof Integer) {
            return ((Number) value).longValue();
        }

        if (value instanceof Float
                || value instanceof Double) {
            double val = ((Number) value).doubleValue();
            if (val == Math.floor(val)) {
                return (long) val;
            }
        }

        try {
            return Long.parseLong(value.toString());
        } catch (IllegalArgumentException ex) {
            return null;
        }

    }

    public static Long strictAsLong(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof Integer) {
            return ((Number) value).longValue();
        }
        return null;
    }

    /**
     * Tries to convert a value to a boolean.
     *
     * If value is a boolean, casts and returns
     * If value is a Number, returns true if value is not 0
     * If value.toString() returns true, t, yes, y, or 1, returns true
     * If value.toString() returns false, f, no, n, or 0, returns false
     * Otherwise returns null
     *
     * @param value The value to convert
     * @return Value converted following rules specified above:w
     */
    public static Boolean asBoolean(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Number) {
            return !value.equals(0);
        }
        final String potential = value.toString();
        if (potential.equals("true")
                || potential.equals("t")
                || potential.equals("yes")
                || potential.equals("y")
                || potential.equals("1")) {
            return true;
        } else if (potential.equals("false")
                || potential.equals("f")
                || potential.equals("no")
                || potential.equals("n")
                || potential.equals("0")) {
            return false;
        }
        return null;
    }

    public static Boolean strictAsBoolean(Object value) {
        if (value == null) {
            return null;
        }

        return value instanceof Boolean ? (Boolean) value : null;
    }
}
