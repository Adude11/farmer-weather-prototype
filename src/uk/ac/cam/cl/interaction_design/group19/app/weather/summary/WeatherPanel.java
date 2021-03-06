package uk.ac.cam.cl.interaction_design.group19.app.weather.summary;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.stream.Stream;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import uk.ac.cam.cl.interaction_design.group19.app.MainWindow;
import uk.ac.cam.cl.interaction_design.group19.app.util.Updatable;

/**
 * Base class for panels in weather view
 * <p>
 * <p>
 * Makes sure the space is divided correctly
 * between buttons and other information
 * <p>
 * Also includes utility methods for subclasses
 */
public abstract class WeatherPanel extends JPanel implements Updatable {
    private static final float  DEFAULT_FONT_SIZE = 18;
    private static final double MAIN_PANEL_RATIO  = 0.85;
    private static final int    REFERENCE_HEIGHT  = 100;
    
    
    /**
     * Main panel       - 0.85 of vertical space
     * Buttons panel    - 0.15 of vertical space
     */
    public WeatherPanel() {
        super(new BorderLayout());
        this.setBackground(MainWindow.BACKGROUND_COLOR);
    }
    
    protected static JPanel createPanel(JComponent... components) {
        var outer = new JPanel(new GridBagLayout());
        var inner = new JPanel(new GridLayout(1, components.length));
        Stream.of(components).forEachOrdered(inner::add);
        outer.add(inner);
        return outer;
    }
    
    protected static JLabel createLabel(float fontSize) {
        return createLabel("", fontSize);
    }
    
    protected static JLabel createLabel(String text, float fontSize) {
        var label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(fontSize));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }
    
    protected static JLabel createLabel() {
        return createLabel("");
    }
    
    protected static JLabel createLabel(String text) {
        return createLabel(text, DEFAULT_FONT_SIZE);
    }
    
    protected static void addOnClick(JButton btn, Runnable btnAction) {
        btn.addActionListener(e -> {
            if (e.getActionCommand().equals(btn.getText())) {
                btnAction.run();
            }
        });
    }
    
    /**
     * Create both main panel and button panel
     * using functions implemented by subclasses
     */
    protected void populate() {
        JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        sp.setResizeWeight(MAIN_PANEL_RATIO);
        sp.setEnabled(false);
        sp.setDividerSize(0);
        
        var height = this.getHeight() != 0 ? this.getHeight() : REFERENCE_HEIGHT;
        
        var mainPanel = createMainPanel();
        mainPanel.setPreferredSize(new Dimension(
                mainPanel.getPreferredSize().width,
                (int) (height * MAIN_PANEL_RATIO)));
        sp.add(mainPanel);
        
        var buttonPanel = createButtonsPanel();
        buttonPanel.setPreferredSize(new Dimension(
                buttonPanel.getPreferredSize().width,
                height - (int) (height * MAIN_PANEL_RATIO)));
        sp.add(buttonPanel);
        
        this.add(sp, BorderLayout.CENTER);
        
        update();
    }
    
    protected abstract JPanel createMainPanel();
    
    protected abstract JPanel createButtonsPanel();
}
