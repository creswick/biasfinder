package org.github.creswick.biasfinder;

import java.io.Console;
import java.io.File;
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
    private static Logger log = LoggerFactory.getLogger(App.class);
    private final WordVectors vec;
    private final Console console;

    public App(final WordVectors vec) {
        this.vec = vec;
        final Console c = System.console();
        if (c == null) {
            System.err.println("No console.");
            System.exit(1);
        }
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

    private void startShell() {
        Command cmd = Command.NullOp;
        while (cmd != Command.Exit) {
            final String input = console.readLine("> ");
            int space_idx = input.indexOf(" ");
            if ( space_idx == -1) {
                space_idx = input.length();
            }
            final Splitter split = Splitter.on(CharMatcher.WHITESPACE)
                                           .trimResults()
                                           .omitEmptyStrings();
            final List<String> commandLine = Lists.newArrayList(split.split(input));

            final String commandStr = commandLine.get(0);

            final List<String> cmdArgs;
            if (commandLine.size() >= 2) {
                cmdArgs = commandLine.subList(1, commandLine.size());
            } else {
                cmdArgs = Lists.newArrayList();
            }

            try {
                cmd = Command.valueOf(commandStr);
                runCommand(cmd, cmdArgs);
            } catch (final IllegalArgumentException iae) {
                cmd = Command.NullOp;
            }
        }
    }

    private void runCommand(final Command cmd, final List<String> cmdArgs) {
        switch(cmd) {
        case Nearest:
            printNearest(cmdArgs);
            break;
        case Exit:
            console.printf("Exiting.\n");
            break;
        case NullOp:
            break;
        default:
            console.printf("Unknown command: "+cmd+"\n");
            break;
        }
    }

    private void printNearest(final List<String> cmdArgs) {
        final String term = cmdArgs.get(0);
        final int count = 10;
        console.printf("The "+ count + " words closest to '"+term+"':\n");
        final Collection<String> lst = vec.wordsNearest(term, count);
        console.printf("   "+Joiner.on(", ").join(lst)+"\n");
    }

}
