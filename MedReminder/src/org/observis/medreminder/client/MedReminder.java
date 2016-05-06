package org.observis.medreminder.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ButtonBase;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DatePicker;
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
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);
	private final LoginServiceAsync loginService = GWT
			.create(LoginService.class);
	private final CommunicationServiceAsync comService = GWT
			.create(CommunicationService.class);
	private boolean loggedIn = false;
	private HorizontalPanel holder = new HorizontalPanel();
	private VerticalPanel individualPanel = new VerticalPanel();
	private Button addSchedule = new Button("add");
	private Button addPatient = new Button("Add patient");
	private TextBox phoneBox = new TextBox();
	private TextBox patientNameBox = new TextBox();
	private VerticalPanel patientsPanel = new VerticalPanel();
	private DatePicker finalDayBox = new DatePicker();	
	private CheckBox[] days = new CheckBox[7];
	private ListBox templatesList = new ListBox();
	private boolean[] weekArray = new boolean[7];
	private DialogBox addPatientBox = new DialogBox();
	private TextBox messageBox = new TextBox();
	private ArrayList<String> dayTime = new ArrayList<String>();
	private String[] patientString;
	private String[] templateString;
	private String[] templatesListString;
	private HorizontalPanel timePanel = new HorizontalPanel();
	private DialogBox popupPanel;
	private Date selectedDate;
	HandlerRegistration addTimeHandler;


	MenuBar bar = new MenuBar();

	/**
	 * Updating the patient panel Args Patient name
	 */

	private void submitTask() {
		// String text = individualPanel.getWidget(0);
		String patientName = ((Label) individualPanel.getWidget(0)).getText().replaceAll("Patient phone: ","");
		String text = ((TextBox) individualPanel.getWidget(5)).getText();
		
		Date curDate = new Date();
		
		Date finalDate;
		finalDate = selectedDate;
		
		String timesString = "";
		
		int atTimeIndex = 0;
		for(int i=0;i<timePanel.getWidgetCount();i++){
			if(timePanel.getWidget(i) instanceof TextBox){
				if(atTimeIndex == 0){
					timesString+=((TextBox) timePanel.getWidget(i)).getText();
				}
				if(atTimeIndex != 0 && atTimeIndex%2 == 0){
					timesString+=","+((TextBox) timePanel.getWidget(i)).getText();
				}
				if(atTimeIndex%2 == 1){
					timesString+=":"+((TextBox) timePanel.getWidget(i)).getText();
				}
				atTimeIndex++;
			}
		}
		
		String daysCheckedString = "";
		
		for(int i = 0;i<days.length;i++){
			if(i==0){
				if(days[i].getValue()){
				daysCheckedString+="1";	
				} else {
					daysCheckedString+="0";
				}
			} else {
				if(days[i].getValue()){
				daysCheckedString+=",1";	
				} else {
					daysCheckedString+=",0";
				}
			}
		}
		Window.alert(curDate.toString());
		Window.alert(finalDate.toString());
		
		comService.sendTask(text, daysCheckedString, timesString, curDate, finalDate, patientName, new AsyncCallback<String>(){

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				showDialog("Failed to send task.");
			}

			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				showDialog(result);
			}
			
		});
	}

	private void addTimeBox() {
		dayTime.add("00:00");
		updateTimePanel();
	}
	
	private void updateTimePanel(){
		timePanel.clear();
		for (int i = 0; i < dayTime.size(); i++) {
			TextBox hour = new TextBox();
			hour.setWidth("15px");
			hour.setMaxLength(2);

			Label deli = new Label();
			deli.setText(":");
			deli.setWidth("8px");
			deli.setAutoHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

			TextBox minute = new TextBox();
			minute.setWidth("15px");
			minute.setMaxLength(2);
			
			
			String[] hourMinute = dayTime.get(i).split(":");
			hour.setText("" + hourMinute[0]);
			minute.setText("" + hourMinute[1]);
			timePanel.add(hour);
			timePanel.add(deli);
			timePanel.add(minute);
		}
		timePanel.add(addSchedule);
	}

	@SuppressWarnings("unused")
	private void requestTemplateList() {
		comService.getTemplateList(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Unable to fetch templateList.");
			}

			@Override
			public void onSuccess(String result) {
				int sel = templatesList.getSelectedIndex();
				templatesList.clear();
				
				String[] arr = result.split(",");
				templatesListString = arr;
				templatesList.addItem("");
				//templatesList.setSelectedIndex(index);
				for (int i = 0; i < templatesListString.length; i++) {
				//	Window.alert("adding template item:"
					//		+ templatesListString[i]);
					templatesList.addItem(templatesListString[i]);
				}
				templatesList.setSelectedIndex(sel);
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
							updateMiddlePanel(selected);

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
	private void loadTemplate(String templateName) {
		
		// interface to get template
		comService.getTemplate(templateName, new AsyncCallback<String[]>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Unable to fetch template by name.");

			}

			@Override
			public void onSuccess(String[] result) {
				templateString = result;
				
				String[] arr = templateString;
				String text = arr[0];
				String[] weekdays = arr[1].split(",");
				String[] times = arr[2].split(",");
				
				dayTime.clear();
				for(String t:times){
					dayTime.add(t);
				}
				String duration = arr[3];
				
				// Date logic
				//Date dueDate = finalDayBox.getHighlightedDate();
				//Date curDate = CalendarUtil.
				Date dueDate = new Date();
				CalendarUtil.addDaysToDate(dueDate, Integer.parseInt(duration));
				selectedDate = dueDate;
				finalDayBox.setValue(dueDate, true);
				
				messageBox.setText(text);
				
				// load weekdays
				for (int i = 0; i < weekdays.length; i++) {
					if (Integer.parseInt(weekdays[i]) == 0) {
						weekArray[i] = false;
					} else {
						weekArray[i] = true;
					}
				}

				// load times
				String patientLabelString = ((Label) individualPanel.getWidget(0)).getText();
				updateMiddlePanel(patientLabelString.replaceAll("Patient phone: ", ""));
			}

		});
		
		
		// splitting array
	}
	private void showDialog(String textToShow){
		//addPatientBox.setText("Add a patient.");
		//addPatientBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		//final Button addClick = new Button("Add");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		VerticalPanel dialogVPanel = new VerticalPanel();
		//phoneBox.setText("+358");
		VerticalPanel pan = new VerticalPanel();
		
		pan.addStyleName("dialogVPanel");
		//dialogVPanel.add(new HTML("<b>*Patient Phone number:</b>"));
		//dialogVPanel.add(phoneBox);
		//dialogVPanel.add(new HTML("Patient name(optional):"));
		//dialogVPanel.add(patientNameBox);
		popupPanel.setText("Info");
		pan.add(new HTML("<b>"+textToShow+"</b"));
		pan.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		pan.add(closeButton);
		popupPanel.setWidget(dialogVPanel);
		popupPanel.center();
		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addPatientBox.hide();
			}
		});
	}
	private void updateMiddlePanel(String patient) {
		// clear panel
		// new elements
		//finalDayBox = new DatePicker();
		Label patientName = new Label();
		patientName.setText("Patient phone: " + patient);

		Button saveData = new Button("Submit");

		saveData.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				submitTask();
			}

		});

		for (int i = 0; i < 7; i++) {
			days[i] = new CheckBox();
		}
		days[0].setText("Monday");
		days[1].setText("Tuesday");
		days[2].setText("Thirsday");
		days[3].setText("Wednesday");
		days[4].setText("Friday");
		days[5].setText("Saturday");
		days[6].setText("Sunday");
		// check used days
		for (int i = 0; i < 7; i++) {
			days[i].setValue(weekArray[i]);
		}

		HorizontalPanel dayPanel = new HorizontalPanel();
		for (CheckBox box : days) {
			dayPanel.add(box);
		}

		timePanel.clear();
			for (int i = 0; i < dayTime.size(); i++) {
				TextBox hour = new TextBox();
				hour.setWidth("15px");
				hour.setMaxLength(2);

				Label deli = new Label();
				deli.setText(":");
				deli.setWidth("8px");
				deli.setAutoHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

				TextBox minute = new TextBox();
				minute.setWidth("15px");
				minute.setMaxLength(2);
				
				
				String[] hourMinute = dayTime.get(i).split(":");
				hour.setText("" + hourMinute[0]);
				minute.setText("" + hourMinute[1]);
				timePanel.add(hour);
				timePanel.add(deli);
				timePanel.add(minute);
			}
			
		
		// handler to add new time schedules, remove old handler
		if(addTimeHandler!=null)
		addTimeHandler.removeHandler();
		
		addTimeHandler = addSchedule.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTimeBox();
			}
		});
		
		timePanel.add(addSchedule);
		
		messageBox.setTitle("Message to Send:");
		messageBox.setWidth("350px");
		messageBox.setHeight("250px");
		requestTemplateList();
		
		individualPanel.clear();

		individualPanel.add(patientName);
		individualPanel.add(templatesList);
		individualPanel.add(finalDayBox);
		individualPanel.add(dayPanel);
		individualPanel.add(timePanel);
		individualPanel.add(messageBox);
		individualPanel.add(saveData);
		//individualPanel.add(addSchedule);
		
		holder.insert(individualPanel, 1);

	}

	private void clearUI() {
		patientsPanel.clear();
		individualPanel.clear();
		holder.clear();

	}

	private void initUI() {
		clearUI();

		addPatient.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addPatientPopup();
			}

		});

		updatePatients();

		holder.insert(patientsPanel, 0);
		holder.insert(individualPanel, 1);

		RootPanel.get("mainPanel").add(bar);
		RootPanel.get("mainPanel").add(holder);

	}

	public void onModuleLoad() {
		dayTime.add("00:00");
		templatesList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				loadTemplate(templatesList.getSelectedItemText());
			}
		});
		finalDayBox.addValueChangeHandler(new ValueChangeHandler<Date>(){

			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				selectedDate = event.getValue();				
			}
			
		});
		
		Command issueLogout = new Command() {

			@Override
			public void execute() {
				// loginService.logout();
			}

		};

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
	}
}
