package uk.ac.cam.cl.interaction_design.group19.app.weather.summary;

import java.awt.CardLayout;
import java.util.List;
import java.util.function.Supplier;
import javax.swing.JPanel;
import uk.ac.cam.cl.interaction_design.group19.app.util.WeatherData;
import uk.ac.cam.cl.interaction_design.group19.app.util.Updatable;
import uk.ac.cam.cl.interaction_design.group19.app.weather.HourlyPanel;

/**
 * Panel showing weather for one day, consists of three subpanels,
 * only one of which is shown at a time, these are:
 *   - summary view
 *   - more info view
 *   - hourly view
 */
public class DayPanel extends JPanel implements Updatable {
    public static final String SUMMARY_CARD   = "Summary";
    public static final String FULL_INFO_CARD = "FullInfo";
    public static final String HOURLY_CARD    = "Hourly";
    
    private final SummaryPanel  summaryPanel;
    private final FullInfoPanel fullInfoPanel;
    private final HourlyPanel   hourlyPanel;
    
    public DayPanel(Supplier<WeatherData> dayDataSupplier, Supplier<List<List<WeatherData>>> hourlyDataSupplier) {
        var layout = new CardLayout();
        this.setLayout(layout);
        
        summaryPanel = new SummaryPanel(
                dayDataSupplier,
                () -> layout.show(this, FULL_INFO_CARD),
                () -> layout.show(this, HOURLY_CARD));
        fullInfoPanel = new FullInfoPanel(
                dayDataSupplier,
                () -> layout.show(this, SUMMARY_CARD));
        hourlyPanel = new HourlyPanel(
                hourlyDataSupplier,
                () -> layout.show(this, SUMMARY_CARD));
        
        this.add(summaryPanel, SUMMARY_CARD);
        this.add(fullInfoPanel, FULL_INFO_CARD);
        this.add(hourlyPanel, HOURLY_CARD);
        
        layout.show(this, SUMMARY_CARD);
    }
    
    @Override
    public void update() {
        summaryPanel.update();
        fullInfoPanel.update();
        hourlyPanel.update();
    }
}
