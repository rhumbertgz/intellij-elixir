package org.elixir_lang.mix.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.filters.TextConsoleBuilderImpl;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import org.elixir_lang.console.ElixirConsoleUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static org.elixir_lang.mix.runner.MixRunningStateUtil.runMix;

/**
 * Created by zyuyou on 15/7/8.
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/rebar/runner/RebarRunningState.java
 */
public class MixRunningState extends CommandLineState {
  protected final MixRunConfigurationBase myConfiguration;

  public MixRunningState(@NotNull ExecutionEnvironment environment, MixRunConfigurationBase configuration) {
    super(environment);
    myConfiguration = configuration;
  }

  @NotNull
  @Override
  public ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
    TextConsoleBuilder consoleBuilder = new TextConsoleBuilderImpl(myConfiguration.getProject()) {
      @Override
      public ConsoleView getConsole() {
        ConsoleView consoleView = super.getConsole();
        ElixirConsoleUtil.attachFilters(myConfiguration.getProject(), consoleView);
        return consoleView;
      }
    };
    setConsoleBuilder(consoleBuilder);
    return super.execute(executor, runner);
  }

  @NotNull
  @Override
  protected ProcessHandler startProcess() throws ExecutionException {
    GeneralCommandLine commandLine = MixRunningStateUtil.commandLine(
      myConfiguration, setupElixirParams(), myConfiguration.getMixArgs()
    );
    return runMix(myConfiguration.getProject(), commandLine);
  }

  @NotNull
  public ConsoleView createConsoleView(Executor executor) {
    TextConsoleBuilder consoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(myConfiguration.getProject());
    return consoleBuilder.getConsole();
  }

  @NotNull
  public List<String> setupElixirParams() throws ExecutionException {
    return new ArrayList<>();
  }
}
