import com.google.common.base.Strings;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import com.google.myjson.Gson;
import com.google.myjson.GsonBuilder;
import com.google.myjson.reflect.TypeToken;
import com.stumbleupon.async.Deferred;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello people");
        testApacheHttpClient();
    }

    private static void testApacheHttpClient() throws IOException {


        String endpoint = "http://localhost:9999/signUp";
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(endpoint);

        httpPost.addHeader("TTL", String.valueOf(30));

        String payload = "{\n" +
                "  \"name\" : {\n" +
                "    \"firstName\" : \"N\",\n" +
                "    \"lastName\" : \"S\"\n" +
                "  },\n" +
                "  \"emails\" : [ \"testSomething.a@domain.com\" ]\n" +
                "}";
        StringEntity stringEntity = new StringEntity(payload, ContentType.APPLICATION_JSON);
        httpPost.setEntity(stringEntity);

        System.out.println(httpClient.execute(httpPost));
    }

    private static void testGsonConversionToStringClass() {
        String input = "{\n" +
                "  \"streamId\" : \"ios-FA2C2490-A9A5-49F0-ADAA-A69350E351D0\",\n" +
                "  \"authVersion\" : \"1.0\",\n" +
                "  \"userJid\" : \"zziz1iix1swsszi1@go.to\",\n" +
                "  \"authData\" : \"dfzf0davyh5zhzf008ydohfayohvohf5\",\n" +
                "  \"attributes\" : {\n" +
                "    \"ua\" : \"domain.to\\/mobile\\/ios\\/2.27.1.543-Change-teams-order-in-team-switcher-preprod\",\n" +
                "    \"clientIp\" : \"0.0.0.0\"\n" +
                "  },\n" +
                "  \"serverVersion\" : \"4.0\"\n" +
                "}";

        Gson gson = new Gson();
        String output = gson.fromJson(input, String.class);
        System.out.println("Converted:" + input + "\ninto:" + output);
    }


    public static abstract class A {
        public abstract String printWho();

    }

    ;

    public static class A1 extends A {

        @Override
        public String printWho() {
            return "-A1,";
        }

    }

    public static class A2 extends A {

        @Override
        public String printWho() {
            return "-A2,";
        }

    }

    private static volatile ScheduledFuture<?> scheduledFuture;

    private static void testTypeCastInWrongClass() {
        A a;
        A1 a1 = new A1();
        A2 a2;
        a = a1;

        System.out.println("Printing a1:" + a1 + a1.printWho() + a1.getClass().getCanonicalName());
        System.out.println("Printing a:" + a + a.printWho() + a.getClass().getCanonicalName());

        a2 = (A2) a;
        System.out.println("Printing a2:" + a2 + a2.printWho() + a2.getClass().getCanonicalName() + a2.getClass().getCanonicalName());

        System.out.println("setting a as a2");
        a = a2;
        System.out.println("Printing a:" + a + a.printWho() + a.getClass().getCanonicalName());
    }

    private static void testRandomWithSeed() {
        Random random = new Random();
        random.setSeed(123456789);
        System.out.println(random.nextInt(10));
        System.out.println(random.nextInt(10));
        System.out.println(random.nextInt(10));
        System.out.println(random.nextInt(10));
        System.out.println(random.nextInt(10));
        System.out.println(random.nextInt(10));
        System.out.println(random.nextInt(10));
    }


    private static void testHashMapLoadFactor() {
        HashMap<String, String> map = new HashMap<>(3);
        map.put("first", "val1");
        map.put("second", "val2");
        map.put("third", "val3");
    }

    private static void testAtomicIntegerUpdate() {
        AtomicInteger counter = new AtomicInteger(Integer.MAX_VALUE - 3);
        for (int i = 5; i > 0; i--) {
            System.out.print("Updating " + counter.get() + " to ");
            counter.updateAndGet(operand -> (operand + 1) % Integer.MAX_VALUE);
            System.out.println(counter.get());
        }
    }

    private static void testIntegerOverflow() {
        int val = 1;
        for (int i = 1; i <= 32; i++) {
            int newVal = val << 1;
            System.out.println("updated " + val + " to 2^" + i + ":" + newVal);
            val = newVal;
        }

        int maxSignedInt = Integer.MAX_VALUE;
        int minSignedInt = Integer.MIN_VALUE;

        System.out.println(maxSignedInt + "+1=" + (maxSignedInt + 1));
        System.out.println(minSignedInt + "-1=" + (minSignedInt - 1));
        System.out.println(minSignedInt + "*2=" + (minSignedInt * 2));
        System.out.println((maxSignedInt - 1) + "+1 %" + (maxSignedInt) + " = " + maxSignedInt % maxSignedInt);
    }

    private static void testMapMerge() {
        ConcurrentHashMap<Integer, String> map = new ConcurrentHashMap<>();
        map.put(1, "first");
        map.merge(1, "I", new BiFunction<String, String, String>() {
            @Override
            public String apply(String s, String s2) {
                System.out.println("first arg:" + s);
                System.out.println("second arg:" + s2);
                return s;
            }
        });
    }

    private static void testDeferrredCallbackChain() throws Exception {
        Deferred<String> stringDeferred = Deferred.fromResult("initial result");
        Deferred<Long> longDeferred = stringDeferred.addCallback(arg -> {
            System.out.println("chaining to return long value");
            return 32L;
        });
        System.out.println("references of the two deferreds: longDeferred:" + longDeferred + ", stringDeferred:" + stringDeferred);
        String stringDeferredValue = stringDeferred.join();
        Long longDeferredValue = longDeferred.join();
        System.out.println("longDeferred:" + longDeferredValue + ", stringDeferred:" + stringDeferredValue);
    }

    private static void testMapOfOptional() {
        Optional<String> valuePresent = Optional.of("valuePresent");
        Optional<String> valueNull = Optional.ofNullable(null);
        Optional<String> empty = Optional.empty();

        testOptional(valuePresent);
        testOptional(valueNull);
        testOptional(empty);
    }

    private static void testOptional(Optional<String> testArg) {
        System.out.println("---------Testing " + testArg + " ----------");
        Optional<String> nullMapResult = testArg.map((Function<String, String>) s -> {
            System.out.println("mapping method invoked with:" + s + ", for:" + testArg);
            return null;
        });
        System.out.println("nullMapResult:" + nullMapResult + ", orElse:" + nullMapResult.orElse("orElse"));
        Optional<String> nonNullMapResult = testArg.map((Function<String, String>) s -> {
            System.out.println("mapping method invoked with:" + s);
            return "map result";
        });
        System.out.println("nonNullMapResult:" + nonNullMapResult + ", orElse:" + nonNullMapResult.orElse("orElse"));
    }

    private static void testMergeOfConcurrentMap() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        map.put("a", new String("existingValue"));
        map.merge("a", new String("defaultIfNotPresent"), new BiFunction<String, String, String>() {
            @Override
            public String apply(String atomicInteger, String atomicInteger2) {
                System.out.println(String.format("For exisitng key - 1st arg:%s, 2nd arg:%s", atomicInteger, atomicInteger2));
                return new String("mergedValue");
            }
        });
        System.out.println("value after merger for existing key:" + map.get("a"));


        map.merge("b", new String("defaultIfNotPresent"), new BiFunction<String, String, String>() {
            @Override
            public String apply(String atomicInteger, String atomicInteger2) {
                System.out.println(String.format("For non-exisitng key - 1st arg:%s, 2nd arg:%s", atomicInteger, atomicInteger2));
                return new String("mergedValue");
            }
        });
        System.out.println("value after merger for non-existing key:" + map.get("b"));
    }

    private static void testExecutorShutdown() {
        ExecutorShutdownTester.testShutdownOnExecutor();
    }

    private static void testExceptionInDeffered() throws InterruptedException {
        Deferred<String> stringDeferred = new Deferred<>();
        Deferred<Long> ret = stringDeferred.addBoth(s -> {
            System.out.println(String.format("Deferred invoked with arg:%s", s));
            return 12l;
        });
        stringDeferred.callback(new Exception("phata"));
        Thread.sleep(1000);
        System.out.println("finished waiting");
    }

    private static void testFormulaParsingByIntegerParseInt() {
        String formula = "30*24*60*60";
        int parsedInt = Integer.parseInt(formula);
        System.out.println("int:" + parsedInt + " parsed from string:" + parsedInt);
    }

    private static void testClassArrayClassName() {
        System.out.println(String.class.getCanonicalName());
        System.out.println(String[].class.getCanonicalName());
        System.out.println(int[].class.getCanonicalName());
        System.out.println(int.class);
        System.out.println(Integer.class);
    }

    private static void testConcurrent() throws InterruptedException {

        final Queue<Integer> integers = new LinkedList<>();
        final Collection<Integer> synchronizedCollection = Collections.synchronizedCollection(integers);
        for (int i = 0; i < 100; i++)
            integers.add(i);

        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                System.out.println("adding");
                synchronizedCollection.add(100);
                System.out.println(Thread.currentThread().isDaemon());
                Thread.currentThread().setDaemon(true);
                //System.out.println("Removing "+integers.poll());
            }
        };
        final Thread thread = new Thread(runnable);
        Executors.newScheduledThreadPool(1).schedule(runnable, 100, TimeUnit.MILLISECONDS);
        synchronized (synchronizedCollection) {
            for (Integer integer : synchronizedCollection) {
                System.out.println(integer);
                Thread.sleep(10);
            }
        }
    }

    private static void testInstanceOf() {
        String a = null;
        if (a instanceof String) {
            System.out.println("null variable is instance");
        }
        if (null instanceof String) {
            System.out.println("null literal is instance");
        }
    }


    private static void testGsonMyJson() {
        final String serializedNull = new GsonBuilder().create().toJson(null);
        System.out.println("Null is serialized to:\"" + serializedNull + "\"");


        final ClassA deserializedFromNullString = new GsonBuilder().create().fromJson("null",
                ClassA.class);
        System.out.println("\"null\" is deserialized to:" + deserializedFromNullString);

        String nullSerialization = null;
        final ClassA deserializedFromNullObject = new GsonBuilder().create().fromJson(nullSerialization,
                ClassA.class);
        System.out.println("null object is deserialized to:" + deserializedFromNullObject);


        final ClassA deserializedFromEmptyString = new GsonBuilder().create().fromJson("",
                ClassA.class);
        System.out.println("\"\" is deserialized to:" + deserializedFromEmptyString);

        final ClassA deserializedFromEmptyJson = new GsonBuilder().create().fromJson("{}",
                ClassA.class);
        System.out.println("\"{}\" is deserialized to:" + deserializedFromEmptyJson);

        final String randomString = "asdjhfgasf";
        final ClassA deserializedFromRandomString = new GsonBuilder().create().fromJson(randomString,
                ClassA.class);
        System.out.println("\"" + randomString + "\" is deserialized to:" + deserializedFromRandomString);
    }

    private static void testLinkify() {
        Patterns.linkify(
                "my name is domain.to and I am http://www.google.com/path/hun?ty=98 created by kunj@domain.to with web addr www.domain.to done resolving to 127.0.8.5.");
    }

    private static void testProgramExit() {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println("async");
            }
        });
        System.out.println("byeee");
    }

    static String getFileExtensionFromName(String fileName) {
        String extension;
        final int startOfExtension = fileName.lastIndexOf('.');
        if (startOfExtension == -1 || startOfExtension == fileName.length() - 1) {
            extension = "";
        } else {
            extension = fileName.substring(startOfExtension + 1);
        }
        return extension;
    }

    static String getTest(String url) {
        if (!Strings.isNullOrEmpty(url)) {
            String filename = url;

            // if the filename contains special characters, we don't
            // consider it valid for our matching purposes:
            if (!filename.isEmpty() &&
                    Pattern.matches("[a-zA-Z_0-9\\.\\-\\(\\)\\%]+", filename)) {
                int dotPos = filename.lastIndexOf('.');
                if (0 <= dotPos) {
                    return filename.substring(dotPos + 1);
                }
            }
        }
        return "ssds";
    }

    private static void crashOnMainThread() {
        final Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler
                = Thread.getDefaultUncaughtExceptionHandler();
        System.out.println("Default exception handler of main:" + defaultUncaughtExceptionHandler);
        final Thread.UncaughtExceptionHandler uncaughtExceptionHandlerForMain
                = Thread.currentThread().getUncaughtExceptionHandler();
        System.out.println("Default exception handler of main:" + uncaughtExceptionHandlerForMain);
        System.out.println("Main thread group:" + Thread.currentThread().getThreadGroup());
        crashOnNewThread(uncaughtExceptionHandlerForMain);
        ExecutorShutdownTester.pauseCurrentThread(500);
        System.out.println("killing main");
        throw new RuntimeException("kill main");
    }

    private static void checkAfterExecuteInPeriodicFutureTask() {
        final int[] count = {0};
        ScheduledThreadPoolExecutor _backgroundPool = new ScheduledThreadPoolExecutor(
                3, new ThreadFactory() {
            private int count = 0;

            @Override
            public Thread newThread(Runnable runnable) {
                count++;
                return new Thread(runnable, "bgpool" + '-' + count);
            }
        }) {
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                System.out.println("before execute. thread:" + t + " runnable:" + r);
                super.beforeExecute(t, r);
            }

            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                System.out.println("inside after execute. count:" + count[0] + " runnable:" + r + " throwable:" + t);
                System.out.println("Getting result");
                if (t == null && r instanceof Future<?> && ((Future<?>) r).isDone()) {
                    final Future<?> future = (Future<?>) r;
                    System.out.println("Future isDone:" + future.isDone() + ", isCancelled:" + future.isCancelled());
                    try {
                        final Object result = future.get();
                    } catch (CancellationException ce) {
                        System.out.println("Ignoring exception in executor:" + ce);
                    } catch (ExecutionException ee) {
                        System.out.println("Received execution exception:" + ee);
                        t = ee.getCause();
                    } catch (InterruptedException ie) {
                        System.out.println("Ignoring exception in executor:" + ie);
                    } catch (Exception e) {
                        System.out.println("Received exception:" + e);
                        t = e;
                    } catch (Error er) {
                        System.out.println("Received error:" + er);
                        t = er;
                    } catch (Throwable th) {
                        System.out.println("Receivd throwable:" + th);
                        t = th;
                    }

                }
                System.out.println("Got result. throwable:" + t);
            }

            @Override
            public void execute(Runnable command) {
                System.out.println("Asking to execute. runnable:" + command);
                super.execute(command);
            }
        };

        scheduledFuture = _backgroundPool.scheduleAtFixedRate(getCommand(count), 5, 5, TimeUnit.SECONDS);
    }

    private static void crashOnNewThread(Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler) {
        final Thread infiniteThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        System.out.println("still running");
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        infiniteThread.start();
        ExecutorShutdownTester.pauseCurrentThread(1000);


        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("thread started");
                throw new RuntimeException("crashing thread");
            }
        });
        System.out.println("starting check for exception in new thread.");
        thread.setUncaughtExceptionHandler(defaultUncaughtExceptionHandler);
        final ThreadGroup threadGroup = thread.getThreadGroup();
        final ThreadGroup parent = threadGroup.getParent();
        System.out.println("Crashing thread: group - " + threadGroup + " parent - " + parent);
        thread.start();

    }

    private static Runnable getCommand(final int[] count) {
        return new Runnable() {
            @Override
            public void run() {
                count[0]++;
                if (count[0] == 3) {
                    throw new RuntimeException("exception as count is 2.");
                }
                System.out.println("run completed");
            }
        };
    }

    private static void throwExceptionInExecutor() {
        final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>()) {
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                System.out.println("received exception " + t);
            }
        };
        executor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("running runnable");
                throw new RuntimeException("runnable phansa");
            }
        });
        final Future<Object> submit1 = executor.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                System.out.println("running callable1");
                throw new RuntimeException("callable runtime phansa");
            }
        });
        final Future<Object> submit2 = executor.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                System.out.println("running callable2");
                throw new Exception("callable checked phansa");
            }
        });
        final SettableFuture<Object> future = SettableFuture.create();
        future.addListener(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException("future listener phata");
            }
        }, executor);
        Futures.addCallback(future, new FutureCallback<Object>() {
            @Override
            public void onSuccess(Object result) {
                throw new RuntimeException("future success phata");
            }

            @Override
            public void onFailure(Throwable t) {
            }
        }, executor);
        future.addListener(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException("future listener phata");
            }
        }, executor);

        final SettableFuture<Object> future1 = SettableFuture.create();
        Futures.addCallback(future1, new FutureCallback<Object>() {
            @Override
            public void onSuccess(Object result) {

            }

            @Override
            public void onFailure(Throwable t) {
                throw new RuntimeException("Future exception phata");
            }
        }, executor);

    }

    private static void printStackTrace() {
        final Integer _lock = 1;

        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("executor");
                try {
                    synchronized (_lock) {
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("executor done.");
                executorService.shutdown();
            }
        });

        synchronized (_lock) {
/*
            System.out.println("\n\n Thread.currentThread().dumpstack");
            Thread.currentThread().dumpStack();
            System.out.println("\n\n Thread.dumpstack");
            Thread.dumpStack();
            System.out.println("\n\n throwable.printStackTrace()");
            new Throwable().printStackTrace();
*/
            System.out.println("\n\n Thread.getAllStackTraces()");
            final Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
            for (Thread thread : allStackTraces.keySet()) {
                final StackTraceElement[] stackTraceElements = allStackTraces.get(thread);
                System.out.println(
                        "\n\n" + thread.toString() + "state:" + thread.getState() + " tid:" + thread.getId());
                for (StackTraceElement stackTraceElement : stackTraceElements) {
                    System.out.println(stackTraceElement.toString());
                }
            }
/*
            System.out.println("\n\nThreadInfo");
            final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
            final ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(threadMXBean.isObjectMonitorUsageSupported(), threadMXBean.isSynchronizerUsageSupported());
            for (ThreadInfo threadInfo : threadInfos) {
                System.out.println("\n"+threadInfo);
            }
*/
        }


    }

    private static void testtreeSetSpeed() {
        ArrayList<Integer> arr = new ArrayList<Integer>(7000);
        Random r = new Random();

        HashSet hashSet = new HashSet(2000);
        TreeSet treeSet = new TreeSet();
        LinkedHashSet linkedSet = new LinkedHashSet(2000);

        // start time
        long startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            int x = i;
            hashSet.add(new Integer(x));
        }
        // end time
        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        startTime = System.nanoTime();
        arr.addAll(hashSet);
        endTime = System.nanoTime();
        System.out.println("HashSet: " + duration + " , " + (endTime - startTime));

        // start time
        startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            int x = i;
            treeSet.add(new Integer(x));
        }
        // end time
        endTime = System.nanoTime();
        duration = endTime - startTime;
        startTime = System.nanoTime();
        arr.addAll(treeSet);
        endTime = System.nanoTime();
        System.out.println("TreeSet: " + duration + " , " + (endTime - startTime));

        // start time
        startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            int x = i;
            linkedSet.add(new Integer(x));
        }
        // end time
        endTime = System.nanoTime();
        duration = endTime - startTime;
        startTime = System.nanoTime();
        arr.addAll(linkedSet);
        endTime = System.nanoTime();
        System.out.println("LhshSet: " + duration + " , " + (endTime - startTime));

    }

    private static void testMultipleIteratorsOnArrayList() {
        String[] array = new String[]{"one", "two", "three", "four", "five"};
        final List<String> strings = Arrays.asList(array);
        final Iterator<String> iterator = strings.iterator();
        while (iterator.hasNext()) {
            final String next = iterator.next();
            if (next.equals("three")) {
                final Iterator<String> iterator1 = strings.iterator();
                while (iterator1.hasNext()) {
                    System.out.println(iterator1.next());
                }
                break;
            } else {
                System.out.println(next);
            }
        }

        final Iterator<String> iterator2 = strings.listIterator();
        while (iterator2.hasNext()) {
            final String next = iterator2.next();
            if (next.equals("three")) {
                final Iterator<String> iterator1 = strings.listIterator();
                while (iterator1.hasNext()) {
                    System.out.println(iterator1.next());
                }
                break;
            } else {
                System.out.println(next);
            }
        }


    }

    private static void testRemovalWhileIterating() {
        String[] array = new String[]{"one", "two", "three", "four"};
        final List<String> strings = new ArrayList<String>(Arrays.asList(array));
        final Iterator<String> iterator = strings.iterator();
        while (iterator.hasNext()) {
            final String s = iterator.next();
            if (s.equals("two")) {
                iterator.remove();
            }
        }
        System.out.println("Post removal list is:" + strings);
    }

    private static void testInstanceOfOnNull() {
        String boo = null;
        try {
            if (boo instanceof String) {
                System.out.println("instanceof does not cause exception even when LHS is null");
            } else {
                System.out.println("instanceof does not cause exception even when LHS is null");
            }
        } catch (Exception e) {
            System.out.println("instanceof does not work when LHS is null");
        }
    }

    private static void testTypeCastOverriddenMethodInvocation() {
        final TestChild testChild = new TestChild(10);
        final TestBase testBase = (TestBase) testChild;
        testBase.myMethod();
    }

    private static void testSimpleDateFormat() {
        testTimeString(new Date(2014, 7, 14, 12, 11, 13));
        testTimeString(new Date(2014, 7, 14, 13, 11, 13));
        testTimeString(new Date(2014, 7, 14, 0, 11, 13));
        testTimeString(new Date(2014, 7, 14, 1, 11, 13));
    }

    private static void testTimeString(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm aa");
        System.out.println("Date is:" + date.toString());
        System.out.println("Formatted date is:" + simpleDateFormat.format(date));
    }

    private static void testFutureCancellation() {
        final SettableFuture<Void> future = SettableFuture.create();
        Futures.addCallback(future, new FutureCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                System.out.println("Received result");
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("Received throwable " + t);
                someoneElseShouldRegisterOnItNow(future);
            }

        });

        Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    System.out.println("completed");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                future.set(null);
            }
        });
        future.cancel(true);
        try {
            Thread.sleep(10000);
            someoneElseShouldRegisterOnItNow(future);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void someoneElseShouldRegisterOnItNow(SettableFuture<Void> future) {
        Futures.addCallback(future, new FutureCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                System.out.println("internal one recevied result");
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("Internal one received throwable " + t);
            }
        });
    }

    private static void testSoutOfNullAndNonNullObject() {
        Object obj = new Object() {
            @Override
            public String toString() {
                return "yo baby";
            }
        };
        Object obj1 = null;
        System.out.println("Null ka sout:" + obj1);
        System.out.println("Non-null ka sout:" + obj);

    }

    private static void testVariableLengthArgs(String... args) {
        System.out.println("Args are:" + args.toString());
        for (String arg : args) {
            System.out.println(String.format("arg:", arg));
        }
    }

    private static void testSplitWithLength() {
        String s1 = "7676776";
        final String pattern = " ";
        final int len = 2;
        printSpilt(s1, pattern, len);

        s1 = "7676 776";
        printSpilt(s1, pattern, len);

        s1 = " 7676776";
        printSpilt(s1, pattern, len);

        s1 = "7676776 ";
        printSpilt(s1, pattern, len);

        s1 = "  7676776";
        printSpilt(s1, pattern, len);

        s1 = "7676776  ";
        printSpilt(s1, pattern, len);

        s1 = "7 6 76776";
        printSpilt(s1, pattern, len);
    }

    private static void printSpilt(String s1, String pattern, int len) {
        //s1=s1.trim();
        System.out.println("String:" + s1 + ", pattern:" + pattern + ", len:" + len);
        String[] split = s1.split(pattern, len);
        for (int i = 0; i < split.length; i++) {
            System.out.println(i + ")" + split[i]);
        }
        System.out.println("------------------------------");
    }

    private static void testSplit() {
        String s1 = "7676776";
        printSpilt(s1);

        s1 = "7676 776";
        printSpilt(s1);

        s1 = " 7676776";
        printSpilt(s1);

        s1 = "7676776 ";
        printSpilt(s1);

        s1 = "7 6 76776";
        printSpilt(s1);
    }

    private static void printSpilt(String s1) {
        s1 = s1.trim();
        String[] split = s1.split("\\s+");
        System.out.println(split.length);
        System.out.println(s1);
        for (String s : split) {
            System.out.println(s + " of len:" + s.length());
        }
        System.out.println("------------------------------");
    }

    private static class testSwitchCase {
        MessageBox _box;

        private testSwitchCase() {
            this._box = MessageBox.ALL;
        }

        public void runTest() {
            switch (_box) {
                case ALL:
                    changeBox();
                    break;
                case INBOX:
                    changeBox();
                    break;
                case SENT:
                    changeBox();
                    break;
            }
        }

        private void changeBox() {
            System.out.println("_box was" + _box.name());
            switch (_box) {
                case ALL:
                    _box = MessageBox.INBOX;
                    break;
                case INBOX:
                    _box = MessageBox.SENT;
                    break;
                case SENT:
                    _box = MessageBox.ALL;
                    break;
            }
            System.out.println("_box is" + _box.name());
        }
    }

    public static enum MessageBox {
        ALL, INBOX, SENT, DRAFT, OUTBOX, FAILED, QUEUED, UNKNOWN;

        public static MessageBox getMessageBox(int type) {
            switch (type) {
                case 0:
                    return ALL;
                case 1:
                    return INBOX;
                case 2:
                    return SENT;
                case 3:
                    return DRAFT;
                case 4:
                    return OUTBOX;
                case 5:
                    return FAILED;
                case 6:
                    return QUEUED;
                default:
                    return UNKNOWN;
            }
        }

        @Override
        public String toString() {
            return "changed";
        }
    }

    private static void testExceptionBySumit() {
        Set<String> set = getSet("[]");
        System.out.println("Returned set of size:" + set.size());
        for (String s : set) {
            System.out.println(s);
        }
        set.add("dummy");
        set.add("mummy");
        String s1 = putSet("", set);
        System.out.println("After putting:" + s1);
        set = getSet(s1);
        System.out.println("Returned set of size:" + set.size());
        for (String s : set) {
            System.out.println(s);
        }
    }

    public static Set<String> getSet(String key) {
        String json = key;
        if (json == null) {
            return new HashSet<String>();
        }
        Gson gson = new Gson();
        TypeToken<Set<String>> typeToken = new TypeToken<Set<String>>() {
        };
        return gson.fromJson(json, typeToken.getType());
    }

    public static String putSet(String key, Set<String> s) {
        Gson gson = new Gson();
        TypeToken<Set<String>> token = new TypeToken<Set<String>>() {
        };
        String json = gson.toJson(s, token.getType());
        return json;
    }
}
