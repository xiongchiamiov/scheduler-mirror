package scheduler.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import scheduler.model.db.DatabaseException;
import scheduler.model.db.IDBCourse;
import scheduler.model.db.IDBCourseAssociation;
import scheduler.model.db.IDBEquipmentType;
import scheduler.model.db.IDBOfferedDayPattern;
import scheduler.model.db.IDBUsedEquipment;
import scheduler.model.db.IDatabase.NotFoundException;

public class Course extends Identified {
	private final Model mModel;
	
	IDBCourse underlyingCourse;
	
	private boolean usedEquipmentLoaded;
	private Set<String> usedEquipmentDescriptions;
	
	private boolean offeredDayPatternsLoaded;
	private Collection<Set<Day>> offeredDayPatterns;
	
	public boolean lectureLoaded; // public for now, for testing
	private Course lecture;
	private Boolean cachedTetheredToLecture;
	
	private boolean documentLoaded;
	private Document document;
	
	
	Course(Model model, IDBCourse underlyingCourse) {
		this.mModel = model;
		this.underlyingCourse = underlyingCourse;
		
		// make sure its not in the cache yet (how could it be, we're not even
		// done with the constructor)
		if (!underlyingCourse.isTransient())
			assert (!model.courseCache.inCache(underlyingCourse));
	}
	
	
	// PERSISTENCE FUNCTIONS
	
	public Course insert() throws DatabaseException {
		assert (isTransient());
		assert (document != null);
		mModel.courseCache.insert(this);
		putOfferedDayPatternsIntoDB();
		putUsedEquipmentIntoDB();
		putAssociationIntoDB();
		return this;
	}
	
	public void update() throws DatabaseException {
		assert (!isTransient());
		removeAssociationFromDB();
		removeOfferedDayPatternsFromDB();
		removeUsedEquipmentFromDB();
		putUsedEquipmentIntoDB();
		putOfferedDayPatternsIntoDB();
		putAssociationIntoDB();
		mModel.courseCache.update(this);
	}
	
	public void delete() throws DatabaseException {
		removeAssociationFromDB();
		removeOfferedDayPatternsFromDB();
		removeUsedEquipmentFromDB();
		mModel.courseCache.delete(this);
	}
	
	
	// ENTITY ATTRIBUTES
	
	public Integer getID() {
		return underlyingCourse.getID();
	}
	
	public boolean isSchedulable() {
		return underlyingCourse.isSchedulable();
	}
	public void setIsSchedulable(boolean isSchedulable) {
		underlyingCourse.setIsSchedulable(isSchedulable);
	}
	
	public String getName() {
		return underlyingCourse.getName();
	}
	public void setName(String name) {
		underlyingCourse.setName(name);
	}
	
	public String getCatalogNumber() {
		return underlyingCourse.getCalatogNumber();
	}
	public void setCatalogNumber(String catalogNumber) {
		underlyingCourse.setCatalogNumber(catalogNumber);
	}
	
	public String getDepartment() {
		return underlyingCourse.getDepartment();
	}
	public void setDepartment(String department) {
		underlyingCourse.setDepartment(department);
	}
	
	public String getWTU() {
		return underlyingCourse.getWTU();
	}
	public void setWTU(String wtu) {
		underlyingCourse.setWTU(wtu);
	}
	public int getWTUInt() {
		return Integer.parseInt(getWTU());
	}
	
	public String getSCU() {
		return underlyingCourse.getSCU();
	}
	public void setSCU(String scu) {
		underlyingCourse.setSCU(scu);
	}
	
	public String getNumSections() {
		return underlyingCourse.getNumSections();
	}
	public int getNumSectionsInt() {
		return Integer.parseInt(getNumSections());
	}
	public void setNumSections(String numSections) {
		underlyingCourse.setNumSections(numSections);
	}
	
	public enum CourseType {
		LEC, LAB, SEM, DIS, ACT, IND
	};
	
	public CourseType getTypeEnum() {
		return CourseType.valueOf(getType());
	}
	public String getType() {
		return underlyingCourse.getType();
	}
	public void setType(String type) {
		underlyingCourse.setType(type);
	}
	
	public String getMaxEnrollment() {
		return underlyingCourse.getMaxEnrollment();
	}
	public void setMaxEnrollment(String maxEnrollment) {
		underlyingCourse.setMaxEnrollment(maxEnrollment);
	}
	public int getMaxEnrollmentInt() {
		return Integer.parseInt(getMaxEnrollment());
	}
	
	public String getNumHalfHoursPerWeek() {
		return underlyingCourse.getNumHalfHoursPerWeek();
	}
	public int getNumHalfHoursPerWeekInt() {
		return Integer.parseInt(getNumHalfHoursPerWeek());
	}
	public void setNumHalfHoursPerWeek(String numHalfHoursPerWeek) {
		underlyingCourse.setNumHalfHoursPerWeek(numHalfHoursPerWeek);
	}
	
	
	
	// ENTITY RELATIONS
	
	
	// Day Patterns
	
	public Collection<Set<Day>> getDayPatterns() throws DatabaseException {
		if (!offeredDayPatternsLoaded) {
			offeredDayPatterns = new LinkedList<Set<Day>>();
			for (IDBOfferedDayPattern offered : mModel.database.findOfferedDayPatternsForCourse(underlyingCourse))
				offeredDayPatterns
						.add(daysFromIntegers(mModel.database.getDayPatternForOfferedDayPattern(offered).getDays()));
			
			offeredDayPatternsLoaded = true;
		}
		return offeredDayPatterns;
	}
	
	public void setDayPatterns(Collection<Set<Day>> dayPatterns) {
		this.offeredDayPatternsLoaded = true;
		this.offeredDayPatterns = dayPatterns;
	}
	
