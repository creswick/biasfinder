package org.github.creswick.biasfinder;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

/**
 * Hello world!
 *
 */
public class App {
    private static final Joiner joiner = Joiner.on(", ");
    private static Logger log = LoggerFactory.getLogger(App.class);
    private final WordVectors vec;
    private final Console console;
    private boolean mDebug;

    public App(final WordVectors vec) {
        this.vec = vec;
        final Console c = new Console();
        this.console = c;
    }

    public static void main(final String[] args) throws Exception {
        log.info("Loading model....");
        final File gModel = new File("GoogleNews-vectors-negative300.bin.gz");
        final WordVectors vec = WordVectorSerializer.loadGoogleModel(gModel, true);
        log.info("Loaded model.");

        final App a = new App(vec);
        a.startShell();
    }

    private void startShell() throws IOException {
        Command cmd = Command.NullOp;
        while (cmd != Command.Exit) {
            try {
                final String input = console.readLine("> ");
                int space_idx = input.indexOf(" ");
                if (space_idx == -1) {
                    space_idx = input.length();
                }
                final Splitter split = Splitter.on(CharMatcher.WHITESPACE).trimResults().omitEmptyStrings();
                final List<String> commandLine = Lists.newArrayList(split.split(input));

                final String commandStr = commandLine.get(0);

                final List<String> cmdArgs;
                if (commandLine.size() >= 2) {
                    cmdArgs = commandLine.subList(1, commandLine.size());
                } else {
                    cmdArgs = Lists.newArrayList();
                }

                cmd = Command.valueOf(commandStr);
                runCommand(cmd, cmdArgs);
            } catch (final Exception e) {
                console.println("An exception occurred!" + e);
                cmd = Command.NullOp;
            }
        }
    }

    private void runCommand(final Command cmd, final List<String> cmdArgs) {
        debug("Running command: " + cmd);
        switch (cmd) {
        case Debug:
            mDebug = ! mDebug;
            break;
        case Sim:
            runSim(cmdArgs);
            break;
        case Manipulate:
            runManipulate(cmdArgs);
            break;
        case Nearest:
            runNearest(cmdArgs);
            break;
        case Exit:
            console.printf("Exiting.\n");
            break;
        case NullOp:
            console.printf("Really?\n");
            break;
        default:
            console.printf("Unknown command: " + cmd + "\n");
            break;
        }
    }

    private void debug(final String string) {
        if (mDebug) {
            console.println("[DEBUG]"+ string);
        }
    }

    private void runSim(final List<String> cmdArgs) {
        if (cmdArgs.size() < 2) {
            console.println("Insufficient arguments (at least 2 args needed)");
            return;
        }

        console.println(vec.similarity(cmdArgs.get(0), cmdArgs.get(1)));
    }

    private void runManipulate(final List<String> cmdArgs) {
        if (cmdArgs.size() < 2) {
            console.printf("Insufficient arguments (at least 2 args needed)");
            return;
        }

        final int splitIdx = cmdArgs.indexOf("-");

        final List<String> positive = cmdArgs.subList(0, splitIdx);
        final List<String> negative = cmdArgs.subList(splitIdx, cmdArgs.size());

        final Collection<String> near = vec.wordsNearest(positive, negative, 10);
        console.println(joiner.join(near));
    }

    private void runNearest(final List<String> cmdArgs) {
        final String term = cmdArgs.get(0);
        final int count = 10;
        console.printf("The " + count + " words closest to '" + term + "':\n");
        final Collection<String> lst = vec.wordsNearest(term, count);
        console.printf("   " + joiner.join(lst) + "\n");
    }

}
