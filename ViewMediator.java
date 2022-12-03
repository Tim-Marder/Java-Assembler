package projectview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import project.DivideByZeroException;
import project.IllegalInstructionException;
import project.MachineModel;
import project.Memory;
import project.ParityCheckException;

public class ViewMediator {

	MachineModel model;
	JFrame frame;
	StepControl stepControl;
	private CodeViewPanel codeViewPanel;
	private MemoryViewPanel memoryViewPanel1;
	private MemoryViewPanel memoryViewPanel2;
	private MemoryViewPanel memoryViewPanel3;
	private ControlPanel controlPanel;
	private ProcessorViewPanel processorPanel;
	private States currentState = States.NOTHING_LOADED;
	private FilesMgr filesMgr;
	private MenuBarBuilder menuBuilder;
	
	public MachineModel getModel() {
		return model;
	}

	public void setModel(MachineModel model) {
		this.model = model;
	}

	public JFrame getFrame() {
		return frame;
	}
	
	public States getCurrentState() {
		return currentState;
	}
	
	public void setCurrentState(States s) {
		if(s == States.PROGRAM_HALTED) stepControl.setAutoStepOn(false);
		currentState = s;
		s.enter();
		notify("");
	}
	
	private void createAndShowGUI() {
		stepControl = new StepControl(this);
		filesMgr = new FilesMgr(this);
		filesMgr.initialize();
		codeViewPanel = new CodeViewPanel(model);
		memoryViewPanel1 = new MemoryViewPanel(model, 0, 160);
		memoryViewPanel2 = new MemoryViewPanel(model, 160, Memory.DATA_SIZE/2);
		memoryViewPanel3 = new MemoryViewPanel(model, Memory.DATA_SIZE/2, Memory.DATA_SIZE);
		controlPanel = new ControlPanel(this);
		processorPanel = new ProcessorViewPanel(model);
		menuBuilder = new MenuBarBuilder(this);
		frame = new JFrame("Simulator");
		JMenuBar bar = new JMenuBar();
		frame.setJMenuBar(bar);
		bar.add(menuBuilder.createFileMenu());
		bar.add(menuBuilder.createExecuteMenu());
		Container content = frame.getContentPane();
		content.setLayout(new BorderLayout(1,1));
		content.setBackground(Color.BLACK);
		frame.setSize(1200,600);
		frame.add(codeViewPanel.createCodeDisplay(), BorderLayout.LINE_START);
		frame.add(processorPanel.createProcessorDisplay(),BorderLayout.PAGE_START);
		JPanel center = new JPanel();
		center.setLayout(new GridLayout(1,3));
		center.add(memoryViewPanel1.createMemoryDisplay());
		center.add(memoryViewPanel2.createMemoryDisplay());
		center.add(memoryViewPanel3.createMemoryDisplay());
		frame.add(center, BorderLayout.CENTER);
		frame.add(controlPanel.createControlDisplay(), BorderLayout.PAGE_END);
		// the next line will be commented or deleted later
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(WindowListenerFactory.windowClosingFactory(e -> exit()));
		frame.setLocationRelativeTo(null);//centers the frame on the screen
		stepControl.start();
		currentState().enter();
		notify("");
		frame.setVisible(true);
	}
	
	private void notify(String str) {
		codeViewPanel.update(str);
		memoryViewPanel1.update(str);
		memoryViewPanel2.update(str);
		memoryViewPanel3.update(str);
		controlPanel.update();
		processorPanel.update();
	}

	
	public void clear() {
		model.clear();
		setCurrentState(States.NOTHING_LOADED);
		currentState.enter();
		notify("Clear");
		model.setProgramSize(0);
	}
	
	public void toggleAutoStep() {
		stepControl.toggleAutoStep();
		if (stepControl.isAutoStepOn()) setCurrentState(States.AUTO_STEPPING);
		else setCurrentState(States.PROGRAM_LOADED_NOT_AUTOSTEPPING);
	}
	
	public void reload() {
		stepControl.setAutoStepOn(false);
		clear();
		filesMgr.finalLoad_ReloadStep();
	}
	
	public void setPeriod(int value) {
		stepControl.setPeriod(value);
	}
	
	public void makeReady(String s) {
		stepControl.setAutoStepOn(false);
		setCurrentState(States.PROGRAM_LOADED_NOT_AUTOSTEPPING);
		currentState.enter();
		notify(s);

	}
	
