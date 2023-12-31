package ru.landsproject.api.configuration.abstractconfigure.util;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class MapFactories {
    private MapFactories() {
        // Nope
    }

    private static class SynchronizedWrapper<K, V> implements ConcurrentMap<K, V> {
        private final Map<K, V> wrapped;

        private SynchronizedWrapper(Map<K, V> wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public V putIfAbsent(K k, V v) {
            synchronized (wrapped) {
                if (!wrapped.containsKey(k)) {
                    wrapped.put(k, v);
                } else {
                    return wrapped.get(k);
                }
            }
            return null;
        }

        @Override
        public boolean remove(Object key, Object expected) {
            synchronized (wrapped) {
                if (Objects.equal(expected, wrapped.get(key))) {
                    return wrapped.remove(key) != null;
                }
            }
            return false;
        }

        @Override
        public boolean replace(K key, V old, V replace) {
            synchronized (wrapped) {
                if (Objects.equal(old, wrapped.get(key))) {
                    wrapped.put(key, replace);
                    return true;
                }
            }
            return false;
        }

        @Override
        public V replace(K k, V v) {
            synchronized (wrapped) {
                if (wrapped.containsKey(k)) {
                    return wrapped.put(k, v);
                }
            }
            return null;
        }

        @Override
        public int size() {
            synchronized (wrapped) {
                return wrapped.size();
            }
        }

        @Override
        public boolean isEmpty() {
            synchronized (wrapped) {
                return wrapped.isEmpty();
            }
        }

        @Override
        public boolean containsKey(Object o) {
            synchronized (wrapped) {
                return wrapped.containsKey(o);
            }
        }

        @Override
        public boolean containsValue(Object o) {
            synchronized (wrapped) {
                return wrapped.containsKey(o);
            }
        }

        @Override
        public V get(Object o) {
            synchronized (wrapped) {
                return wrapped.get(o);
            }
        }

        @Override
        public V put(K k, V v) {
            synchronized (wrapped) {
                return wrapped.put(k, v);
            }
        }

        @Override
        public V remove(Object o) {
            synchronized (wrapped) {
                return wrapped.remove(o);
            }
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> map) {
            synchronized (wrapped) {
                wrapped.putAll(map);
            }
        }

        @Override
        public void clear() {
            synchronized (wrapped) {
                wrapped.clear();
            }
        }

        @Override
        public Set<K> keySet() {
            synchronized (wrapped) {
                return ImmutableSet.copyOf(wrapped.keySet());
            }
        }

        @Override
        public Collection<V> values() {
            synchronized (wrapped) {
                return ImmutableSet.copyOf(wrapped.values());
            }
        }

        @Override
        public Set<Entry<K, V>> entrySet() {
            synchronized (wrapped) {
                return ImmutableSet.copyOf(wrapped.entrySet());
            }
        }
    }

    public static MapFactory  unordered() {
        return new EqualsSupplier() {
            @Override
            public <K, V> ConcurrentMap<K, V> create() {
                return new ConcurrentHashMap<>();
            }
        };
    }

    public static MapFactory sorted(final Comparator<Object> comparator) {
        return new EqualsSupplier() {
            @Override
            public <K, V> ConcurrentMap<K, V> create() {
                return new ConcurrentSkipListMap<>(comparator);
            }
        };
    }

    public static MapFactory sortedNatural() {
        return new EqualsSupplier() {
            @Override
            public <K, V> ConcurrentMap<K, V> create() {
                return new ConcurrentSkipListMap<>();
            }
        };
    }

    public static MapFactory insertionOrdered() {
        return new EqualsSupplier() {
            @Override
            public <K, V> ConcurrentMap<K, V> create() {
                return new SynchronizedWrapper<>(new LinkedHashMap<>());
            }
        };
    }

    private static abstract class EqualsSupplier implements MapFactory {
        @Override
        public boolean equals(Object o) {
            return o.getClass().equals(getClass());
        }
    }


}
