package org.observis.medreminder.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ButtonBase;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import org.observis.medreminder.shared.FieldVerifier;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MedReminder implements EntryPoint {


	private final LoginServiceAsync loginService = GWT
			.create(LoginService.class);
	private boolean loggedIn = false;

	PatientHolder patientHolder;
	PackageHolder packageHolder;
	
	DialogBox popupPanel = new DialogBox();
	MenuBar bar = new MenuBar();
	private HandlerRegistration closeDialogHandlerReg;


	
	private void setMainStyles(){
		RootPanel.get().setStyleName("rootPanel");
		/*patientsPanel.setStyleName("patientsPanel");
		individualPanel.setStyleName("individualPanel");
		deliveriesPanel.setStyleName("deliveriesPanel");
		packagePanel.setStyleName("packagePanel");
		//patientsPanel.addStyleName("patientsPanel");*/
	}


	private void showDialog(String textToShow) {
		// addPatientBox.setText("Add a patient.");
		// addPatientBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		// final Button addClick = new Button("Add");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		// phoneBox.setText("+358");
		VerticalPanel pan = new VerticalPanel();

		pan.addStyleName("dialogVPanel");
		// dialogVPanel.add(new HTML("<b>*Patient Phone number:</b>"));
		// dialogVPanel.add(phoneBox);
		// dialogVPanel.add(new HTML("Patient name(optional):"));
		// dialogVPanel.add(patientNameBox);
		popupPanel.setText("Info");
		pan.add(new HTML("<b>" + textToShow + "</b"));
		pan.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		pan.add(closeButton);
		popupPanel.setWidget(pan);
		popupPanel.center();
		// Add a handler to close the DialogBox
		closeDialogHandlerReg  = closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				popupPanel.hide();
				popupPanel.clear();
				closeDialogHandlerReg.removeHandler();
			}
		});
	}


	public void onModuleLoad() {
		patientHolder = new PatientHolder();
		packageHolder = new PackageHolder();
		
		
		//initPackageHolder();
		setMainStyles();



		Command issueLogout = new Command() {

			@Override
			public void execute() {
				// loginService.logout();
			}

		};
		Command issuePatientsCommand = new Command() {
			@Override
			public void execute() {
				RootPanel.get("mainPanel").remove(packageHolder);
				RootPanel.get("mainPanel").add(patientHolder);
			}

		};
		Command issueTemplatesCommand = new Command() {
			@Override
			public void execute() {
				RootPanel.get("mainPanel").remove(patientHolder);
				RootPanel.get("mainPanel").add(packageHolder);				
			}

		};
		
		bar.addItem("Patients",issuePatientsCommand);
		bar.addItem("Packages",issueTemplatesCommand);
		bar.addItem("Logout", issueLogout);

		// main screen
		final Button sendButton = new Button("Send");
		final Button loginButton = new Button("Login");
		final TextBox nameField = new TextBox();
		nameField.setText("Username");
		final PasswordTextBox passField = new PasswordTextBox();

		final Label errorLabel = new Label();

		// We can add style names to widgets
		sendButton.addStyleName("sendButton");
		loginButton.addStyleName("loginButton");
		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		RootPanel.get("nameFieldContainer").add(nameField);
		RootPanel.get("passFieldContainer").add(passField);
		// RootPanel.get("sendButtonContainer").add(sendButton);
		RootPanel.get("loginButtonContainer").add(loginButton);
		RootPanel.get("errorLabelContainer").add(errorLabel);

		// Focus the cursor on the name field when the app loads
		nameField.setFocus(true);
		nameField.selectAll();

		// Create the popup dialog box
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		final Label textToServerLabel = new Label();
		final HTML serverResponseLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
		dialogVPanel.add(textToServerLabel);
		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);

		
		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				sendButton.setEnabled(true);
				sendButton.setFocus(true);
			}
		});

		// Create the popup dialog box
		final DialogBox loginBox = new DialogBox();
		loginBox.setText("RPC Login");
		loginBox.setAnimationEnabled(true);
		final Button closeLoginBox = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeLoginBox.getElement().setId("closeLoginBox");
		// final Label loginDetails = new Label();
		final HTML loginResponse = new HTML();
		VerticalPanel dialogLoginPanel = new VerticalPanel();
		dialogLoginPanel.addStyleName("dialogVPanel");
		dialogLoginPanel.add(new HTML("<b>Sending name to the server:</b>"));
		dialogLoginPanel.add(textToServerLabel);
		dialogLoginPanel.add(new HTML("<br><b>Server replies:</b>"));
		dialogLoginPanel.add(loginResponse);
		dialogLoginPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogLoginPanel.add(closeLoginBox);
		loginBox.setWidget(dialogLoginPanel);

		// Add a handler to close the DialogBox
		closeLoginBox.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (loggedIn) {
					RootPanel.get("nameFieldContainer").clear();
					RootPanel.get("passFieldContainer").clear();
					// RootPanel.get("sendButtonContainer").clear();
					RootPanel.get("loginButtonContainer").clear();
					RootPanel.get("errorLabelContainer").clear();
					RootPanel.get().clear();
					loginBox.clear();
					loginBox.hide();
					patientHolder.initUI();
				} else {
					loginBox.hide();
					sendButton.setEnabled(true);
					sendButton.setFocus(true);
				}
			}
		});

		class LoginHandler implements ClickHandler, KeyUpHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				sendLoginToServer();
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					sendLoginToServer();
				}
			}

			/**
			 * Send the name from the nameField to the server and wait for a
			 * response.
			 */
			private void sendLoginToServer() {
				// First, we validate the input.
				errorLabel.setText("");
				String user = nameField.getText();
				String pass = passField.getText();
				if (!FieldVerifier.isValidName(user)
						&& !FieldVerifier.isValidPass(pass)) {
					errorLabel.setText("Please enter at least four characters");
					return;
				}

				// Then, we send the input to the server.
				sendButton.setEnabled(false);
				serverResponseLabel.setText("");
				loginService.logIn(user, pass, new AsyncCallback<Boolean>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						dialogBox.setText("Remote Procedure Call - Failure");
						serverResponseLabel
								.addStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML("Critical error has occured.");
						dialogBox.center();
						closeButton.setFocus(true);
					}

					public void onSuccess(Boolean authenticated) {
						if (authenticated) {
							loginResponse.setText("Login successful.");
							loginBox.center();
							RootPanel.get("nameFieldContainer").clear();
							RootPanel.get("passFieldContainer").clear();
							RootPanel.get("loginButtonContainer").clear();
							RootPanel.get().add(bar);
							patientHolder.loadPatients();
							loggedIn = true;
						} else {
							loginResponse.setText("Login failed.");
							loginBox.center();
						}
						closeLoginBox.setFocus(true);
					}
				});
			}
		}
		// Add a handler to send the name to the server
		LoginHandler loginHandler = new LoginHandler();
		nameField.addKeyUpHandler(loginHandler);
		passField.addKeyUpHandler(loginHandler);
		loginButton.addClickHandler(loginHandler);
	}
}
