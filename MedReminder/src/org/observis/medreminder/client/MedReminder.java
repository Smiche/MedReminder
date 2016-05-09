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
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side services.
	 */
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);
	private final LoginServiceAsync loginService = GWT
			.create(LoginService.class);
	private final CommunicationServiceAsync comService = GWT
			.create(CommunicationService.class);
	private boolean loggedIn = false;
	private HorizontalPanel patientHolder = new HorizontalPanel();
	private HorizontalPanel packageHolder = new HorizontalPanel();
	private VerticalPanel individualPanel = new VerticalPanel();	
	
	
	private Button addPatient = new Button("Add");
	private TextBox phoneBox = new TextBox();
	private TextBox patientNameBox = new TextBox();

	private VerticalPanel patientsPanel = new VerticalPanel();
	private ListBox packagesList = new ListBox();
	private DialogBox addPatientBox = new DialogBox();
	private VerticalPanel packagesListPanel = new VerticalPanel();

	private String[] patientString;
	private String[] packagesListString;
	private DialogBox popupPanel = new DialogBox();

	private VerticalPanel packagePanel = new VerticalPanel();
	private ArrayList<VerticalPanel> messagePanel = new ArrayList<VerticalPanel>();
	private VerticalPanel deliveriesPanel = new VerticalPanel();

	private String medValue = "";
	private String selectedPatient = "";
	private String selectedPackage = "";
	
	MenuBar bar = new MenuBar();

	private HandlerRegistration closeDialogHandlerReg;

	private Button addPackage = new Button("Add");

	/**
	 * Submitting task to server
	 */

	private void submitTask() {
		ArrayList<Message> data = new ArrayList<Message>();
		Window.alert("submitting data?");
		VerticalPanel cur = new VerticalPanel();
		String txt = "";
		String dayVal = "";
		HorizontalPanel tp = new HorizontalPanel();
		String h = "", m = "";
		String title = "";
		
		
		for (int i = 0; i < messagePanel.size(); i++) {
			cur = messagePanel.get(i);
			if (cur.getWidget(0) instanceof Label) {
				title = ((Label) cur.getWidget(0)).getText();
			}
			if (cur.getWidget(1) instanceof TextBox) {
				txt = ((TextBox) cur.getWidget(1)).getText();
			}
			txt.replaceAll("[value]", medValue);
			if (cur.getWidget(2) instanceof Label) {
				dayVal = ((Label) cur.getWidget(2)).getText();
				dayVal = dayVal.replaceAll("Day: ", "");
			}
			if (cur.getWidget(3) instanceof HorizontalPanel) {
				tp = (HorizontalPanel) cur.getWidget(3);
			}
			if (tp.getWidget(0) instanceof TextBox
					&& tp.getWidget(2) instanceof TextBox) {
				h = ((TextBox) tp.getWidget(0)).getText();
				m = ((TextBox) tp.getWidget(2)).getText();
			}
			data.add(new Message(title, txt, h+":"+m, dayVal));
		}

		comService.scheduleMessages(data, selectedPatient,
				new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(String result) {
						showDialog("Succesfully scheduled: " + result);
					}
				});
	}

	private void addPatientPopup() {
		// Create the popup dialog box
		addPatientBox.setText("Add a patient.");
		addPatientBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		final Button addClick = new Button("Add");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		VerticalPanel dialogVPanel = new VerticalPanel();
		phoneBox.setText("+358");

		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>*Patient Phone number:</b>"));
		dialogVPanel.add(phoneBox);
		dialogVPanel.add(new HTML("Patient name(optional):"));
		dialogVPanel.add(patientNameBox);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(addClick);
		dialogVPanel.add(closeButton);
		addPatientBox.setWidget(dialogVPanel);
		addPatientBox.center();
		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addPatientBox.hide();
				phoneBox.setText("+358");
				patientNameBox.setText("");
			}
		});
		addClick.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				comService.addPatient(patientNameBox.getText(),
						phoneBox.getText(), new AsyncCallback<String>() {

							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Failure");
							}

							@Override
							public void onSuccess(String result) {
								loadPatients();
							}

						});
				addPatientBox.hide();
				phoneBox.setText("+358");
				patientNameBox.setText("");
			}

		});

	}

	private void addPackagePopup(){
		
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void loadPatients() {
		comService.getPatients(new AsyncCallback() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Unable to fetch patients.");

			}

			@Override
			public void onSuccess(Object result) {
				String a = (String) result;
				patientString = a.split(",");
				initUI();
				addPatientBox.hide();
				phoneBox.setText("+358");
				patientNameBox.setText("");

			}
		});

	}

	private void updatePatients() {
		List<String> patients = Arrays.asList(patientString);
		TextCell patientsCell = new TextCell();
		CellList<String> cellList = new CellList<String>(patientsCell);
		// cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		final SingleSelectionModel<String> selectionModel = new SingleSelectionModel<String>();
		cellList.setSelectionModel(selectionModel);
		selectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						String selected = selectionModel.getSelectedObject();
						if (selected != null) {
							// Window.alert("You selected: " + selected);
							selectedPatient = selected;
							updateMiddlePanel();
						}
					}
				});
		// Window.alert("Size:"+patients.size());
		cellList.setRowCount(patients.size(), true);
		cellList.setRowData(0, patients);

		patientsPanel = new VerticalPanel();
		patientsPanel.add(cellList);
		patientsPanel.add(addPatient);

	}

	@SuppressWarnings("unused")
	private void requestPackagesList() {
		comService.getPackagesList(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Unable to fetch templateList.");
			}

			@Override
			public void onSuccess(String result) {
				Integer sel = packagesList.getSelectedIndex();
				packagesList.clear();
				
				String[] arr = result.split(",");
				packagesListString = arr;
				packagesList.addItem("");
				
				for (int i = 0; i < packagesListString.length; i++) {
					packagesList.addItem(packagesListString[i]);
				}
				if(sel!=null)
				packagesList.setSelectedIndex(sel);
			}
		});
	}

	@SuppressWarnings("unused")
	private void loadPackage(String packageName) {

		// interface to get template
		comService.getPackage(packageName,
				new AsyncCallback<ArrayList<Message>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Unable to fetch template by name.");

					}

					@Override
					public void onSuccess(ArrayList<Message> messages) {
						individualPanel.remove(packagePanel);
						packagePanel.clear();
						messagePanel.clear();
						// templateString = result;
						for (Message msg : messages) {
							// replace text logic
							String text = msg.text.replaceAll("[value]", "");
							//
							TextBox box = new TextBox();
							box.setText(text);
							box.setAlignment(TextAlignment.JUSTIFY);
							String[] time = msg.time.split(":");
							
							TextBox hour = new TextBox();
							hour.setWidth("15px");
							hour.setMaxLength(2);
							hour.setText(time[0]);
							
							TextBox minute = new TextBox();
							minute.setWidth("15px");
							minute.setMaxLength(2);
							minute.setText(time[1]);

							Label delimeter = new Label(":");
							delimeter.setWidth("8px");
							delimeter
									.setAutoHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

							HorizontalPanel timePane = new HorizontalPanel();

							timePane.add(hour);
							timePane.add(delimeter);
							timePane.add(minute);
							VerticalPanel vp = new VerticalPanel();
							vp.add(new Label("" + msg.title));
							vp.add(box);
							vp.add(new Label("Day: " + msg.day));
							vp.add(timePane);
							
							messagePanel.add(vp);
							
						}
						for(VerticalPanel p:messagePanel){
							packagePanel.add(p);
						}
						individualPanel.add(packagePanel);
					}

				});
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
		closeDialogHandlerReg = closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				popupPanel.hide();
				popupPanel.clear();
				closeDialogHandlerReg.removeHandler();
			}
		});
	}

	private void updateMiddlePanel() {
		// clear panel
		// new elements
		// finalDayBox = new DatePicker();
		Label patientName = new Label("Patient phone: " + selectedPatient);
		// patientName.setText("Patient phone: " + patient);

		Button submitData = new Button("Submit");

		submitData.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				submitTask();
			}

		});

		requestPackagesList();
		individualPanel.clear();
		individualPanel.add(patientName);
		individualPanel.add(packagesList);
		individualPanel.add(submitData);
		
		patientHolder.insert(individualPanel, 1);

	}

	private void clearUI() {
		patientsPanel.clear();
		individualPanel.clear();
		patientHolder.clear();

	}

	private void initUI() {
		clearUI();

		addPatient.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addPatientPopup();
			}

		});

		addPackage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addPackagePopup();
			}

		});

		
		updatePatients();

		patientHolder.insert(patientsPanel, 0);
		patientHolder.insert(individualPanel, 1);

		RootPanel.get("mainPanel").add(bar);
		
		RootPanel.get("mainPanel").add(patientHolder);

	}
	
	private void initPackageHolder(){
		comService.getPackagesList(new AsyncCallback<String>(){

			@Override
			public void onFailure(Throwable caught) {
				showDialog("Failed to get packages!");				
			}

			@Override
			public void onSuccess(String result) {
				packageHolder.clear();
				packagesListPanel.clear();
				
				String[] packageArr = result.split(",");
				List<String> packages = Arrays.asList(packageArr);
				TextCell packageCell = new TextCell();
				CellList<String> packagesCellList = new CellList<String>(packageCell);
				// cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

				final SingleSelectionModel<String> selectionModel = new SingleSelectionModel<String>();
				packagesCellList.setSelectionModel(selectionModel);
				selectionModel
						.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
							public void onSelectionChange(SelectionChangeEvent event) {
								String selected = selectionModel.getSelectedObject();
								if (selected != null) {
									Window.alert("You selected: " + selected);
									selectedPackage = selected;
									//updateMiddlePanel();
								}
							}
						});
				// Window.alert("Size:"+patients.size());
				
				
				packagesCellList.setRowCount(packages.size(), true);
				packagesCellList.setRowData(0, packages);		
				
				packagesListPanel.add(packagesCellList);
				packagesListPanel.add(addPackage);
				
				packageHolder.add(packagesListPanel);
			}
			
		});
		
	}
	
	public void onModuleLoad() {
		initPackageHolder();
		
		packagesList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				loadPackage(packagesList.getSelectedItemText());
			}
		});

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
					initUI();
				} else {
					loginBox.hide();
					sendButton.setEnabled(true);
					sendButton.setFocus(true);
				}
			}
		});

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler, KeyUpHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				sendNameToServer();
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					sendNameToServer();
				}
			}

			/**
			 * Send the name from the nameField to the server and wait for a
			 * response.
			 */
			private void sendNameToServer() {
				// First, we validate the input.
				errorLabel.setText("");
				String textToServer = nameField.getText();
				if (!FieldVerifier.isValidName(textToServer)) {
					errorLabel.setText("Please enter at least four characters");
					return;
				}

				// Then, we send the input to the server.
				sendButton.setEnabled(false);
				textToServerLabel.setText(textToServer);
				serverResponseLabel.setText("");
				greetingService.greetServer(textToServer,
						new AsyncCallback<String>() {
							public void onFailure(Throwable caught) {
								// Show the RPC error message to the user
								dialogBox
										.setText("Remote Procedure Call - Failure");
								serverResponseLabel
										.addStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(SERVER_ERROR);
								dialogBox.center();
								closeButton.setFocus(true);
							}

							public void onSuccess(String result) {
								dialogBox.setText("Remote Procedure Call");
								serverResponseLabel
										.removeStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(result);
								dialogBox.center();
								closeButton.setFocus(true);
							}
						});
			}
		}
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
				// textToServerLabel.setText(textToServer);
				serverResponseLabel.setText("");
				loginService.logIn(user, pass, new AsyncCallback<Boolean>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						dialogBox.setText("Remote Procedure Call - Failure");
						serverResponseLabel
								.addStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML(SERVER_ERROR);
						dialogBox.center();
						closeButton.setFocus(true);
					}

					public void onSuccess(Boolean authenticated) {
						if (authenticated) {
							loginResponse.setText("Login successful.");
							loginBox.center();
							RootPanel.get("nameFieldContainer").clear();
							RootPanel.get("passFieldContainer").clear();
							// RootPanel.get("sendButtonContainer").add(sendButton);
							RootPanel.get("loginButtonContainer").clear();
							loadPatients();
							loggedIn = true;
						} else {
							loginResponse.setText("Login failed.");
							loginBox.center();
						}
						// dialogBox.setText("Remote Procedure Call");
						// serverResponseLabel.removeStyleName("serverResponseLabelError");
						// serverResponseLabel.setHTML(result);
						// dialogBox.center();
						closeLoginBox.setFocus(true);
					}
				});
			}
		}
		// Add a handler to send the name to the server
		// MyHandler handler = new MyHandler();
		LoginHandler loginHandler = new LoginHandler();
		// sendButton.addClickHandler(handler);
		nameField.addKeyUpHandler(loginHandler);
		passField.addKeyUpHandler(loginHandler);
		loginButton.addClickHandler(loginHandler);
		showDialog("hello");
	}
}
