package org.observis.medreminder.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;

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
	
	
}
