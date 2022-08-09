package net.acidfrog.kronos.toolkit.benchmark;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.StampedLock;

import net.acidfrog.kronos.scribe.ANSI;
import net.acidfrog.kronos.scribe.Level;
import net.acidfrog.kronos.scribe.Logger;
import net.acidfrog.kronos.scribe.LoggerFactory;
import net.acidfrog.kronos.toolkit.Chrono;
import net.acidfrog.kronos.toolkit.Chrono.TimeUnit;

public final class Benchmark<T> implements Comparable<Benchmark<T>> {

    private static final int LOG_PADDING = "00:00:00.000\s[main]\sINFO\s\sBenchmark.java\s-\s".length();

    private final Class<T> clazz;
    private final T[] objects;
    private final Map<String, Method> methods;
    private final Map<Method, Optional<Class<?>[]>> args;
    private final Map<Method, Telemetry> telemetry;
    private final Logger logger;
    private final StampedLock lock;
    private boolean logOutput;

    private Benchmark(Class<T> clazz, T[] objects) {
        this.clazz = clazz;
        this.objects = objects;
        this.methods = new HashMap<String, Method>();
        this.args = new HashMap<Method, Optional<Class<?>[]>>();
        this.telemetry = new HashMap<Method, Telemetry>();
        this.logger = LoggerFactory.get(Benchmark.class).setLevel(Level.INFO);
        this.lock = new StampedLock();
        this.logOutput = true;

        var ms = clazz.getMethods();
        for (int i = 0; i < ms.length; i++) {
            var method = ms[i];
            var argArr = method.getParameterTypes();
            methods.put(method.getName(), method);
            args.put(method, Optional.of(argArr));
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Benchmark<T> prepare(T... objects) {
        if (objects.length == 0) {
            throw new IllegalArgumentException("objects.length == 0");
        }

        return new Benchmark<T>((Class<T>) objects[0].getClass(), objects);
    }

    @SuppressWarnings("unchecked")
    public static <T> Benchmark<T> prepare(Class<T> clazz, T... objects) {
        return new Benchmark<T>(clazz, objects);
    }

    public void setLogOutput(boolean logOutput) {
        this.logOutput = logOutput;
    }

    private final Telemetry reusableTelemetry = new Telemetry();
    private final StringBuilder reusableStringBuilder = new StringBuilder();

    public static Object[] generateRandomPrimitiveArguments(Class<?>[] argTypes, long maxValue) {
        int aSize = argTypes.length;
        var args = new Object[aSize];

        for (int i = 0; i < aSize; i++) {
            var argType = argTypes[i];
            
            if (argType == int.class) {
                args[i] = (int) (Math.random() * ((int) (maxValue)));
                continue;
            }
            if (argType == long.class) {
                args[i] = (long) (Math.random() * maxValue);
                continue;
            }
            if (argType == double.class) {
                args[i] = (double) (Math.random() * maxValue);
                continue;
            }
            if (argType == float.class) {
                args[i] = (float) (Math.random() * maxValue);
                continue;
            }
            if (argType == short.class) {
                args[i] = (short) (Math.random() * Math.min(Short.MAX_VALUE, maxValue));
                continue;
            }
            if (argType == byte.class) {
                args[i] = (byte) (Math.random() * Math.min(Byte.MAX_VALUE, maxValue));
                continue;
            }
            if (argType == boolean.class) {
                args[i] = Math.random() > 0.5;
                continue;
            }
            if (argType == char.class) {
                args[i] = (char) (Math.random() * Math.min(Character.MAX_VALUE, maxValue));
                continue;
            }
            
            throw new IllegalArgumentException("Unknown primitive type: " + argType);
        }

        return args;
    }

    public static Object[] generateArguments(ArgumentFactory<?>... factories) {
        int fSize = factories.length;
        var args = new Object[fSize];

        for (int i = 0; i < fSize; i++) {
            var factory = factories[i];
            args[i] = factory.create();
        }

        return args;
    }

    public Telemetry simulate(int iterations, String methodName) {
        var argTypes = args.get(methods.get(methodName)).get();

        // check if argTypes are primitive
        for (var argType : argTypes) {
            if (!argType.isPrimitive()) {
                logger.warn("Currently only primitive types are supported for random arguments. Null will now be returned.");
                return null;
            }
        }

        return run(iterations, methodName, generateRandomPrimitiveArguments(argTypes, 1_000_000));
    }

    public Telemetry run(int iterations, String methodName, Object... methodArgs) {
        var method = methods.get(methodName);
        if (method == null) {
            throw new IllegalArgumentException("methodName not found: " + methodName);
        }
        var argTypes = args.get(method).orElse(null);

        long[] times = new long[iterations];
        long shortest = Long.MAX_VALUE;
        long average = 0;
        long longest = 0;
        long total = 0;

        int iteration = 0;
        do {
            if (objects == null || objects.length == 0) {
                times[iteration++] = execute(null, method, methodArgs);
                continue;
            }
            for (var obj : objects) {
                times[iteration++] = execute(obj, method, methodArgs);
            }
        } while (iteration < iterations);

        for (var t : times) {
            average += t;

            if (t < shortest) {
                shortest = t;
            }
            if (t > longest) {
                longest = t;
            }
        }
        total = average;
        average /= iterations;

        var s = new Chrono.Time(shortest, TimeUnit.NANOSECONDS);
        if (s.convert(TimeUnit.SECONDS).value() >= 1) {
            s = s.convert(TimeUnit.SECONDS);
        } else if (s.convert(TimeUnit.MILLISECONDS).value() >= 1) {
            s = s.convert(TimeUnit.MILLISECONDS);
        } else if (s.convert(TimeUnit.MICROSECONDS).value() >= 1) {
            s = s.convert(TimeUnit.MICROSECONDS);
        } else if (s.convert(TimeUnit.NANOSECONDS).value() >= 1) {
            s = s.convert(TimeUnit.NANOSECONDS);
        }

        var l = new Chrono.Time(longest, TimeUnit.NANOSECONDS);
        if (l.convert(TimeUnit.SECONDS).value() >= 1) {
            l = l.convert(TimeUnit.SECONDS);
        } else if (l.convert(TimeUnit.MILLISECONDS).value() >= 1) {
            l = l.convert(TimeUnit.MILLISECONDS);
        } else if (l.convert(TimeUnit.MICROSECONDS).value() >= 1) {
            l = l.convert(TimeUnit.MICROSECONDS);
        } else if (l.convert(TimeUnit.NANOSECONDS).value() >= 1) {
            l = l.convert(TimeUnit.NANOSECONDS);
        }

        var a = new Chrono.Time(average, TimeUnit.NANOSECONDS);
        if (a.convert(TimeUnit.SECONDS).value() >= 1) {
            a = a.convert(TimeUnit.SECONDS);
        } else if (a.convert(TimeUnit.MILLISECONDS).value() >= 1) {
            a = a.convert(TimeUnit.MILLISECONDS);
        } else if (a.convert(TimeUnit.MICROSECONDS).value() >= 1) {
            a = a.convert(TimeUnit.MICROSECONDS);
        } else if (a.convert(TimeUnit.NANOSECONDS).value() >= 1) {
            a = a.convert(TimeUnit.NANOSECONDS);
        }

        var t = new Chrono.Time(total, TimeUnit.NANOSECONDS);
        if (t.convert(TimeUnit.SECONDS).value() >= 1) {
            t = t.convert(TimeUnit.SECONDS);
        } else if (t.convert(TimeUnit.MILLISECONDS).value() >= 1) {
            t = t.convert(TimeUnit.MILLISECONDS);
        } else if (t.convert(TimeUnit.MICROSECONDS).value() >= 1) {
            t = t.convert(TimeUnit.MICROSECONDS);
        } else if (t.convert(TimeUnit.NANOSECONDS).value() >= 1) {
            t = t.convert(TimeUnit.NANOSECONDS);
        }

        reusableStringBuilder.setLength(0);
        if (!(argTypes == null)) {
            for (var c : argTypes) {
                reusableStringBuilder.append(c.getSimpleName()).append(", ");
            }
            if (reusableStringBuilder.length() > 2) {
                reusableStringBuilder.setLength(reusableStringBuilder.length() - 2);
            }
        }

        reusableTelemetry.set(method.getName() + "(" + reusableStringBuilder.toString() + ")", iterations, s, l, a, t);
        telemetry.put(method, (Telemetry) reusableTelemetry.clone());
        
        if (logOutput) {
            logger.info(clazz.getSimpleName() + "::" + reusableTelemetry.toString() + "\n" + new String(new char[LOG_PADDING]).replace("\0", " ") + "Args: " + Arrays.toString(methodArgs));
        }

        return reusableTelemetry;
    }

    private long execute(Object object, Method method, Object... args) {
        long start = 1, end = 0; // results in -1 if not executed
        
        long stamp = lock.writeLock();
        try {
            if (!lock.validate(stamp)) {
                stamp = lock.writeLock();
            }

            start = Chrono.now();
            method.invoke(object, args);
            end = Chrono.now();
        } catch (Exception e) {
            logger.fatal("Method failed to execute." + ANSI.NEWLINE, e);
        } finally {
            // executes after return statement still
            lock.unlock(stamp);
        }

        return end - start;
    }

    public Telemetry getTelemetry(String methodName) {
        return telemetry.get(methods.get(methodName));
    }

    public Telemetry getTelemetry(Method method) {
        return telemetry.get(method);
    }

    @Override
    public int compareTo(Benchmark<T> comp) {
        var t0 = this.telemetry.values().stream().mapToLong(Telemetry::comparableRuntime).toArray();
        var t1 = comp.telemetry.values().stream().mapToLong(Telemetry::comparableRuntime).toArray();
        return Long.compare(Arrays.stream(t0).sum(), Arrays.stream(t1).sum());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (var method : telemetry.keySet()) {
            sb.append(method.getName())
            .append(": ")
            .append(telemetry.get(method))
            .append(ANSI.NEWLINE);
        }
        return sb.toString();
    }
}
