//
// MIT License
//
// Copyright (c) 2024 Incendo
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package org.incendo.cloud.cli.example;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.fusesource.jansi.Ansi;
import org.incendo.cloud.cli.CLICommandManager;

import static org.fusesource.jansi.Ansi.ansi;
import static org.incendo.cloud.parser.standard.EnumParser.enumParser;

/**
 * A simple example application.
 */
public final class ExampleApplication {

    /**
     * Runs the example application.
     *
     * @param args command args, will be joined to a space-separated string
     */
    public static void main(final @NonNull String @NonNull[] args) {
        CLICommandManager.bootstrap(args, manager -> {
            manager.command(
                    manager.commandBuilder("test")
                            .flag(manager.flagBuilder("color").withComponent(enumParser(Ansi.Color.class)))
                            .handler(ctx -> System.out.println(ansi()
                                    .fg(ctx.flags().getValue("color", Ansi.Color.MAGENTA))
                                    .render("Hello World!")
                                    .reset()))
            );
        });
    }
}