	public void execute() {
		while (currentState != States.PROGRAM_HALTED &&
				currentState != States.NOTHING_LOADED) {
				try {
					model.step();
				} catch (CodeAccessException e) {
					JOptionPane.showMessageDialog(frame,
							"Illegal access to code from line " + model.getPC() + "\n"
									+ "Exception message: " + e.getMessage(),
									"Run time error",
									JOptionPane.OK_OPTION);
					System.out.println("Illegal access to code from line " +
									model.getPC()); // just for debugging
					System.out.println("Exception message: " + e.getMessage());
				} catch(ArrayIndexOutOfBoundsException e) {
					JOptionPane.showMessageDialog(frame,
							"ArrayIndexOutOfBounds on line " + model.getPC() + "\n"
									+ "Exception message: " + e.getMessage(),
									"Array Index Out Of Bounds",
									JOptionPane.OK_OPTION);
					System.out.println("ArrayIndexOutOfBounds on line " +
									model.getPC()); // just for debugging
					System.out.println("Exception message: " + e.getMessage());
				} catch(NullPointerException e) {
					JOptionPane.showMessageDialog(frame,
							"NullPointerException on line " + model.getPC() + "\n"
									+ "Exception message: " + e.getMessage(),
									"Null Pointer Exception",
									JOptionPane.OK_OPTION);
					System.out.println("NullPointerException on line " +
									model.getPC()); // just for debugging
					System.out.println("Exception message: " + e.getMessage());
				} catch(ParityCheckException e) {
					JOptionPane.showMessageDialog(frame,
							"ParityCheckException on line " + model.getPC() + "\n"
									+ "Exception message: " + e.getMessage(),
									"Parity Check Exception",
									JOptionPane.OK_OPTION);
					System.out.println("ParityCheckException on line " +
									model.getPC()); // just for debugging
					System.out.println("Exception message: " + e.getMessage());
				} catch(IllegalInstructionException e) {
					JOptionPane.showMessageDialog(frame,
							"IllegalInstructionException on line " + model.getPC() + "\n"
									+ "Exception message: " + e.getMessage(),
									"Illegal Instructions",
									JOptionPane.OK_OPTION);
					System.out.println("IllegalInstructionException on line " +
									model.getPC()); // just for debugging
					System.out.println("Exception message: " + e.getMessage());
				} catch(IllegalArgumentException e) {
					JOptionPane.showMessageDialog(frame,
							"IllegalArgumentException on line " + model.getPC() + "\n"
									+ "Exception message: " + e.getMessage(),
									"Illegal Arguments",
									JOptionPane.OK_OPTION);
					System.out.println("IllegalArgumentException on line " +
									model.getPC()); // just for debugging
					System.out.println("Exception message: " + e.getMessage());
				} catch(DivideByZeroException e) {
					JOptionPane.showMessageDialog(frame,
							"DivideByZeroException on line " + model.getPC() + "\n"
									+ "Exception message: " + e.getMessage(),
									"Can't Divide by Zero",
									JOptionPane.OK_OPTION);
					System.out.println("DivideByZeroException on line " +
									model.getPC()); // just for debugging
					System.out.println("Exception message: " + e.getMessage());
				}
		}
		notify("");
	}
	
	public void assembleFile() {
		filesMgr.assembleFile();
	}
	
	public void loadFile() {
		filesMgr.loadFile();
	}
	
	public void exit() {
		int decision = JOptionPane.showConfirmDialog(
			frame, "Do you really wish to exit?",
			"Confirmation", JOptionPane.YES_NO_OPTION);
		if (decision == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}
	
	public States currentState() {
		return currentState;
	}

	public void step() {
		if (currentState != States.PROGRAM_HALTED &&
			currentState != States.NOTHING_LOADED) {
			try {
				model.step();
			} catch (CodeAccessException e) {
				JOptionPane.showMessageDialog(frame,
						"Illegal access to code from line " + model.getPC() + "\n"
								+ "Exception message: " + e.getMessage(),
								"Run time error",
								JOptionPane.OK_OPTION);
				System.out.println("Illegal access to code from line " +
								model.getPC()); // just for debugging
				System.out.println("Exception message: " + e.getMessage());
			} catch(ArrayIndexOutOfBoundsException e) {
				JOptionPane.showMessageDialog(frame,
						"ArrayIndexOutOfBounds on line " + model.getPC() + "\n"
								+ "Exception message: " + e.getMessage(),
								"Array Index Out Of Bounds",
								JOptionPane.OK_OPTION);
				System.out.println("ArrayIndexOutOfBounds on line " +
								model.getPC()); // just for debugging
				System.out.println("Exception message: " + e.getMessage());
			} catch(NullPointerException e) {
				JOptionPane.showMessageDialog(frame,
						"NullPointerException on line " + model.getPC() + "\n"
								+ "Exception message: " + e.getMessage(),
								"Null Pointer Exception",
								JOptionPane.OK_OPTION);
				System.out.println("NullPointerException on line " +
								model.getPC()); // just for debugging
				System.out.println("Exception message: " + e.getMessage());
			} catch(ParityCheckException e) {
				JOptionPane.showMessageDialog(frame,
						"ParityCheckException on line " + model.getPC() + "\n"
								+ "Exception message: " + e.getMessage(),
								"Parity Check Exception",
								JOptionPane.OK_OPTION);
				System.out.println("ParityCheckException on line " +
								model.getPC()); // just for debugging
				System.out.println("Exception message: " + e.getMessage());
			} catch(IllegalInstructionException e) {
				JOptionPane.showMessageDialog(frame,
						"IllegalInstructionException on line " + model.getPC() + "\n"
								+ "Exception message: " + e.getMessage(),
								"Illegal Instructions",
								JOptionPane.OK_OPTION);
				System.out.println("IllegalInstructionException on line " +
								model.getPC()); // just for debugging
				System.out.println("Exception message: " + e.getMessage());
			} catch(IllegalArgumentException e) {
				JOptionPane.showMessageDialog(frame,
						"IllegalArgumentException on line " + model.getPC() + "\n"
								+ "Exception message: " + e.getMessage(),
								"Illegal Arguments",
								JOptionPane.OK_OPTION);
				System.out.println("IllegalArgumentException on line " +
								model.getPC()); // just for debugging
				System.out.println("Exception message: " + e.getMessage());
			} catch(DivideByZeroException e) {
				JOptionPane.showMessageDialog(frame,
						"DivideByZeroException on line " + model.getPC() + "\n"
								+ "Exception message: " + e.getMessage(),
								"Can't Divide by Zero",
								JOptionPane.OK_OPTION);
				System.out.println("DivideByZeroException on line " +
								model.getPC()); // just for debugging
				System.out.println("Exception message: " + e.getMessage());
			}
			notify("");
		}

	}
	
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ViewMediator mediator = new ViewMediator();
				MachineModel model = new MachineModel(() ->
				mediator.setCurrentState(States.PROGRAM_HALTED));
				mediator.setModel(model);
				mediator.createAndShowGUI();
			}
		});
	}
	
}
