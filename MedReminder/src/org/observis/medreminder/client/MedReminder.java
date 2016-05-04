package org.observis.medreminder.client;

import java.util.Arrays;
import java.util.List;

import org.observis.medreminder.shared.FieldVerifier;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MedReminder implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network " + "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
	private final LoginServiceAsync loginService = GWT.create(LoginService.class);
	private final CommunicationServiceAsync comService = GWT.create(CommunicationService.class);
	private boolean loggedIn = false;
	HorizontalPanel holder = new HorizontalPanel();
	VerticalPanel individualPanel = new VerticalPanel();
	Button addSchedule = new Button("add");
	Button addPatient = new Button("Add patient");
	TextBox phoneBox = new TextBox();
	TextBox patientNameBox = new TextBox();
	VerticalPanel patientsPanel = new VerticalPanel();
	String[] patientString;
	/**
	 * Updating the patient panel
	 * Args Patient name
	 */
	private void addSchedule(){
		individualPanel.remove(individualPanel.getWidgetCount()-1);
		TextBox hour = new TextBox();
		hour.setWidth("15px");
		hour.setText("00");
		hour.setMaxLength(2);
		
		Label deli = new Label();
		deli.setText(":");
		deli.setWidth("8px");
		deli.setAutoHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		TextBox minute = new TextBox();
		minute.setWidth("15px");
		minute.setText("00");
		minute.setMaxLength(2);
		HorizontalPanel timePanel = new HorizontalPanel();
		
		timePanel.add(hour);
		timePanel.add(deli);
		timePanel.add(minute);	
		
		
		individualPanel.add(timePanel);
		individualPanel.add(addSchedule);
		//individualPanel.getWidget(individualPanel.getWidgetCount());
		
	}
	
	private void addPatientPopup(){
		// Create the popup dialog box
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Add a patient.");
		dialogBox.setAnimationEnabled(true);
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
		dialogBox.setWidget(dialogVPanel);
		dialogBox.center();
		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				phoneBox.setText("+358");
				patientNameBox.setText("");
			}
		});
		addClick.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				comService.addPatient(patientNameBox.getText(), phoneBox.getText(), new AsyncCallback<String>(){

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Failure");
					}

					@Override
					public void onSuccess(String result) {
						Window.alert(result);
					
					}
					
				});
				dialogBox.hide();
				phoneBox.setText("+358");
				patientNameBox.setText("");
			}
			
		});
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void loadPatients(){
		TextCell patientsCell = new TextCell();
		CellList<String> cellList = new CellList<String>(patientsCell);
		cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		
		
		comService.getPatients(new AsyncCallback(){

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Unable to fetch patients.");
				
			}

			@Override
			public void onSuccess(Object result) {
				String a = (String) result;
				patientString = a.split(",");
				
			}
			
		});
		
		List<String> patients = Arrays.asList(patientString);
		
		final SingleSelectionModel<String> selectionModel = new SingleSelectionModel<String>();
		    cellList.setSelectionModel(selectionModel);
		    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
		      public void onSelectionChange(SelectionChangeEvent event) {
		        String selected = selectionModel.getSelectedObject();
		        if (selected != null) {
		        	updateMiddlePanel(selected);
		          //Window.alert("You selected: " + selected);
		        }
		      }
		    });
		    //Window.alert("Size:"+patients.size());
		    cellList.setRowCount(patients.size(), true);
		    cellList.setRowData(0,patients);
		    patientsPanel = new VerticalPanel();
		    
		    patientsPanel.add(cellList);
		    patientsPanel.add(addPatient);
		    

			

	}
	
	private void updateMiddlePanel(String patient){		
		individualPanel.clear();
		Label patientName = new Label();
		patientName.setText("Patient name: "+patient);
		Button saveData = new Button("Save");
		DatePicker finalDate = new DatePicker();
		CheckBox[] days = new CheckBox[7];
		for(int i =0;i<7;i++){
			days[i]= new CheckBox();
		}
		days[0].setText("Monday");
		days[1].setText("Tuesday");
		days[2].setText("Thirsday");
		days[3].setText("Wednesday");
		days[4].setText("Friday");
		days[5].setText("Saturday");
		days[6].setText("Sunday");
		HorizontalPanel dayPanel = new HorizontalPanel();
		for(CheckBox box:days){
			dayPanel.add(box);
		}
		
		HorizontalPanel timePanel = new HorizontalPanel();
		
		TextBox hour = new TextBox();
		hour.setWidth("15px");
		hour.setText("00");
		hour.setMaxLength(2);
		
		Label deli = new Label();
		deli.setText(":");
		deli.setWidth("8px");
		deli.setAutoHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		TextBox minute = new TextBox();
		minute.setWidth("15px");
		minute.setText("00");
		minute.setMaxLength(2);
		
		timePanel.add(hour);
		timePanel.add(deli);
		timePanel.add(minute);	
		
		addSchedule.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				addSchedule();
			}
			
		});
		
		individualPanel.add(patientName);		
		individualPanel.add(saveData);
		individualPanel.add(finalDate);
		individualPanel.add(dayPanel);
		individualPanel.add(timePanel);
		individualPanel.add(addSchedule);
		
		holder.insert(individualPanel,1);
		
	}
	private void initUI() {
		Command issueLogout = new Command(){

			@Override
			public void execute() {
			//loginService.logout();
			}
			
		};
		
		MenuBar bar = new MenuBar(true);
		bar.addItem("Logout", issueLogout);
		
		individualPanel = new VerticalPanel();
		
		
		addPatient.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				 addPatientPopup();
			}
			
		});
		
	    
	    loadPatients();
	    
	    holder.insert(patientsPanel, 0);	    
		holder.insert(individualPanel,1);
		
	    RootPanel.get("mainPanel").add(bar);
	    RootPanel.get("mainPanel").add(holder);

	}
	public void onModuleLoad() {
			  
		
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
		//RootPanel.get("sendButtonContainer").add(sendButton);
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
				final Label loginDetails = new Label();
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
						if(loggedIn){
							RootPanel.get("nameFieldContainer").clear();
							RootPanel.get("passFieldContainer").clear();
							//RootPanel.get("sendButtonContainer").clear();
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
					 * Send the name from the nameField to the server and wait for a response.
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
						greetingService.greetServer(textToServer, new AsyncCallback<String>() {
							public void onFailure(Throwable caught) {
								// Show the RPC error message to the user
								dialogBox.setText("Remote Procedure Call - Failure");
								serverResponseLabel.addStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(SERVER_ERROR);
								dialogBox.center();
								closeButton.setFocus(true);
							}

							public void onSuccess(String result) {
								dialogBox.setText("Remote Procedure Call");
								serverResponseLabel.removeStyleName("serverResponseLabelError");
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
					 * Send the name from the nameField to the server and wait for a response.
					 */
					private void sendLoginToServer() {
						// First, we validate the input.
						errorLabel.setText("");
						String user = nameField.getText();
						String pass = passField.getText();
						if (!FieldVerifier.isValidName(user) && !FieldVerifier.isValidPass(pass)) {
							errorLabel.setText("Please enter at least four characters");
							return;
						}

						// Then, we send the input to the server.
						sendButton.setEnabled(false);
						//textToServerLabel.setText(textToServer);
						serverResponseLabel.setText("");
						loginService.logIn(user,pass, new AsyncCallback<Boolean>() {
							public void onFailure(Throwable caught) {
								// Show the RPC error message to the user
								dialogBox.setText("Remote Procedure Call - Failure");
								serverResponseLabel.addStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(SERVER_ERROR);
								dialogBox.center();
								closeButton.setFocus(true);
							}

							public void onSuccess(Boolean authenticated) {
								if(authenticated){
									loginResponse.setText("Login successful.");
									loginBox.center();
									loggedIn = true;
								} else {
									loginResponse.setText("Login failed.");
									loginBox.center();
								}
								//dialogBox.setText("Remote Procedure Call");
								//serverResponseLabel.removeStyleName("serverResponseLabelError");
								//serverResponseLabel.setHTML(result);
								//dialogBox.center();
								closeLoginBox.setFocus(true);
							}
						});
					}
				}
		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
		LoginHandler loginHandler = new LoginHandler();
		sendButton.addClickHandler(handler);
		nameField.addKeyUpHandler(handler);
		loginButton.addClickHandler(loginHandler);
	}
}

