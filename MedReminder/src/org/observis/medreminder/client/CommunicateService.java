package org.observis.medreminder.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("communicate")
public interface CommunicateService extends RemoteService {
	String addPatient(String name) throws IllegalArgumentException;
	String loadPatients(String doctorName) throws IllegalArgumentException;
}
