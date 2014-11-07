package com.infynyxx.deepCopy;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RunBenchmark {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(new JsonFactory());

    public static void main( String[] args ) throws IOException, RunnerException {
        Options options = new OptionsBuilder()
                .include(RunBenchmark.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5000)
                .forks(1)
                .verbosity(VerboseMode.NORMAL)
                .measurementTime(TimeValue.milliseconds(5000L))
                .build();
        new Runner(options).run();
    }

    private Map<String, Object> readFile() throws IOException {
        String fileName = "file2.json";
        File file = File.createTempFile(fileName, RunBenchmark.class.getName());
        file.deleteOnExit();
        InputStream instream = RunBenchmark.class.getResourceAsStream("/" + fileName);
        final OutputStream out = new FileOutputStream(file);
        ByteStreams.copy(instream, out);
        String input = Files.toString(file, Charsets.UTF_8);
        return toMap(input);
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime, Mode.SampleTime, Mode.SingleShotTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void runDeepCopyByteArrayOutputStream() throws IOException {
        DeepCopyByteArrayOutputStream.copy(readFile());
    }

    @Benchmark
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
