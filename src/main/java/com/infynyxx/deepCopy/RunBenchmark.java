package com.infynyxx.deepCopy;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RunBenchmark {
    private static final String TEST = ".*RunBenchmark.*";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(new JsonFactory());

    public static void main( String[] args ) throws IOException {
        Main.main(getArguments(TEST, 10, 5000, 10));
    }

    private static String[] getArguments(String className, int nRuns, int runForMilliseconds, int nThreads) {
        return new String[]{className,
                "-i", "" + nRuns,
                "-r", runForMilliseconds + "ms", // Run time for each iteration. Examples: 100s, 200ms
                "-t", "" + nThreads,
                "-wi", "3", // warmup iterations
                "-v"
        };
    }

    private Map<String, Object> readFile() throws IOException {
        String fileName = "file2.json";
        File file = File.createTempFile(fileName, RunBenchmark.class.getName());
        InputStream instream = RunBenchmark.class.getResourceAsStream("/" + fileName);
        final OutputStream out = new FileOutputStream(file);
        ByteStreams.copy(instream, out);
        String input = Files.toString(file, Charsets.UTF_8);
        return toMap(input);
    }

    @GenerateMicroBenchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime, Mode.SampleTime, Mode.SingleShotTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void runDeepCopyByteArrayOutputStream() throws IOException {
        DeepCopyByteArrayOutputStream.copy(readFile());
    }

    @GenerateMicroBenchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime, Mode.SampleTime, Mode.SingleShotTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void runDeepCopyKryo() throws IOException {
        DeepCopyKryo.copy(readFile());
    }

    private static Map<String, Object> toMap(String json) throws IOException {
        if (json == null) {
            return new HashMap<String, Object>();
        }
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() { };
        return OBJECT_MAPPER.readValue(json, typeRef);
    }
}
