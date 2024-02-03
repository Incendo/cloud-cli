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
package org.incendo.cloud.cli;

import java.util.Objects;
import java.util.function.Consumer;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.internal.JansiLoader;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.internal.CommandRegistrationHandler;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * {@link CommandManager} implementation for creating simple CLI applications.
 */
@API(status = API.Status.STABLE)
public class CLICommandManager extends CommandManager<CLISender> {

    /**
     * Creates a new command manager and configures it using the given {@code managerConsumer}, then executes the command
     * specified by the given {@code args}.
     *
     * <p>This will execute {@link CLICommandManager#run(String[])}, which means that the method will block until the command
     * has been executed. If an error is thrown, the application exits with code {@code 1}.</p>
     *
     * @param args            user-supplied args
     * @param managerConsumer consumer that allows access to the command manager before the command is executed
     */
    public static void bootstrap(
            final @NonNull String @NonNull[] args,
            final @NonNull Consumer<@NonNull CLICommandManager> managerConsumer
    ) {
        final CLICommandManager cliCommandManager = new CLICommandManager(ExecutionCoordinator.simpleCoordinator());
        managerConsumer.accept(cliCommandManager);
        cliCommandManager.run(args);
    }

    private PermissionFunction permissionFunction = PermissionFunction.alwaysTrue();

    /**
     * Creates a new command manager instance.
     *
     * @param executionCoordinator execution coordinator instance. When choosing the appropriate coordinator for your
     *                             project, be sure to consider any limitations noted by the platform documentation.
     */
    public CLICommandManager(@NonNull final ExecutionCoordinator<CLISender> executionCoordinator) {
        super(executionCoordinator, CommandRegistrationHandler.nullCommandRegistrationHandler());

        System.setProperty("jansi.graceful", "true"); // If Jansi fails to load, then we just don't use colors.
        JansiLoader.initialize();

        this.registerDefaultExceptionHandlers(
                triplet -> {
                    final String message = triplet.first().formatCaption(triplet.second(), triplet.third());
                    System.out.println(ansi().eraseLine().fg(Ansi.Color.RED).a(message).reset());
                },
                pair -> {
                    System.err.println(ansi().eraseLine().fg(Ansi.Color.RED).a(pair.first()));
                    pair.second().printStackTrace();
                    System.err.print(ansi().reset());
                }
        );
    }

    @Override
    public final boolean hasPermission(final @NonNull CLISender sender, final @NonNull String permission) {
        return this.permissionFunction.hasPermission(permission);
    }

    /**
     * Sets the permission function.
     *
     * <p>The permission function will get invoked when {@link #hasPermission(CLISender, String)} is invoked.</p>
     *
     * @param permissionFunction new permission function
     */
    public final void permissionFunction(final @NonNull PermissionFunction permissionFunction) {
        this.permissionFunction = Objects.requireNonNull(permissionFunction);
    }

    /**
     * Executes the command in the given {@code args} and blocks until the response has been received. If an error is thrown,
     * the application exists with code {@code 1}.
     *
     * @param args user-supplied args
     */
    public void run(final @NonNull String@NonNull[] args) {
        Objects.requireNonNull(args, "args");
        try {
            this.commandExecutor().executeCommand(CLISender.sender(), String.join(" ", args)).join();
        } catch (final Exception e) {
            System.exit(1);
        }
    }
}
