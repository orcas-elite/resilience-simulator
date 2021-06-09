package de.unistuttgart.sqa.orcas.misim.parsing;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import de.unistuttgart.sqa.orcas.misim.entities.generator.Generator;
import de.unistuttgart.sqa.orcas.misim.events.ChaosMonkeyEvent;
import de.unistuttgart.sqa.orcas.misim.models.MainModel;

/**
 * The ExpModelParser class reads a json file that contains the experiment model.
 */
public class ExpModelParser {
    public static Set<Object> parsedExperimentObjects = Collections.unmodifiableSet(new HashSet<>());


    public static final List<Class<? extends Parser<?>>> parserClasses;

    /*
     * Classes of objects that should be loaded by the {@code ExpModelParser} can be registered here.
     * TODO: this may be automatable via a compile-level injection.
     * TODO: Maybe move to separate class for cleanup
     */
    static {
        List<Class<? extends Parser<?>>> temp = Arrays.asList(
            ChaosMonkeyParser.class,
            SummonerMonkeyParser.class,
            LatencyMonkeyParser.class,
            GeneratorParser.class);
        parserClasses = Collections.unmodifiableList(temp);
    }

    /**
     * Read the given experiment file and parses it into a set of objects. These objects are usually self-scheduling
     * Events like, {@link ChaosMonkeyEvent} or {@link Generator} objects.
     *
     * @return a set of objects that represent the experiment.
     */
    public static Set<Object> parseExperimentData(Path path) {
        try {
            Gson gson = new GsonParser().getGson();
            JsonObject root = gson.fromJson(new JsonReader(new FileReader(path.toFile())), JsonObject.class);

            Set<? extends Parser<?>> parserInstances = parserClasses.stream().map(clazz -> {
                try {
                    return clazz.getDeclaredConstructor().newInstance();
                } catch (InstantiationException
                    | IllegalAccessException
                    | NoSuchMethodException
                    | InvocationTargetException e) {
                    e.printStackTrace();
                    throw new ParsingException(
                        String.format("Parser %s could not be instantiated.", clazz.getTypeName()));
                }
            }).collect(Collectors.toSet());

            Set<Parser<?>> parsedObjects = new HashSet<>();

            for (Parser<?> parserInstance : parserInstances) {
                Class<?> clazz = Array.newInstance(parserInstance.getClass(), 0).getClass();
                Object result = gson.fromJson(root.get(parserInstance.getDescriptionKey()), clazz);

                if (result == null) {
                    continue;
                }

                int count = Array.getLength(result);
                for (int i = 0; i < count; i++) {
                    parsedObjects.add((Parser<?>) Array.get(result, i));
                }
            }

            parsedExperimentObjects =
                parsedObjects.stream().map(o -> o.convertToObject(MainModel.get())).collect(Collectors.toSet());

            return parsedExperimentObjects;


        } catch (FileNotFoundException ex) {
            throw new ParsingException(String.format("Experiment file '%s' not found", path.toUri()));
        }
    }
}

