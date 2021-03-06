package org.multibit.hd.ui.views.wizards.change_password;

import com.google.common.eventbus.Subscribe;
import org.multibit.hd.ui.events.view.VerificationStatusChangedEvent;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Model object to provide the following to "credentials wizard":</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class ChangePasswordWizardModel extends AbstractWizardModel<ChangePasswordState> {

  private static final Logger log = LoggerFactory.getLogger(ChangePasswordWizardModel.class);

  /**
   * The "change credentials" panel model
   */
  private ChangePasswordPanelModel changePasswordPanelModel;

  /**
   * @param state The state object
   */
  public ChangePasswordWizardModel(ChangePasswordState state) {
    super(state);
  }

  @Override
  public String getPanelName() {
    return state.name();
  }

  /**
   * @return The credentials the user entered (must be able to unlock the current wallet)
   */
  public String getEnteredPassword() {
    return changePasswordPanelModel.getEnterPasswordModel().getValue();
  }

  /**
   * @return The confirmed credentials (use this to lock up the current wallet)
   */
  public String getConfirmedPassword() {
    return changePasswordPanelModel.getConfirmPasswordModel().getValue();
  }

  /**
   * <p>Reduced visibility for panel models only</p>
   *
   * @param changePasswordPanelModel The "enter credentials" panel model
   */
  void setChangePasswordPanelModel(ChangePasswordPanelModel changePasswordPanelModel) {
    this.changePasswordPanelModel = changePasswordPanelModel;
  }

  @Subscribe
  public void onVerificationStatusChangedEvent(VerificationStatusChangedEvent event) {

    if (ChangePasswordState.CHANGE_PASSWORD_ENTER_PASSWORD.name().equals(event.getPanelName())) {
      ViewEvents.fireWizardButtonEnabledEvent(event.getPanelName(), WizardButton.NEXT, event.isOK());
    }

  }

  @Override
  public void showNext() {

    switch (state) {
      case CHANGE_PASSWORD_ENTER_PASSWORD:
        state = ChangePasswordState.CHANGE_PASSWORD_REPORT;
        break;
      case CHANGE_PASSWORD_REPORT:
         state = ChangePasswordState.CHANGE_PASSWORD_REPORT;
         break;
       default:
        throw new IllegalStateException("Unknown state: " + state.name());
    }
  }
}
