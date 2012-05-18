package scheduler.view.web.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import scheduler.view.web.shared.GenerateException;
import scheduler.model.db.DatabaseException;
import scheduler.view.web.client.GreetingService;
import scheduler.view.web.client.InvalidLoginException;
import scheduler.view.web.shared.CompleteWorkingCopyDocumentGWT;
import scheduler.view.web.shared.CouldNotBeScheduledExceptionGWT;
import scheduler.view.web.shared.CourseGWT;
import scheduler.view.web.shared.ExistingWorkingDocumentDoesntExistExceptionGWT;
import scheduler.view.web.shared.InstructorGWT;
import scheduler.view.web.shared.LocationGWT;
import scheduler.view.web.shared.LoginResponse;
import scheduler.view.web.shared.OriginalDocumentGWT;
import scheduler.view.web.shared.ScheduleItemGWT;
import scheduler.view.web.shared.ServerResourcesResponse;
import scheduler.view.web.shared.SessionClosedFromInactivityExceptionGWT;
import scheduler.view.web.shared.SynchronizeRequest;
import scheduler.view.web.shared.SynchronizeResponse;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {
	private static final boolean LOG_ENTERING_AND_EXITING_CALLS = true;
	
	
	public final GreetingServiceImplInner inner;
	
	public GreetingServiceImpl() {
		this(true);
	}
	
	public GreetingServiceImpl(boolean loadAndSaveFromFileSystem) {
		String filepath = loadAndSaveFromFileSystem ? getDatabaseStateFilepath() : null;
		
		this.inner = new GreetingServiceImplInner(loadAndSaveFromFileSystem, filepath);
	}
	

	Properties readPropertiesFile() throws IOException {
		Properties properties = new Properties();
		InputStream in = GreetingServiceImpl.class.getResourceAsStream("scheduler.properties");
		if (in == null)
			throw new IOException("Couldnt load scheduler.properties (make sure its in GreetingServiceImpl's directory)");
		properties.load(in);
		in.close();
		return properties;
	}

	private String getDatabaseStateFilepath() {
		String filepath;
		boolean applyServletPath;
		
		try {
			Properties properties = readPropertiesFile();
			assert(properties != null);
			
			filepath = properties.getProperty("databasefilepath");
			if (filepath == null)
				throw new Exception("filepath not set!");
			else
				System.out.println("loaded filepath from properties: " + filepath);
			
			String useServletContextRealPathStr = properties.getProperty("useServletContextRealPath");
			if (useServletContextRealPathStr == null) {
				applyServletPath = false;
				System.out.println("Assuming default applyServletPath: " + applyServletPath);
			}
			else {
				applyServletPath =
						useServletContextRealPathStr.equalsIgnoreCase("true") ||
						useServletContextRealPathStr.equalsIgnoreCase("yes") ||
						useServletContextRealPathStr.equalsIgnoreCase("1");
				System.out.println("loaded applyservletpath from properties: " + applyServletPath);
			}
		}
		catch (Exception e) {
			filepath = "DatabaseState.javaser";
			applyServletPath = true;
			
			e.printStackTrace();
			System.err.println("Couldnt load properties, continuing with defaults (filepath=\"" + filepath + "\" applyServletPath=" + applyServletPath + ")");
		}
		
		
		if (applyServletPath) {
			try {
				filepath = getServletContext().getRealPath(filepath);
				System.out.println("Applied servlet path, got: " + filepath);
			}
			catch (Exception e) {
				e.printStackTrace();
				System.err.println("Requested servlet context real path, but getServletContext().getRealPath() threw an exception. Continuing with filepath " + filepath);
			}
		}
		
		assert(filepath != null);
		return filepath;
	}
	
	
	
	
	
	
	
	
	
	// COURSES
	


	@Override
	public LoginResponse loginAndGetAllOriginalDocuments(String username) throws InvalidLoginException {
		LoginResponse result;
		
		if (LOG_ENTERING_AND_EXITING_CALLS)
			System.out.println("Begin GreetingServiceImpl.loginAndGetAllOriginalDocuments(username " + username + ")");
		
		synchronized (this) {
			inner.sanityCheck();
		
			try {
				result = inner.loginAndGetAllOriginalDocuments(username);
			}
			catch (DatabaseException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}

			inner.sanityCheck();
			inner.flushToFileSystem();
		}
		
		if (LOG_ENTERING_AND_EXITING_CALLS)
			System.out.println("End GreetingServiceImpl.loginAndGetAllOriginalDocuments(username " + username + ")");
		
		inner.sanityCheck();
		inner.flushToFileSystem();
		
		return result;
	}
	
	@Override
	public ServerResourcesResponse<OriginalDocumentGWT> getAllOriginalDocuments(int sessionID) throws SessionClosedFromInactivityExceptionGWT {
		ServerResourcesResponse<OriginalDocumentGWT> result;
		
		if (LOG_ENTERING_AND_EXITING_CALLS)
			System.out.println("Begin GreetingServiceImpl.getAllOriginalDocuments(session " + sessionID + ")");
		
		synchronized (this) {
			inner.sanityCheck();
			
			try {
				result = inner.getAllOriginalDocuments(sessionID);
			}
			catch (DatabaseException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			
			inner.sanityCheck();
			inner.flushToFileSystem();
		}

		if (LOG_ENTERING_AND_EXITING_CALLS)
			System.out.println("End GreetingServiceImpl.getAllOriginalDocuments(" + sessionID + ")");
		
		return result;
	}
	
	@Override
	public CompleteWorkingCopyDocumentGWT createAndOpenWorkingCopyForOriginalDocument(int sessionID, int originalDocumentID, boolean openExistingWorkingDocument) throws SessionClosedFromInactivityExceptionGWT, ExistingWorkingDocumentDoesntExistExceptionGWT {
		CompleteWorkingCopyDocumentGWT result;
		
		if (LOG_ENTERING_AND_EXITING_CALLS)
			System.out.println("Begin GreetingServiceImpl.createAndOpenWorkingCopyForOriginalDocument(doc " + originalDocumentID + ")");
		
		synchronized (this) {
			inner.sanityCheck();
			
			result = inner.createAndOpenWorkingCopyForOriginalDocument(sessionID, originalDocumentID, openExistingWorkingDocument);

			inner.sanityCheck();
			inner.flushToFileSystem();
		}

		if (LOG_ENTERING_AND_EXITING_CALLS)
			System.out.println("End GreetingServiceImpl.createAndOpenWorkingCopyForOriginalDocument(doc " + originalDocumentID + ")");
		
		return result;
	}
	
	@Override
	public void saveWorkingCopyToOriginalDocument(int sessionID, int workingCopyDocumentID) throws SessionClosedFromInactivityExceptionGWT {
		if (LOG_ENTERING_AND_EXITING_CALLS)
			System.out.println("Begin GreetingServiceImpl.saveWorkingCopyToOriginalDocument(workingdocid " + workingCopyDocumentID + ")");
		
		synchronized (this) {
			inner.sanityCheck();
			
			try {
				inner.saveWorkingCopyToOriginalDocument(sessionID, workingCopyDocumentID);
			}
			catch (DatabaseException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}

			inner.sanityCheck();
			inner.flushToFileSystem();
		}
		
		if (LOG_ENTERING_AND_EXITING_CALLS)
			System.out.println("End GreetingServiceImpl.saveWorkingCopyToOriginalDocument(workingdocid " + workingCopyDocumentID + ")");
	}
	
	@Override
	public void deleteWorkingCopyDocument(int sessionID, int workingCopyDocumentID) throws SessionClosedFromInactivityExceptionGWT {
		if (LOG_ENTERING_AND_EXITING_CALLS)
			System.out.println("Begin GreetingServiceImpl.deleteWorkingCopyDocument(workingdocid " + workingCopyDocumentID + ")");
		
		synchronized (this) {
			inner.sanityCheck();
			
			try {
				inner.deleteWorkingCopyDocument(sessionID, workingCopyDocumentID);
			}
			catch (DatabaseException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}

			inner.sanityCheck();
			inner.flushToFileSystem();
		}

		if (LOG_ENTERING_AND_EXITING_CALLS)
			System.out.println("End GreetingServiceImpl.deleteWorkingCopyDocument(workingdocid " + workingCopyDocumentID + ")");
	}
	
	@Override
	public void associateWorkingCopyWithNewOriginalDocument(
			int sessionID,
			int workingCopyDocumentID, String newOriginalDocumentName,
			boolean allowOverwrite) throws SessionClosedFromInactivityExceptionGWT {
		if (LOG_ENTERING_AND_EXITING_CALLS)
			System.out.println("Begin GreetingServiceImpl.associateWorkingCopyWithNewOriginalDocument(workingdocid " + workingCopyDocumentID + ")");
		
		System.out.println("associating!");
		
		synchronized (this) {
			inner.sanityCheck();
			
			try {
				inner.associateWorkingCopyWithNewOriginalDocument(sessionID, workingCopyDocumentID, newOriginalDocumentName, allowOverwrite);
			}
			catch (DatabaseException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}

			inner.sanityCheck();
			inner.flushToFileSystem();
		}
		
		if (LOG_ENTERING_AND_EXITING_CALLS)
			System.out.println("End GreetingServiceImpl.associateWorkingCopyWithNewOriginalDocument(workingdocid " + workingCopyDocumentID + ")");
	}
	
	@Override
	public void generateRestOfSchedule(int sessionID, int documentID) throws CouldNotBeScheduledExceptionGWT, SessionClosedFromInactivityExceptionGWT, GenerateException {
		if (LOG_ENTERING_AND_EXITING_CALLS)
			System.out.println("Begin GreetingServiceImpl.generateRestOfSchedule(doc " + documentID + ")");
		
		synchronized (this) {
			inner.sanityCheck();
			
			try {
				inner.generateRestOfSchedule(sessionID, documentID);
			}
			catch (Exception e) {
				e.printStackTrace();
				throw new GenerateException(e.toString());
			}

			inner.sanityCheck();
			inner.flushToFileSystem();
		}

		if (LOG_ENTERING_AND_EXITING_CALLS)
			System.out.println("End GreetingServiceImpl.generateRestOfSchedule(doc " + documentID + ")");
	}
	
	@Override
	public SynchronizeResponse<OriginalDocumentGWT> synchronizeOriginalDocuments(int sessionID, SynchronizeRequest<OriginalDocumentGWT> request) throws SessionClosedFromInactivityExceptionGWT {
		SynchronizeResponse<OriginalDocumentGWT> result;
		
		if (LOG_ENTERING_AND_EXITING_CALLS)
			System.out.println("Begin GreetingServiceImpl.synchronizeOriginalDocuments(session " + sessionID + ")");
		
		synchronized (this) {
			inner.sanityCheck();
			
			try {
				result = inner.synchronizeOriginalDocuments(sessionID, request);
			}
			catch (DatabaseException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}

			inner.sanityCheck();
			inner.flushToFileSystem();
		}

		if (LOG_ENTERING_AND_EXITING_CALLS)
			System.out.println("End GreetingServiceImpl.synchronizeOriginalDocuments(" + sessionID + ")");
		
		return result;
	}

	@Override
	public SynchronizeResponse<CourseGWT> synchronizeDocumentCourses(
			int sessionID,
			int documentID,
			SynchronizeRequest<CourseGWT> request) throws SessionClosedFromInactivityExceptionGWT {

		SynchronizeResponse<CourseGWT> result;
		
		if (LOG_ENTERING_AND_EXITING_CALLS)
			System.out.println("Begin GreetingServiceImpl.synchronizeDocumentCourses(session " + sessionID + " doc " + documentID + ")");

		synchronized (this) {
			inner.sanityCheck();
			
			result = inner.synchronizeDocumentCourses(sessionID, documentID, request);

			inner.sanityCheck();
			inner.flushToFileSystem();
		}

		if (LOG_ENTERING_AND_EXITING_CALLS)
			System.out.println("End GreetingServiceImpl.synchronizeDocumentCourses(" + documentID + ")");
		
		return result;
	}
	
	@Override
	public SynchronizeResponse<InstructorGWT> synchronizeDocumentInstructors(
			int sessionID,
			int documentID,
			SynchronizeRequest<InstructorGWT> request) throws SessionClosedFromInactivityExceptionGWT {

		SynchronizeResponse<InstructorGWT> result;
		
		if (LOG_ENTERING_AND_EXITING_CALLS)
			System.out.println("Begin GreetingServiceImpl.synchronizeDocumentInstructors(" + sessionID + ", " + documentID + ")");

		synchronized (this) {
			inner.sanityCheck();
			
			try {
				result = inner.synchronizeDocumentInstructors(sessionID, documentID, request);
			}
			catch (DatabaseException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}

			inner.sanityCheck();
			inner.flushToFileSystem();
		}

		if (LOG_ENTERING_AND_EXITING_CALLS)
			System.out.println("End GreetingServiceImpl.synchronizeDocumentInstructors(" + sessionID + ")");
		
		return result;
	}
	@Override
	public SynchronizeResponse<LocationGWT> synchronizeDocumentLocations(
			int sessionID,
			int documentID,
			SynchronizeRequest<LocationGWT> request) throws SessionClosedFromInactivityExceptionGWT {

		if (LOG_ENTERING_AND_EXITING_CALLS)
			System.out.println("Begin GreetingServiceImpl.synchronizeDocumentLocations(" + documentID + ")");
		
		SynchronizeResponse<LocationGWT> result;
		
		synchronized (this) {
			inner.sanityCheck();
			
			try {
				result = inner.synchronizeDocumentLocations(sessionID, documentID, request);
			}
			catch (DatabaseException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}

			inner.sanityCheck();
			inner.flushToFileSystem();
		}
		
		if (LOG_ENTERING_AND_EXITING_CALLS)
			System.out.println("End GreetingServiceImpl.synchronizeDocumentLocations(" + documentID + ")");
		
		return result;
	}

	@Override
	public SynchronizeResponse<ScheduleItemGWT> synchronizeDocumentScheduleItems(
			int sessionID,
			int documentID,
			SynchronizeRequest<ScheduleItemGWT> request) throws SessionClosedFromInactivityExceptionGWT {

		SynchronizeResponse<ScheduleItemGWT> result;
		
		if (LOG_ENTERING_AND_EXITING_CALLS)
			System.out.println("Begin GreetingServiceImpl.synchronizeDocumentScheduleItems(session " + sessionID + " doc" + documentID + ")");

		synchronized (this) {
			inner.sanityCheck();
			
			try {
				result = inner.synchronizeDocumentScheduleItems(sessionID, documentID, request);
			}
			catch (DatabaseException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}

			inner.sanityCheck();
			inner.flushToFileSystem();
		}

		if (LOG_ENTERING_AND_EXITING_CALLS)
			System.out.println("End GreetingServiceImpl.synchronizeDocumentScheduleItems(" + documentID + ")");
		
		return result;
	}
}
