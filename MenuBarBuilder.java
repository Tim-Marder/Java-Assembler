package projectview;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class MenuBarBuilder {
	
	private JMenuItem assemble = new JMenuItem("Assemble Source...");
	private JMenuItem load = new JMenuItem("Load File...");
	private JMenuItem exit = new JMenuItem("Exit");
	private JMenuItem go = new JMenuItem("Go");
	private ViewMediator mediator;
	
	public MenuBarBuilder(ViewMediator mediator) {
		this.mediator = mediator;
	}

	public void update() {
		assemble.setEnabled(mediator.getCurrentState().getAssembleFileActive());
		load.setEnabled(mediator.getCurrentState().getLoadFileActive());
		go.setEnabled(mediator.getCurrentState().getStepActive());
	}
	
	JMenu createFileMenu() { //public?
		JMenu menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		
		assemble.setMnemonic(KeyEvent.VK_F);
		assemble.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_F, KeyEvent.ALT_DOWN_MASK));
		assemble.addActionListener(e -> mediator.assembleFile());
		assemble.setMnemonic(KeyEvent.VK_M);
		assemble.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_M, KeyEvent.ALT_DOWN_MASK));
		assemble.addActionListener(e -> mediator.assembleFile());
		assemble.setMnemonic(KeyEvent.VK_M);
		assemble.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_M, ActionEvent.CTRL_MASK));
		assemble.addActionListener(e -> mediator.assembleFile());
		menu.add(assemble);

		load.setMnemonic(KeyEvent.VK_L);
		load.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_L, KeyEvent.ALT_DOWN_MASK));
		load.addActionListener(e -> mediator.loadFile());
		exit.setMnemonic(KeyEvent.VK_E);
		exit.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_E, KeyEvent.ALT_DOWN_MASK));
		exit.addActionListener(e -> mediator.exit());
		menu.add(load);
		menu.addSeparator();
		menu.add(exit);

		return menu;
	}
	
	JMenu createExecuteMenu() {
		
		JMenu menu = new JMenu("Execute");
		menu.setMnemonic(KeyEvent.VK_X);
		
		go.setMnemonic(KeyEvent.VK_G);
		go.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_G, KeyEvent.ALT_DOWN_MASK));
		go.addActionListener(e -> mediator.execute());
		menu.add(go);
		
		return menu;
		
	}
	
	

}
