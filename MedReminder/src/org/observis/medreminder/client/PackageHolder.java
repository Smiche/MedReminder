package org.observis.medreminder.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class PackageHolder extends HorizontalPanel{
	private final CommunicationServiceAsync comService = GWT
			.create(CommunicationService.class);
	private Button createMessage = new Button("Add");
	private TextBox packageNameBox = new TextBox();
	
	private TextBox messageTitleBox = new TextBox();
	private TextBox messageTextBox = new TextBox();
	private TextBox messageHourBox = new TextBox();
	private TextBox messageMinuteBox = new TextBox();
	private TextBox messageDayBox = new TextBox();
	
	private DialogBox createMessageBox = new DialogBox();
	
	private VerticalPanel packagesListPanel = new VerticalPanel();
	private VerticalPanel packagesMiddlePanel = new VerticalPanel();
	private ArrayList<VerticalPanel> messagesMiddlePanel = new ArrayList<VerticalPanel>();
	
	private DialogBox addPackageBox = new DialogBox();
	
	private String[] packagesListString;
	private String medValue = "";
	private String selectedPatient = "";
	private String selectedPackage = "";
	private String selectedDelivery = "";
	private Button addPackage = new Button("Add");
	private Button removePackage = new Button("Remove");
	
	public PackageHolder(){
		addPackage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addPackagePopup();
			}

		});
		removePackage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				comService.removePackage(selectedPackage, new AsyncCallback<Void>(){

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Failed to remove package.");	
					}

					@Override
					public void onSuccess(Void result) {
						initPackageHolder();
						selectedPackage = "";
					}
					
				});
			}

		});
		
		createMessage.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				createMessagePopup();				
			}
			
		});
		
		this.initPackageHolder();
	}
	
	private void addPackagePopup(){
		// Create the popup dialog box
		addPackageBox.setText("Add a package.");
		addPackageBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		final Button addClick = new Button("Add");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		VerticalPanel dialogVPanel = new VerticalPanel();
		packageNameBox.setText("");

		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>*Package name:</b>"));
		dialogVPanel.add(packageNameBox);
		
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(addClick);
		dialogVPanel.add(closeButton);
		addPackageBox.setWidget(dialogVPanel);
		addPackageBox.center();
		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addPackageBox.hide();
				packageNameBox.setText("+358");
			}
		});
		addClick.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				comService.addPackage(packageNameBox.getText(), new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Failure");
							}

							@Override
							public void onSuccess(Void result) {
								initPackageHolder();
							}

						});
				addPackageBox.hide();
				packageNameBox.setText("");
			}

		});
	}
	
	private void createMessagePopup(){
		// Create the popup dialog box
		createMessageBox.setText("Add a message.");
		createMessageBox.setAnimationEnabled(true);
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
		createMessageBox.setWidget(dialogVPanel);
		createMessageBox.center();
		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				createMessageBox.hide();
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
				Message m = new Message(messageTitleBox.getText(), messageTextBox.getText(), messageHourBox.getText()+":"+messageMinuteBox.getText(), messageDayBox.getText());
				
				comService.addMessage(m,selectedPackage, new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Failure");
							}

							@Override
							public void onSuccess(Void result) {
								//initPackageHolder();
								updatePackageHolderMiddle();
							}

						});
				createMessageBox.hide();
				messageTitleBox.setText("");
				messageTextBox.setText("");
				messageDayBox.setText("");
				messageHourBox.setText("");
				messageMinuteBox.setText("");
			}

		});
		
	}
	
	private void loadEditablePackage(String packageName){
		// interface to get template
		comService.getPackage(packageName,
				new AsyncCallback<ArrayList<Message>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Unable to fetch template by name.");

					}

					@Override
					public void onSuccess(ArrayList<Message> messages) {
						//individualPanel.remove(packagePanel);
						
						packagesMiddlePanel.clear();
						packagesMiddlePanel.add(createMessage);
						messagesMiddlePanel.clear();
						
						// templateString = result;
						if(!messages.isEmpty())
						for (Message msg : messages) {
							if(msg.text == null) break;
							// replace text logic
							String text = msg.text;
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
							
							Button removeButton = new Button("Remove");
							removeButton.addClickHandler(new ClickHandler(){

								@Override
								public void onClick(ClickEvent event) {
									Button cur = (Button) event.getSource();
									
									VerticalPanel mp = (VerticalPanel) cur.getParent();
									Label lab = (Label)mp.getWidget(0);
									
									String titleToDelete = lab.getText();
									TextBox lab2 = (TextBox)mp.getWidget(1);
									String textToDelete = lab2.getText();
									comService.removeMessage(titleToDelete, textToDelete, new AsyncCallback<Void>(){

										@Override
										public void onFailure(Throwable caught) {
											// TODO Auto-generated method stub
											
										}

										@Override
										public void onSuccess(Void result) {
											loadEditablePackage(selectedPackage);
										}
										
									});
								}
								
							});
							
							VerticalPanel vp = new VerticalPanel();
							vp.add(new Label("" + msg.title));
							vp.add(box);
							vp.add(new Label("Day: " + msg.day));
							vp.add(timePane);
							vp.add(removeButton);
							
							messagesMiddlePanel.add(vp);
							
						}
						for(VerticalPanel p:messagesMiddlePanel){
							packagesMiddlePanel.add(p);
						}
						PackageHolder.this.add(packagesMiddlePanel);
					}

				});
	}
	
	
	private void initPackageHolder(){
		comService.getPackagesList(new AsyncCallback<String>(){

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to load packages.");			
			}

			@Override
			public void onSuccess(String result) {
				PackageHolder.this.clear();
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
									selectedPackage = selected;
									updatePackageHolderMiddle();
								}
							}
						});
				// Window.alert("Size:"+patients.size());
				
				
				packagesCellList.setRowCount(packages.size(), true);
				packagesCellList.setRowData(0, packages);		
				
				packagesListPanel.add(packagesCellList);
				packagesListPanel.add(addPackage);
				packagesListPanel.add(removePackage);
				
				PackageHolder.this.add(packagesListPanel);
			}
			
		});
	}
	private void updatePackageHolderMiddle(){
		this.clear();
		this.add(packagesListPanel);
		loadEditablePackage(selectedPackage);		
	}
}
