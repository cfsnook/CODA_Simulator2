/*******************************************************************************
 * (c) Crown owned copyright 2015 (UK Ministry of Defence)
 * This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0
 * International License
 *
 * This is to identify the UK Ministry of Defence as owners along with the license rights provided. The
 * URL of the CC BY NC SA 4.0 International License is 
 * http://creativecommons.org/licenses/by-nc-sa/4.0/legalcode (Accessed 02-NOV-15).
 *  
 * Contributors:
 *   University of Southampton - Initial API and implementation
 *******************************************************************************/

package ac.soton.coda.internal.simulator2.views;

import java.io.IOException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.part.WorkbenchPart;

import ac.soton.coda.internal.simulator2.ISimulation;
import ac.soton.coda.internal.simulator2.ISimulation.MODE;
import ac.soton.coda.internal.simulator2.ISimulation.SIM_RESULT;
import ac.soton.coda.internal.simulator2.ISimulationChangeListener;
import ac.soton.coda.internal.simulator2.ISimulationManager;
import ac.soton.coda.internal.simulator2.SimulationManager;
import ac.soton.coda.internal.simulator2.utils.Utils;
import de.prob.exception.ProBError;
import de.prob.scripting.ModelTranslationError;

/**
 * <p>
 * A implementation for a view to control the CODA simulation.
 * </P>
 * 
 * @author htson
 * @version 0.1
 * @see ISimulationChangeListener
 * @see ISimulation
 * @since 0.1
 */
public class ControlView extends ViewPart implements ISimulationChangeListener {

	// The View ID.
	public static final String VIEW_ID = "ac.soton.coda.simulation2.controlView";

	// The mode label.
	private Label modeLabel;

	// The combo for choosing the simulation MODE.
	private Combo modeCombo;

	// The load button.
	private Button loadButton;

	// The save button.
	private Button saveButton;

	// The time label.
	private Label timeLabel;

	// The current time.
	private Label currentTime;

	// The tick button.
	private Button tickButton;

	// The spinner for choosing the tick duration.
	private Spinner tickSpinner;
	private static int TICK_STEP_MINIMUM = 1;
	private static int TICK_STEP_MAXIMUM = 10;
	private static int TICK_STEP_DEFAULT = 5;

	// The restart button.
	private Button restartButton;

	// The step button.
	private Button stepButton;

	// The stop button.
	private Button stopButton;

	// The continue button.
	private Button continueButton;
	private static int CONTINUE_STEP = 20;