	private void removeOfferedDayPatternsFromDB() throws DatabaseException {
		for (IDBOfferedDayPattern offered : mModel.database.findOfferedDayPatternsForCourse(underlyingCourse))
			mModel.database.deleteOfferedDayPattern(offered);
	}
	
	private static Set<Day> daysFromIntegers(Set<Integer> integers) {
		Set<Day> result = new TreeSet<Day>();
		for (Integer integer : integers)
			result.add(Day.values()[integer]);
		return result;
	}
	
	private static Set<Integer> daysToIntegers(Set<Day> days) {
		Set<Integer> result = new TreeSet<Integer>();
		for (Day day : days)
			result.add(day.ordinal());
		return result;
	}
	
	private void putOfferedDayPatternsIntoDB() throws DatabaseException {
		if (!offeredDayPatternsLoaded)
			return;
		try {
			for (Set<Day> dayPattern : offeredDayPatterns) {
				Set<Integer> integers = daysToIntegers(dayPattern);
				mModel.database.insertOfferedDayPattern(underlyingCourse, mModel.database.findDayPatternByDays(integers),
						mModel.database.assembleOfferedDayPattern());
			}
		}
		catch (NotFoundException e) {
			throw new AssertionError(e);
		}
	}
	
	
	
	// Used Equipment
	
	private void putUsedEquipmentIntoDB() throws DatabaseException {
		if (!usedEquipmentLoaded)
			return;
		try {
			for (String usedEquipmentDescription : usedEquipmentDescriptions)
				mModel.database.insertUsedEquipment(underlyingCourse,
						mModel.database.findEquipmentTypeByDescription(usedEquipmentDescription),
						mModel.database.assembleUsedEquipment());
		}
		catch (NotFoundException e) {
			throw new AssertionError(e);
		}
	}
	
	private void removeUsedEquipmentFromDB() throws DatabaseException {
		for (IDBUsedEquipment usedEquipment : mModel.database.findUsedEquipmentByEquipmentForCourse(underlyingCourse)
				.values())
			mModel.database.deleteUsedEquipment(usedEquipment);
	}
	
	public Set<String> getUsedEquipment() throws DatabaseException {
		if (!usedEquipmentLoaded) {
			usedEquipmentDescriptions = new HashSet<String>();
			for (IDBEquipmentType equipment : mModel.database.findUsedEquipmentByEquipmentForCourse(underlyingCourse)
					.keySet())
				usedEquipmentDescriptions.add(equipment.getDescription());
			usedEquipmentLoaded = true;
		}
		
		return usedEquipmentDescriptions;
	}
	
	public void setUsedEquipment(Set<String> usedEquipmentDescriptions) {
		this.usedEquipmentLoaded = true;
		this.usedEquipmentDescriptions = usedEquipmentDescriptions;
	}
	
	
	
	// Lecture / Tethered
	
	private void putAssociationIntoDB() throws DatabaseException {
		if (!lectureLoaded)
			return;
		if (lecture != null)
			mModel.database.associateLectureAndLab(lecture.underlyingCourse, underlyingCourse);
	}
	
	private void removeAssociationFromDB() throws DatabaseException {
		if (!lectureLoaded)
			return;
		if (lecture != null)
			mModel.database.disassociateLectureAndLab(lecture.underlyingCourse, underlyingCourse);
	}
	
	private void loadLectureAndTethered() throws DatabaseException {
		if (lectureLoaded)
			return;
		
		assert (lecture == null);
		assert (cachedTetheredToLecture == null);
		assert(underlyingCourse.getType() != null);
		
//		System.out.println("is lab? " + underlyingCourse.getType().equals("LAB"));
		
		if (underlyingCourse.getType().equals("LAB")) {
			IDBCourseAssociation assoc = mModel.database.getAssociationForLabOrNull(underlyingCourse);
			System.out.println("assoc? " + assoc);
			if (assoc != null) {
				assert (mModel.database.getAssociationLab(assoc).getID() == underlyingCourse.getID());
				lecture = mModel.findCourseByID(mModel.database.getAssociationLecture(assoc).getID());
				cachedTetheredToLecture = assoc.isTethered();
			}
		}
		
		lectureLoaded = true;
	}
	
	public Course getLecture() throws DatabaseException {
		loadLectureAndTethered();
		return lecture;
	}
	
	public void setLecture(Course newLecture) throws DatabaseException {
		assert (newLecture == null || !newLecture.isTransient()); // You need to
																						// insert
																						// something
																						// before you
																						// can
																						// reference
																						// it
		loadLectureAndTethered();
		this.lecture = newLecture;
	}
	
	public Boolean isTetheredToLecture() throws DatabaseException {
		loadLectureAndTethered();
		return cachedTetheredToLecture;
	}
	
	public void setTetheredToLecture(Boolean tetheredToLecture) throws DatabaseException {
		loadLectureAndTethered();
		this.cachedTetheredToLecture = tetheredToLecture;
	}
	
	
	
	// Document
	
	public Document getDocument() throws DatabaseException {
		if (!documentLoaded) {
			assert (document == null);
			document = mModel.findDocumentByID(mModel.database.findDocumentForCourse(underlyingCourse).getID());
			documentLoaded = true;
		}
		return document;
	}
	
	public Course setDocument(Document newDocument) {
		assert (!newDocument.isTransient()); // You need to insert something
															// before you can reference it
		document = newDocument;
		documentLoaded = true;
		return this;
	}
	
	public String toString() {
		return this.getDepartment() + " " + this.getCatalogNumber() + " - " + this.getName();
	}
	
}
