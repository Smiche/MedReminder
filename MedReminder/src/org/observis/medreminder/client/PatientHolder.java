package org.observis.medreminder.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class PatientHolder extends HorizontalPanel{
	
	private final CommunicationServiceAsync comService = GWT
			.create(CommunicationService.class);
	private Button addPatient = new Button("Add");
	private Button removePatient = new Button("Remove");
	private VerticalPanel individualPanel = new VerticalPanel();	
	private Button createCustomMessage = new Button("Add");
	private TextBox phoneBox = new TextBox();
	
	private TextBox patientNameBox = new TextBox();
	private TextBox messageTitleBox = new TextBox();
	private TextBox messageTextBox = new TextBox();
	private TextBox messageHourBox = new TextBox();
	private TextBox messageMinuteBox = new TextBox();
	private TextBox messageDayBox = new TextBox();
	private TextBox deliveryTextBox = new TextBox();
	private TextBox deliveryDateBox = new TextBox();
	private TextBox deliveryTimeBox = new TextBox();
	SingleSelectionModel<String> patientSelectionModel;
	private VerticalPanel patientsPanel = new VerticalPanel();
	private ListBox packagesList = new ListBox();
	private DialogBox addPatientBox = new DialogBox();
	private DialogBox createCustomMessageBox = new DialogBox();
	private DialogBox editDeliveryBox = new DialogBox();
	
	private VerticalPanel deliveriesPanel = new VerticalPanel();
	private ArrayList<Delivery> deliveries = new ArrayList<Delivery>();
	

	
	private DialogBox popupPanel = new DialogBox();
	private VerticalPanel packagePanel = new VerticalPanel();
	private ArrayList<VerticalPanel> messagePanel = new ArrayList<VerticalPanel>();
	
	private String[] patientString;
	private String[] packagesListString;
	private String medValue = "";
	private String selectedPatient = "";
	private String selectedDelivery = "";
	
	private HandlerRegistration closeDialogHandlerReg;
	private Button removeAllDeliveries = new Button("Remove all");
	
	public PatientHolder(){
		patientsPanel.setStyleName("patientsPanel");
		individualPanel.setStyleName("individualPanel");
		deliveriesPanel.setStyleName("deliveriesPanel");
		packagePanel.setStyleName("packagePanel");
		
		addPatient.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addPatientPopup();
			}

		});
		
		removePatient.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				if(!selectedPatient.equalsIgnoreCase("")){
				comService.removePatient(selectedPatient, new AsyncCallback<Void>(){

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Unable to remove patient: "+selectedPatient);						
					}

					@Override
					public void onSuccess(Void result) {
						loadPatients();
						//updatePatients();
						individualPanel.clear();
					}
					
				});
			}
			}
		});
		
		createCustomMessage.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				createCustomMessagePopup();
			}
			
		});
		
		packagesList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				loadPackage(packagesList.getSelectedItemText());
			}
		});
	}
	
	
	
	private void updatePatientDeliveries(){
		comService.getDeliveries(selectedPatient,new AsyncCallback<ArrayList<Delivery>>(){

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Unable to fetch deliveries for patient: "+selectedPatient );
			}

			@Override
			public void onSuccess(ArrayList<Delivery> result) {
				deliveriesPanel.clear();
				deliveries.clear();
				
				deliveries = result;
				if(deliveries.size()>0){
					
					List<String> deliveriesTitle = new ArrayList<String>();
					for(Delivery d:deliveries){
						deliveriesTitle.add(d.date+ " "+d.time);
					}
					TextCell deliveryCell = new TextCell();
					//deliveryCell.
					CellList<String> deliveriesCellList = new CellList<String>(deliveryCell);
					// cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

					final SingleSelectionModel<String> selectionModel = new SingleSelectionModel<String>();
					deliveriesCellList.setSelectionModel(selectionModel);
					selectionModel
							.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
								public void onSelectionChange(SelectionChangeEvent event) {
									String selected = selectionModel.getSelectedObject();
									if (selected != null) {
										selectedDelivery = selected;
										openDeliveryPopup();
									}
								}
							});
					// Window.alert("Size:"+patients.size());
					deliveriesCellList.setRowCount(deliveriesTitle.size(), true);
					deliveriesCellList.setRowData(0, deliveriesTitle);

					//patientsPanel = new VerticalPanel();
					deliveriesPanel.add(deliveriesCellList);
					deliveriesPanel.add(removeAllDeliveries);
					
				}
				
			}
			
		});
	}
	
	private void submitTask() {
		ArrayList<Message> data = new ArrayList<Message>();
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
	
	private void openDeliveryPopup(){
		Delivery cur = null;
		
		for(Delivery d:deliveries){
			if(selectedDelivery.equals(d.date+" "+d.time)){
				cur = d;
			}
		}
		
		editDeliveryBox.setText("Edit delivery.");
		editDeliveryBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		final Button editClick = new Button("Save");
		final Button removeButton = new Button("Remove");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		VerticalPanel dialogVPanel = new VerticalPanel();
		//+phoneBox.setText("+358");
		
		deliveryDateBox.setText(cur.date);
		deliveryTimeBox.setText(cur.time);
		deliveryTextBox.setText(cur.text);
		
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("Delivery date:"));
		dialogVPanel.add(deliveryDateBox);
		dialogVPanel.add(new HTML("Delivery time:"));
		dialogVPanel.add(deliveryTimeBox);
		dialogVPanel.add(new HTML("Delivery text:"));
		dialogVPanel.add(deliveryTextBox);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(editClick);
		dialogVPanel.add(removeButton);
		dialogVPanel.add(closeButton);
		editDeliveryBox.setWidget(dialogVPanel);
		editDeliveryBox.center();
		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				editDeliveryBox.hide();
				deliveryDateBox.setText("");
				deliveryTimeBox.setText("");
				deliveryTextBox.setText("");
				patientSelectionModel.clear();
			}
		});
		
		removeButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				Delivery toRemove = null;
				for(Delivery d:deliveries){
					if(selectedDelivery.equals(d.date +" "+d.time)){
						toRemove = d;
					}
				}
				if(toRemove!=null)
				comService.removeDelivery(toRemove,new AsyncCallback<Void>(){
					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
					}
					@Override
					public void onSuccess(Void result) {
						updatePatientDeliveries();
					}
					
				});
				editDeliveryBox.hide();
				deliveryDateBox.setText("");
				deliveryTimeBox.setText("");
				deliveryTextBox.setText("");
			}
			
		});
		
		editClick.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Delivery toEdit = null;
				for(Delivery d:deliveries){
					if(selectedDelivery.equals(d.date +" "+d.time)){
						toEdit = d;
					}
				}
				
				Delivery changedDelivery = new Delivery(selectedPatient, deliveryTextBox.getText(), deliveryDateBox.getText(), deliveryTimeBox.getText(),"0");
				if(toEdit!=null)
				comService.editDelivery(toEdit,changedDelivery, new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Failure");
							}
							@Override
							public void onSuccess(Void result) {
								// TODO Auto-generated method stub
								updatePatientDeliveries();
							}

						});
				editDeliveryBox.hide();
				deliveryDateBox.setText("");
				deliveryTimeBox.setText("");
				deliveryTextBox.setText("");
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

	private void createCustomMessagePopup(){
		// Create the popup dialog box
		createCustomMessageBox.setText("Add a message.");
		createCustomMessageBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		final Button addClick = new Button("Add");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		VerticalPanel dialogVPanel = new VerticalPanel();
		messageTitleBox.setText("");
		messageTextBox.setText("");
		messageDayBox.setText("");
		messageHourBox.setText("");
		messageMinuteBox.setText("");
		
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>*Title:</b>"));
		dialogVPanel.add(messageTitleBox);
		dialogVPanel.add(new HTML("<b>*Text:</b>"));
		dialogVPanel.add(messageTextBox);
		dialogVPanel.add(new HTML("<b>*Day:</b>"));
		dialogVPanel.add(messageDayBox);
		dialogVPanel.add(new HTML("<b>*Time:</b>"));
		dialogVPanel.add(new HTML("Hour"));
		dialogVPanel.add(messageHourBox);
		dialogVPanel.add(new HTML("Minute"));
		dialogVPanel.add(messageMinuteBox);
		
		
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(addClick);
		dialogVPanel.add(closeButton);
		createCustomMessageBox.setWidget(dialogVPanel);
		createCustomMessageBox.center();
		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				createCustomMessageBox.hide();
				messageTitleBox.setText("");
				messageTextBox.setText("");
				messageDayBox.setText("");
				messageHourBox.setText("");
				messageMinuteBox.setText("");
			}
		});
		addClick.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				//
				VerticalPanel newMsg = new VerticalPanel();
				
				// replace text logic
				String text = messageTextBox.getText();
				//.replaceAll("[value]", "");
				TextBox box = new TextBox();
				box.setText(text);
				//box.setAlignment(TextAlignment.JUSTIFY);
				
				TextBox hour = new TextBox();
				hour.setWidth("15px");
				hour.setMaxLength(2);
				hour.setText(messageHourBox.getText());
				
				TextBox minute = new TextBox();
				minute.setWidth("15px");
				minute.setMaxLength(2);
				minute.setText(messageMinuteBox.getText());

				Label delimeter = new Label(":");
				delimeter.setWidth("8px");
				delimeter
						.setAutoHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

				HorizontalPanel timePane = new HorizontalPanel();

				timePane.add(hour);
				timePane.add(delimeter);
				timePane.add(minute);
				VerticalPanel vp = new VerticalPanel();
				vp.add(new Label("" + messageTitleBox.getText()));
				vp.add(box);
				vp.add(new Label("Day: " + messageDayBox.getText()));
				vp.add(timePane);
				vp.setStyleName("messageItemStyle");
				
				messagePanel.add(vp);
				packagePanel.clear();
				for(VerticalPanel p:messagePanel){
					packagePanel.add(p);
				}
				
				packagePanel.add(createCustomMessage);
				createCustomMessageBox.hide();
				messageTitleBox.setText("");
				messageTextBox.setText("");
				messageDayBox.setText("");
				messageHourBox.setText("");
				messageMinuteBox.setText("");
			}

		});
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" }) void loadPatients() {
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
		
		patientSelectionModel = new SingleSelectionModel<String>();
		cellList.setSelectionModel(patientSelectionModel);
		patientSelectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						String selected = patientSelectionModel.getSelectedObject();
						if (selected != null) {
							selectedPatient = selected;
							updateMiddlePanel();
						}
					}
				});
		// Window.alert("Size:"+patients.size());
		cellList.setRowCount(patients.size(), true);
		cellList.setRowData(0, patients);

		patientsPanel.clear();
		patientsPanel.add(cellList);
		patientsPanel.add(addPatient);
		patientsPanel.add(removePatient);

	}

	@SuppressWarnings("unused")
	private void requestPackagesList() {
		comService.getPackagesList(new AsyncCallback<String>() {
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Unable to fetch packages list.");
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
				
				updatePatientDeliveries();
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
						Window.alert("Unable to fetch package by name.");

					}

					@Override
					public void onSuccess(ArrayList<Message> messages) {
						individualPanel.remove(packagePanel);
						packagePanel.clear();
						messagePanel.clear();
						// templateString = result;
						for (Message msg : messages) {
							// replace text logic
							String text = msg.text;
							//.replaceAll("[value]", "");
							TextBox box = new TextBox();
							box.setText(text);
							//box.setAlignment(TextAlignment.JUSTIFY);
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
							vp.setStyleName("messageItemStyle");
							
							messagePanel.add(vp);
							
						}
						for(VerticalPanel p:messagePanel){
							packagePanel.add(p);
						}
						packagePanel.add(createCustomMessage);
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
		
		this.insert(individualPanel, 1);

	}
	
	private void clearUI() {
		patientsPanel.clear();
		individualPanel.clear();
		this.clear();

	}

	void initUI() {
		clearUI();
		updatePatients();
		
		this.insert(patientsPanel, 0);
		this.insert(individualPanel, 1);
		this.insert(deliveriesPanel, 2);
		//RootPanel.get("mainPanel").add(bar);
		//RootPanel.get("mainPanel").add(this);
	}
}
