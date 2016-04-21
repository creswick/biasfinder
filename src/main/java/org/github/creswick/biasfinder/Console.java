package org.github.creswick.biasfinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class Console {

    private final PrintStream mOut;
    private final BufferedReader mIn;

    public Console() {
        mOut = System.out;
        mIn = new BufferedReader(new InputStreamReader(System.in));
    }

    public void printf(final String string, final Object... args) {
        mOut.printf(string, args);
    }

    public void println(final Object str) {
        if (str instanceof String) {
            mOut.println((String)str);
        } else {
            mOut.println(str);
        }
    }

    public void print(final String str) {
        mOut.print(str);
    }

    public String readLine(final String string) throws IOException {
        mOut.print(string);
        return mIn.readLine();
    }

}
