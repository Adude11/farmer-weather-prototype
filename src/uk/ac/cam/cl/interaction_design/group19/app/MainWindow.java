package uk.ac.cam.cl.interaction_design.group19.app;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import uk.ac.cam.cl.interaction_design.group19.app.GDDs.GDDsView;
import uk.ac.cam.cl.interaction_design.group19.app.GDDs.bizeeAPI;
import uk.ac.cam.cl.interaction_design.group19.app.api.MetOfficeAPI;
import uk.ac.cam.cl.interaction_design.group19.app.map.MapsView;
import uk.ac.cam.cl.interaction_design.group19.app.settings.SettingsView;
import uk.ac.cam.cl.interaction_design.group19.app.util.ExtremeEvent;
import uk.ac.cam.cl.interaction_design.group19.app.util.IconType;
import uk.ac.cam.cl.interaction_design.group19.app.util.Icons;
import uk.ac.cam.cl.interaction_design.group19.app.util.PropertyFactory;
import uk.ac.cam.cl.interaction_design.group19.app.util.Updatable;
import uk.ac.cam.cl.interaction_design.group19.app.util.WeatherType;
import uk.ac.cam.cl.interaction_design.group19.app.weather.WeatherView;
import uk.ac.cam.cl.interaction_design.group19.app.weather.WeeklyPanel;
import uk.ac.cam.cl.interaction_design.group19.app.GDDs.bizeeAPI;

/**
 * Main window of the application.
 * Contains style constants like screen dimension and colors.
 * Enforces division between views and data
 * by passing lambdas with references to model owned by MainWindow to view objects
 */
public class MainWindow extends JFrame implements Updatable {
    public static final int SCREEN_WIDTH  = 320;
    public static final int SCREEN_HEIGHT = 480;
    
    public static final  int   BOTTOM_TAB_WIDTH         = 55;
    public static final  Color BACKGROUND_COLOR         = new Color(229, 235, 255);
    public static final  Color LOW_CONTRAST_TEXT_COLOR  = new Color(51, 82, 122);
    private static final Color HIGH_CONTRAST_TEXT_COLOR = Color.BLACK;
    
    private final Model model;
    
    private final WeatherView  weatherView;
    private final GDDsView     gddsView;
    private final MapsView     mapsView;
    private final SettingsView settingsView;
    
    public MainWindow() throws IOException {
        model = new Model(this::update,
                          () -> UIManager.put("text", HIGH_CONTRAST_TEXT_COLOR),
                          () -> UIManager.put("text", LOW_CONTRAST_TEXT_COLOR));
        
        weatherView = new WeatherView(time -> MetOfficeAPI.getDayData(time, model.getLocationID()),
                                      time -> MetOfficeAPI.fiveDayForecast(model.getLocationID()),
                                      time -> IntStream.range(0, WeeklyPanel.NUM_DAYS_TO_SHOW).mapToObj(
                                              i -> MetOfficeAPI.daySummary(model.getLocationID(), i))
                                                       .collect(Collectors.toList()));
        gddsView = new GDDsView(() -> bizeeAPI.gddForecast(model.getLocationID(), 10));
        mapsView = new MapsView();
        settingsView = createSettingsView();
        
        initWindow();
        addTabs();
        this.setVisible(true);
    }
    
    public void initWindow() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Farmer Weather App");
        this.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        
        this.setResizable(false);
        this.setIconImage(Icons.getIcon(IconType.APP_ICON));
    }
    
    public void addTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(BACKGROUND_COLOR);
        
        
        JLabel weatherLabel = new JLabel("Weather");
        weatherLabel.setPreferredSize(new Dimension(BOTTOM_TAB_WIDTH, weatherLabel.getPreferredSize().height));
        
        JLabel mapsLabel = new JLabel("Maps");
        mapsLabel.setPreferredSize(new Dimension(BOTTOM_TAB_WIDTH, mapsLabel.getPreferredSize().height));
        
        JLabel GDDsLabel = new JLabel("GDDs");
        GDDsLabel.setPreferredSize(new Dimension(BOTTOM_TAB_WIDTH, GDDsLabel.getPreferredSize().height));
        
        JLabel settingsLabel = new JLabel("Settings");
        settingsLabel.setPreferredSize(new Dimension(BOTTOM_TAB_WIDTH, settingsLabel.getPreferredSize().height));
        
        tabs.addTab("Weather", weatherView);
        tabs.addTab("Maps", mapsView);
        tabs.addTab("GDDs", gddsView);
        tabs.addTab("Settings", settingsView);
        tabs.setTabComponentAt(0, weatherLabel);
        tabs.setTabComponentAt(1, mapsLabel);
        tabs.setTabComponentAt(2, GDDsLabel);
        tabs.setTabComponentAt(3, settingsLabel);
        
        tabs.setTabPlacement(JTabbedPane.BOTTOM);
        tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        this.add(tabs, BorderLayout.CENTER);
    }
    
    private SettingsView createSettingsView() {
        var alerts = Arrays.stream(ExtremeEvent.values())
                           .collect(Collectors.toMap(
                                   e -> e,
                                   e -> PropertyFactory.createProperty(
                                           () -> model.getAlert(e),
                                           v -> model.setAlert(e, v))));
        return new SettingsView(
                PropertyFactory.createProperty(model::getPostcode, model::setPostcode),
                PropertyFactory.createProperty(model::getHighContrastMode, model::setHighContrastMode),
                alerts);
    }
    
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
                                   try {
                                       for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                                           if ("Nimbus".equals(info.getName())) {
                                               UIManager.setLookAndFeel(info.getClassName());
                        
                                               break;
                                           }
                                       }
                                       UIManager.getLookAndFeelDefaults().put("Panel.background", BACKGROUND_COLOR);
                                       UIManager.put("text", LOW_CONTRAST_TEXT_COLOR);
//                                       UIManager.put ("JLabel.foreground", Color.white);
                                   } catch (Exception e) {
                                       // Default to Java LookAndFell
                                   }
                                   try {
                                       new MainWindow();
                                   } catch (IOException e) {
                                       e.printStackTrace();
                                   }
                               }
        );
    }
    
    @Override
    public void update() {
        weatherView.update();
        mapsView.update();
        gddsView.update();
        settingsView.update();
    }
}
