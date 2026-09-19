package io.github.rpravin86.mobile.reporting;

import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.PickleStepTestStep;
import io.cucumber.plugin.event.Status;
import io.cucumber.plugin.event.TestCaseFinished;
import io.cucumber.plugin.event.TestCaseStarted;
import io.cucumber.plugin.event.TestRunFinished;
import io.cucumber.plugin.event.TestStepFinished;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ExtentCucumberReporter implements ConcurrentEventListener {

    private final Map<UUID, Instant> scenarioStartTimes = new ConcurrentHashMap<>();

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestCaseStarted.class, this::scenarioStarted);
        publisher.registerHandlerFor(TestStepFinished.class, this::stepFinished);
        publisher.registerHandlerFor(TestCaseFinished.class, this::scenarioFinished);
        publisher.registerHandlerFor(TestRunFinished.class, event -> ExtentReportManager.flush());
    }

    private void scenarioStarted(TestCaseStarted event) {
        scenarioStartTimes.put(event.getTestCase().getId(), event.getInstant());
        String location = event.getTestCase().getUri() + ":" + event.getTestCase().getLocation().getLine();
        ExtentReportManager.startScenario(
                event.getTestCase().getName(),
                location,
                event.getTestCase().getTags()
        );
    }

    private void stepFinished(TestStepFinished event) {
        if (!(event.getTestStep() instanceof PickleStepTestStep pickleStep)) {
            return;
        }

        String step = pickleStep.getStep().getKeyword() + pickleStep.getStep().getText();
        Status status = event.getResult().getStatus();
        if (status == Status.PASSED) {
            ExtentReportManager.passStep(step);
        } else if (status == Status.SKIPPED) {
            ExtentReportManager.skipStep(step);
        } else {
            ExtentReportManager.failStep(step, event.getResult().getError());
        }
    }

    private void scenarioFinished(TestCaseFinished event) {
        Instant startedAt = scenarioStartTimes.remove(event.getTestCase().getId());
        long elapsedMillis = startedAt == null
                ? 0
                : Duration.between(startedAt, event.getInstant()).toMillis();

        Status status = event.getResult().getStatus();
        if (status == Status.PASSED) {
            ExtentReportManager.scenarioPassed(elapsedMillis);
        } else if (status == Status.SKIPPED) {
            ExtentReportManager.scenarioSkipped(elapsedMillis);
        } else {
            ExtentReportManager.scenarioFailed(elapsedMillis, event.getResult().getError());
        }
        ExtentReportManager.finishScenario();
    }
}
