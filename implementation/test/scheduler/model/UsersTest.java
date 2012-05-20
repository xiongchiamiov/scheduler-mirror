package scheduler.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import scheduler.model.Document;
import scheduler.model.User;
import scheduler.model.Model;
import scheduler.model.db.DatabaseException;
import scheduler.model.db.IDatabase.NotFoundException;

public abstract class UsersTest extends ModelTestCase {
	private static final int START_HALF_HOUR = 14; // 7am
	private static final int END_HALF_HOUR = 44; // 10pm
	
	public void testTransientsNotInserted() throws DatabaseException {
		Model model = createBlankModel();
		
		User user = model.createTransientUser("eovadia", true);
		assertEquals(user.getID(), null);
		
		assertTrue(model.isEmpty());
		model.closeModel();
	}

	public void testInsertAndFindBasicUser() throws DatabaseException {
		Model model = createBlankModel();
		
		{
			model.createTransientUser("eovadia", true)
					.insert()
					.getID();
		}
		
		User found = model.findUserByUsername("eovadia");
		assertTrue(found.getUsername().equals("eovadia"));
		assertTrue(found.isAdmin() == true);
		model.closeModel();
	}

	public void testInsertAndDeleteUser() throws DatabaseException {
		Model model = createBlankModel();
		
		User user = model.createTransientUser("eovadia", true)
				.insert();
		
		user.delete();
		
		assertTrue(model.isEmpty());
		model.closeModel();
	}
	
	public void testUpdateUser() throws DatabaseException {
		Model model = createBlankModel();

		{
			User user = model.createTransientUser("eovadia", true)
					.insert();
			user.setUsername("vkalland");
			user.setAdmin(false);
			user.update();
		}
		
		User user = model.findUserByUsername("vkalland");
		assertTrue(user.getUsername().equals("vkalland"));
		assertTrue(user.isAdmin() == false);
		model.closeModel();
	}

	public void testDeleteUser() throws Exception {
		Model model = createBlankModel();

		User user = model.createTransientUser("eovadia", true)
				.insert();
		user.setUsername("vkalland");
		user.setAdmin(false);
		user.delete();
	
		assertTrue(model.isEmpty());
		model.closeModel();
	}
}