	/**
	 * An implementation for to create the part control for the view. The
	 * widgets for controlling the CODA simulations are created here.
	 * 
	 * @see WorkbenchPart#createPartControl(Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(4, true));

		// Mode label
		modeLabel = new Label(parent, SWT.RIGHT | SWT.BORDER);
		modeLabel.setText("Mode");
		modeLabel
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Mode combo
		modeCombo = new Combo(parent, SWT.READ_ONLY);
		modeCombo.setItems(new String[] {//
				MODE.RECORDING.toString(), MODE.PLAYBACK.toString() //
				});
		modeCombo
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		// When the combo is selected, change the simulation mode.
		modeCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				MODE mode = getMode();
				if (Utils.DEBUG)
					Utils.debug("Change simulation mode to " + mode);

				ISimulationManager simMan = SimulationManager.getDefault();
				try {
					simMan.setMode(mode);
				} catch (ProBError e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ModelTranslationError e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		// Load button
		loadButton = new Button(parent, SWT.PUSH);
		loadButton.setText("Load");
		loadButton
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		// When load button is select, open a dialog for choosing the file to
		// load
		loadButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell shell = loadButton.getShell();
				FileDialog dialog = new FileDialog(shell, SWT.OPEN);
				dialog.setFilterExtensions(new String[] { "*.gold.oracle" });
				ISimulationManager simManager = SimulationManager.getDefault();
				ISimulation sim = simManager.getCurrentSimulation();
				IFolder oracleFolder;
				try {
					oracleFolder = sim.getOracleFolder();
				} catch (CoreException e1) {
					Utils.handleException(e1, "Cannot get the oracle folder.");
					return;
				}
				dialog.setFilterPath(oracleFolder.getRawLocation().toString());
				dialog.setText("Select Gold Oracle File to Playback");
				String rawLocation = dialog.open();

				if (Utils.DEBUG)
					Utils.debug("Load run from " + rawLocation);

				try {
					sim.loadRun(rawLocation);
				} catch (ProBError e1) {
					Utils.handleException(e1,
							"ProB was not able to load the model.");
					return;
				} catch (ModelTranslationError e1) {
					Utils.handleException(e1,
							"Was not able to translate the model.");
					return;
				} catch (IOException e1) {
					Utils.handleException(
							e1,
							"Loading of the model failed."
									+ " Please check to make sure that the Rodin static checker has "
									+ "produced a valid static checked file (.bcc or .bcm)."
									+ " If not, try cleaning the project.");
					return;
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		// Save button
		saveButton = new Button(parent, SWT.PUSH);
		saveButton.setText("Save");
		saveButton
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		// When the save button is selected, create a progress monitor dialog
		// and save the current simulation run.
		saveButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ISimulation sim = SimulationManager.getDefault()
						.getCurrentSimulation();
				ProgressMonitorDialog dialog = new ProgressMonitorDialog(
						saveButton.getShell());
				try {
					if (Utils.DEBUG)
						Utils.debug("Save current run");

					sim.save(dialog);
				} catch (CoreException e1) {
					Utils.handleException(e1, "Error saving the current run");
					e1.printStackTrace();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		// Current time
		timeLabel = new Label(parent, SWT.RIGHT | SWT.BORDER);
		timeLabel.setText("Current time: ");
		timeLabel
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		currentTime = new Label(parent, SWT.LEFT | SWT.BORDER);
		currentTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));

		// Tick buttons and spinner.
		tickButton = new Button(parent, SWT.PUSH);
		tickButton.setText("Tick");
		tickButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true,
				false));
		tickButton
				.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		tickButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ISimulationManager simMan = SimulationManager.getDefault();
				ISimulation sim = simMan.getCurrentSimulation();
				int ticks = tickSpinner.getSelection();
				if (Utils.DEBUG)
					Utils.debug("Progress by " + ticks + " ticks");
				SIM_RESULT result = sim.tick(ticks);
				if (result != SIM_RESULT.OK)
					Utils.handleResult(result);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		tickSpinner = new Spinner(parent, SWT.DEFAULT);
		tickSpinner.setMinimum(TICK_STEP_MINIMUM);
		tickSpinner.setMaximum(TICK_STEP_MAXIMUM);
		tickSpinner.setSelection(TICK_STEP_DEFAULT);

		// Restart button
		restartButton = new Button(parent, SWT.PUSH);
		restartButton.setText("Restart");
		restartButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		// When the restart button is selected, restart the current simulation.
		restartButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ISimulationManager simMan = SimulationManager.getDefault();
				ISimulation sim = simMan.getCurrentSimulation();
				if (Utils.DEBUG)
					Utils.debug("Restart simulation");
				SIM_RESULT result;
				try {
					result = sim.restart();
					if (result != SIM_RESULT.OK)
						Utils.handleResult(result);
				} catch (ProBError e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ModelTranslationError e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		// Step button
		stepButton = new Button(parent, SWT.PUSH);
		stepButton.setText("Step");
		stepButton
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		// When the step button is selected, make a step in the simulation.
		stepButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ISimulationManager simMan = SimulationManager.getDefault();
				ISimulation sim = simMan.getCurrentSimulation();
				if (Utils.DEBUG)
					Utils.debug("Executes a step");
				
				SIM_RESULT result = sim.step();
				if (result != SIM_RESULT.OK) {
					Utils.handleResult(result);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		// Stop button
		stopButton = new Button(parent, SWT.PUSH);
		stopButton.setText("Stop");
		stopButton
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		stopButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ISimulationManager simMan = SimulationManager.getDefault();
				if (Utils.DEBUG)
					Utils.debug("Stop the current simulation");
				try {
					simMan.setMode(MODE.RECORDING);
				} catch (ProBError e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ModelTranslationError e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		// Continue button
		continueButton = new Button(parent, SWT.PUSH);
		continueButton.setText("Continue");
		continueButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		continueButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				ISimulationManager simMan = SimulationManager.getDefault();
				ISimulation sim = simMan.getCurrentSimulation();
				SIM_RESULT result = sim.toContinue(CONTINUE_STEP);
				if (result != SIM_RESULT.OK)
					Utils.handleResult(result);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		// Layout the components.
		parent.pack(true);

		// Listen to the simulation changes from the simulation manager.
		ISimulationManager simManager = SimulationManager.getDefault();
		simManager.addSimulationChangeListener(this);

		// Set the input to the view.
		setInput();
	}

	/**
	 * @return
	 */
	protected MODE getMode() {
		String text = modeCombo.getText();
		if (text.equals(MODE.RECORDING.toString())) {
			return MODE.RECORDING;
		} else {
			// PLAYBACK
			return MODE.PLAYBACK;
		}
	}

	/**
	 * When the part is focused, pass the focus to the step button.
	 * 
	 * @see WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		stepButton.setFocus();
	}

	/**
	 * An implementation of a call-back when the CODA simulations change. Set
	 * the input again will force the view to refresh.
	 * 
	 * @see ISimulationChangeListener#simulationChange()
	 */
	@Override
	public void simulationChange() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				setInput();
			}
		});
	}

	/**
	 * Utility method to set the input and refresh the view.
	 */
	private void setInput() {
		ISimulation simulation = SimulationManager.getDefault()
				.getCurrentSimulation();

		refresh(simulation);
	}

	/**
	 * Utility method for refresh the view with the given simulation.
	 * 
	 * @param simulation
	 *            the input simulation
	 */
	private void refresh(ISimulation simulation) {
		if (simulation == null) {
			modeLabel.setEnabled(false);
			modeCombo.setEnabled(false);
			loadButton.setEnabled(false);
			saveButton.setEnabled(false);
			timeLabel.setEnabled(false);
			currentTime.setEnabled(false);
			tickSpinner.setEnabled(false);
			tickButton.setEnabled(false);
			restartButton.setEnabled(false);
			stepButton.setEnabled(false);
			stopButton.setEnabled(false);
			continueButton.setEnabled(false);
			return;
		} else {
			MODE mode = simulation.getMode();
			modeLabel.setEnabled(true);
			modeCombo.setEnabled(true);
			modeCombo.setText(mode.toString());
			loadButton.setEnabled(true);
			saveButton.setEnabled(true);
			timeLabel.setEnabled(true);
			currentTime.setEnabled(true);
			currentTime.setText("" + simulation.getCurrentTime());
			currentTime.pack(true);
			tickSpinner.setEnabled(true);
			tickButton.setEnabled(true);
			restartButton.setEnabled(true);
			stepButton.setEnabled(true);
			if (mode == MODE.PLAYBACK)
				stopButton.setEnabled(true);
			else 
				stopButton.setEnabled(false);
			continueButton.setEnabled(true);
		}
	}

}
