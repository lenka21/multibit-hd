package org.multibit.hd.ui.views.wizards.welcome;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.I18NConfiguration;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.i18n.LanguageKey;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.ComboBoxes;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.BackgroundPanel;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

/**
 * <p>Wizard panel to provide the following to UI:</p>
 * <ul>
 * <li>Welcome users to the application and allow them to select a language</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class WelcomeSelectLanguagePanelView extends AbstractWizardPanelView<WelcomeWizardModel, String> implements ActionListener {

  private static final Logger log = LoggerFactory.getLogger(WelcomeSelectLanguagePanelView.class);

  // Model
  private String localeCode = Languages.currentLocale().getLanguage();

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public WelcomeSelectLanguagePanelView(AbstractWizard<WelcomeWizardModel> wizard, String panelName) {

    super(wizard.getWizardModel(), panelName, MessageKey.WELCOME_TITLE);

    PanelDecorator.addExitCancelNext(this, wizard);

  }

  @Override
  public void newPanelModel() {

    localeCode = Languages.currentLocale().getLanguage();
    setPanelModel(localeCode);

    // Bind it to the wizard model
    getWizardModel().setLocaleCode(localeCode);

  }

  @Override
  public JPanel newWizardViewPanel() {

    BackgroundPanel panel = Panels.newDetailBackgroundPanel(AwesomeIcon.GLOBE);

    panel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[][]", // Column constraints
      "[][]" // Row constraints
    ));

    JComboBox<String> languagesComboBox = ComboBoxes.newLanguagesComboBox(this, Languages.currentLocale());

    panel.add(Labels.newSelectLanguageLabel(),"shrink");
    panel.add(languagesComboBox, "growx,width min:350:,push,wrap");
    panel.add(Labels.newWelcomeNote(), "grow,push,span 2,wrap");

    return panel;
  }

  @Override
  public void fireInitialStateViewEvents() {

    // Enable the "next" button
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, true);

  }

  @Override
  public void afterShow() {

    // Do nothing

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Do nothing - panel model is updated via an action and wizard model is not applicable

  }

  /**
   * <p>Handle the change locale action event</p>
   *
   * @param e The action event
   */
  @Override
  public void actionPerformed(ActionEvent e) {

    JComboBox source = (JComboBox) e.getSource();
    String localeCode = LanguageKey.values()[source.getSelectedIndex()].getKey();

    // Determine the new locale
    Locale newLocale = Languages.newLocaleFromCode(localeCode);

    // Create a new configuration to reset the separators
    Configuration configuration = Configurations.currentConfiguration.deepCopy();
    I18NConfiguration i18NConfiguration = new I18NConfiguration(newLocale);
    configuration.setI18NConfiguration(i18NConfiguration);

    // Update the main configuration
    Configuration newConfiguration = Configurations.currentConfiguration.deepCopy();
    newConfiguration.getI18NConfiguration().setLocale(newLocale);

    // Make the switch immediately
    Configurations.switchConfiguration(newConfiguration);

  }
}
