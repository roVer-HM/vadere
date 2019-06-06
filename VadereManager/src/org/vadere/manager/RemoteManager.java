package org.vadere.manager;

import org.vadere.manager.commandHandler.StateAccessHandler;
import org.vadere.simulator.control.RemoteManagerListener;
import org.vadere.simulator.entrypoints.ScenarioFactory;
import org.vadere.simulator.projects.RunnableFinishedListener;
import org.vadere.simulator.projects.Scenario;
import org.vadere.simulator.projects.ScenarioRun;
import org.vadere.util.logging.Logger;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

public class RemoteManager implements RemoteManagerListener, RunnableFinishedListener, Runnable {


	private static Logger logger = Logger.getLogger(RemoteManager.class);

	private ScenarioRun currentSimulationRun;
	private Thread currentSimulationThread;
	private final Object waitForLoopEnd;
	private ReentrantLock lock;


	public RemoteManager() {
		waitForLoopEnd = new Object();
		lock = new ReentrantLock();
	}

	public void loadScenario(String scenarioString) {
		Scenario scenario = null;
		try {
			scenario = ScenarioFactory.createScenarioWithScenarioJson(scenarioString);
		} catch (IOException e) {
			throw new TraCIException("Cannot create Scenario from given file.");
		}
		currentSimulationRun = new ScenarioRun(scenario, this, true);
		currentSimulationRun.addRemoteManagerListener(this);
	}


	synchronized public void accessState(StateAccessHandler stateAccessHandler){
		try {

			if (!currentSimulationRun.isWaitForSimCommand()) {
				synchronized (waitForLoopEnd){
					waitForLoopEnd.wait();
				}
			}
			lock.lock();
			stateAccessHandler.execute(currentSimulationRun.getSimulationState());

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new TraCIException("Interrupted while accessing simulation state");
		} finally {
			lock.unlock();
		}
	}

	synchronized public void nextStep(double simTime){
		try {
			lock.lock();
			currentSimulationRun.nextSimCommand(simTime);

		} finally {
			lock.unlock();
		}
	}

	@Override
	public void simulationStepFinishedListener() {
		synchronized (waitForLoopEnd){
			waitForLoopEnd.notify();
		}
	}

	@Override
	public void finished(Runnable runnable) {
		logger.infof("Simulation finished.");
	}

	@Override
	public void run() {
		startSimulation();
	}

	private void startSimulation(){
		if (currentSimulationRun == null)
			throw new IllegalStateException("ScenarioRun object must not be null");

		currentSimulationThread = new Thread(currentSimulationRun);
		currentSimulationThread.setUncaughtExceptionHandler((t, ex) -> {
			currentSimulationRun.simulationFailed(ex);
		});

		logger.infof("Start Scenario %s with remote control...", currentSimulationRun.getScenario().getName());
		currentSimulationThread.start();
	}
}