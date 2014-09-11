package forms;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import dao.TestDao;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import beans.Student;
import beans.Test;
import beans.TestFormat;
import beans.Group;
import beans.Matter;
import beans.Score;
import beans.Teacher;

public final class TestForm 
{
	private static final String TEACHER_SESSION   = "teacherSession";
	private static final String ID_FIELD          = "id";
	private static final String FORMAT_FIELD      = "format";
	private static final String TEACHER_FIELD     = "teacher";
	private static final String COEFFICIENT_FIELD = "coefficient";
	private static final String DATE_FIELD        = "date";
	private static final String SCORES_FIELD      = "scores";
	private static final String STUDENTS_FIELD    = "students";
    private static final String TITLE_FIELD       = "title";
    private static final String GROUP_FIELD       = "group";
    private static final String MATTER_FIELD      = "matter";
    private static final String AVERAGE_FIELD     = "average";
    private Map<String, String> errors            = new HashMap<String, String>();
    private TestDao testDao;
    
    /**
     * Constructor
     * 
     * @param testDao
     */
    public TestForm(TestDao testDao) 
    {
    	this.testDao = testDao;
    }
    
    /**
     * Creates a test into database
     * 
     * @param request
     * @return test
     */
    public Test create(HttpServletRequest request) 
    {
    	String date        = getFieldVar(request, DATE_FIELD);
    	String format      = getFieldVar(request, FORMAT_FIELD);
    	String title       = getFieldVar(request, TITLE_FIELD);
    	String coefficient = getFieldVar(request, COEFFICIENT_FIELD);
    	String groupId     = getFieldVar(request, GROUP_FIELD);
    	String matterId    = getFieldVar(request, MATTER_FIELD);
    	Teacher teacher    = (Teacher) getSessionVar(request, TEACHER_SESSION);
    	Test test          = new Test();
    	
        try 
        {
        	treatFormatId(format, test);
        	treatTitle(title, test);
        	treatDate(date, test);
        	treatCoefficient(coefficient, test);
        	treatGroupId(groupId, test);
        	treatMatterId(matterId, test);
        	treatTeacher(teacher, test);
            
            if (errors.isEmpty()) testDao.create(test);
            
        } 
        catch (Exception e) 
        {
        	e.printStackTrace();
        }

        return test;
    }
    
    /**
     * Edits a test into a database
     * 
     * @param request
     * @return test
     */
    public Test edit(HttpServletRequest request)
    {
    	String id          = getFieldVar(request, ID_FIELD);
    	String date        = getFieldVar(request, DATE_FIELD);
    	String format      = getFieldVar(request, FORMAT_FIELD);
    	String title       = getFieldVar(request, TITLE_FIELD);
    	String coefficient = getFieldVar(request, COEFFICIENT_FIELD);
    	String matterId    = getFieldVar(request, MATTER_FIELD);
    	String students    = getFieldVar(request, STUDENTS_FIELD);
    	String scores      = getFieldVar(request, SCORES_FIELD);
    	Teacher teacher    = (Teacher) getSessionVar(request, TEACHER_SESSION);
    	Test test          = new Test();

    	try 
        {
    		treatId(id, test);
        	treatFormatId(format, test);
        	treatTitle(title, test);
        	treatDate(date, test);
        	treatCoefficient(coefficient, test);
        	treatMatterId(matterId, test);
        	treatTeacher(teacher, test);
        	treatScores(scores, students, test);
            
            if (errors.isEmpty()) testDao.edit(test);
            else test = testDao.get(test);
            
        } 
        catch (Exception e) 
        {
        	e.printStackTrace();
        }

        return test;
    }
    
    /**
     * Return a test into database
     * 
     * @param request
     * @return test
     */
    public Test get(HttpServletRequest request)
    {
    	String id = getFieldVar(request, ID_FIELD);
    	Test test = new Test();
    	
    	treatId(id, test);
    	test = testDao.get(test);
    
    	return test;
    }
    
    /**
     * Searches one or more tests into database
     *
     * @param request
     * @return tests
     */
    public Set<Test> search(HttpServletRequest request) 
    {
    	String id       = getFieldVar(request, ID_FIELD);
    	String date     = getFieldVar(request, DATE_FIELD);
    	String format   = getFieldVar(request, FORMAT_FIELD);
    	String title    = getFieldVar(request, TITLE_FIELD);
    	String groupId  = getFieldVar(request, GROUP_FIELD);
    	String matterId = getFieldVar(request, MATTER_FIELD);
    	String average  = getFieldVar(request, AVERAGE_FIELD);
    	Set<Test> tests = new TreeSet<Test>();
    	Teacher teacher = (Teacher) getSessionVar(request, TEACHER_SESSION);
    	Test test       = new Test();

    	treatId(id, test);
    	treatMatterId(matterId, test);
    	treatGroupId(groupId, test);
    	treatAverage(average, test);
    	treatFormatId(format, test);
    	treatDate(date, test);
    	test.setTitle(title);
    	test.setTeacher(teacher);
    	tests = testDao.search(test);
        
    	return tests;
    }
    
    /**
     * Deletes a test into database
     * 
     * @param request
     */
    public void delete(HttpServletRequest request)
    {
    	String id       = getFieldVar(request, ID_FIELD);
    	Test test       = new Test();
    	Teacher teacher = (Teacher) getSessionVar(request, TEACHER_SESSION);
    	
    	try 
        {
    		treatId(id, test);
    		treatTeacher(teacher, test);
            
            if (errors.isEmpty()) testDao.delete(test);
            
        } 
        catch (Exception e) 
        {
        	e.printStackTrace();
        }
    }
    
    /**
     *  Treats test's id
     *  
     * @param id
     * @param test
     */
    private void treatId(String id, Test test)
    {
    	try
    	{
    		validateId(id);
    		test.setId(Long.parseLong(id));
    	}
    	catch (Exception e) 
    	{
            setError(ID_FIELD, e.getMessage());
        }
    }
    
    /**
     *  Treat test's title
     *  
     * @param title
     * @param test
     */
    private void treatTitle(String title, Test test) 
    {
    	try 
    	{
            validateTitle(title);
        } 
    	catch (Exception e) 
    	{
            setError(TITLE_FIELD, e.getMessage());
        }
    	
    	test.setTitle(title.substring(0, 1).toUpperCase() + title.substring(1).toLowerCase());
    }
    
    /**
     *  Treats test's date
     *  
     * @param date
     * @param test
     */
    private void treatDate(String date, Test test) 
    {
    	try
    	{
    		validateDate(date);
    		
    		if (date.matches("(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)"))
    		{
    			date = convertDateFormat("dd/MM/yyyy","yyyy-MM-dd", date);
    		}
    	} 	
    	catch (Exception e) 
    	{
    		setError(DATE_FIELD, e.getMessage());
    	}
    	
    	test.setDate(date);
    }
    
    /**
     *  Treats test's coefficient
     *  
     * @param coefficient
     * @param test
     */
    private void treatCoefficient(String coefficient, Test test) 
    {
    	try
    	{
    		validateCoefficient(coefficient);
    		test.setCoefficient(Float.parseFloat(coefficient.replace(",",".")));
    	} 	
    	catch (Exception e) 
    	{
    		setError(COEFFICIENT_FIELD, e.getMessage());
    	}
    }
    
    /**
     *  Treats matter's id of a test
     *  
     * @param matterId
     * @param test
     */
    private void treatMatterId(String matterId, Test test) 
    {
    	Matter matter = new Matter();
    	
    	try
    	{
    		validateMatterId(matterId);
    		matter.setId(Long.parseLong(matterId));
    	}
    	catch (Exception e) 
    	{
    		setError(MATTER_FIELD, e.getMessage());
    	}
    	
    	test.setMatter(matter);
    }
    
    /**
     *  Treats group's id of a test
     *  
     * @param groupId
     * @param test
     */
    private void treatGroupId(String groupId, Test test) 
    {
    	Group group = new Group();
    	
    	try
    	{
    		validateGroupId(groupId);
    		group.setId(Long.parseLong(groupId));
    	}
    	catch (Exception e) 
    	{
    		setError(GROUP_FIELD, e.getMessage());
    	}
    	
    	test.setGroup(group);
    }
    
    /**
     *  Treats test's teacher
     *  
     * @param teacher
     * @param test
     */
    private void treatTeacher(Teacher teacher, Test test) 
    {
    	try
    	{
    		validateTeacher(teacher);
    	}
    	catch (Exception e) 
    	{
    		setError(TEACHER_FIELD, e.getMessage());
    	}
   
    	test.setTeacher(teacher);
    }
    
    /**
     *  Treats format's id of a test
     *  
     * @param formatId
     * @param test
     */
    private void treatFormatId(String formatId, Test test) 
    {
    	TestFormat format = new TestFormat();
    	
    	try
    	{
    		validateFormatId(formatId);
    		format.setId(Long.parseLong(formatId));
    	}
    	catch (Exception e) 
    	{
    		setError(FORMAT_FIELD, e.getMessage());
    	}
    	
    	test.setFormat(format);
    }

    /**
     *  Treats test's average
     *  
     * @param average
     * @param test
     */
    private void treatAverage(String average, Test test) 
    {
    	try
    	{
    		validateAverage(average);
    		test.setAverage(Float.parseFloat(average));
    	}
    	catch (Exception e) 
    	{
    		setError(AVERAGE_FIELD, e.getMessage());
    	}
    }
    
    /**
     *  Treats student's id of a score
     *  
     * @param studentId
     * @param score
     */
    private void treatStudentId(String studentId, Score score)
    {
    	Student etudiant = new Student();
    	
    	try
    	{
    		validateStudentId(studentId);
    		etudiant.setId(Long.parseLong(studentId));
    	}
    	catch (Exception e) 
    	{
    		setError(STUDENTS_FIELD, e.getMessage());
    	}
 
    	score.setStudent(etudiant);
    }
    
    /**
     * Treats test's score
     * 
     * @param scoreString
     * @param score
     */
    private void treatScore(String scoreString, Score score)
    {
    	try
    	{
    		validateScore(scoreString);
    		score.setScore(Float.parseFloat(scoreString.replace(",",".")));
    	}
    	catch(Exception e)
    	{
    		setError(SCORES_FIELD, e.getMessage());
    	}
    }
    
    /**
     * Treats test's scores
     * 
     * @param scores
     * @param students
     * @param test
     */
    private void treatScores(String scores, String students, Test test)
    {
    	String[] tabScores = scores.split("-");
    	String[] tabStudentIds = students.split("-");
    	Set<Score> scoreList = new HashSet<Score>();
    	
        for (int i=0;i<tabScores.length;i++)
        {
            Score score = new Score();
            treatStudentId(tabStudentIds[i], score);
            treatScore(tabScores[i], score);
            scoreList.add(score);
        }
        
    	test.setScores(scoreList);
    }

    /**
     * Validates test's id
     * 
     * @param id
     * @throws Exception
     */
    private void validateId(String id) throws Exception 
    {
        if ((id == null)) throw new Exception("Le numéro d'identification est nul");
    }
    
    /**
     * Validates test's title
     * 
     * @param title
     * @throws Exception
     */
    private void validateTitle(String title) throws Exception 
    {
        if ((title == null) || (title.length() < 2) || (title.length() > 50)) 
        {
            throw new Exception("Veuillez entrer un intitulé de 2 à 50 caractères");
        }
    }
    
    /**
     * Validates test's date
     * 
     * @param date
     * @throws Exception
     */
    private void validateDate(String date) throws Exception 
    {
        if ((date == null) || ((!date.matches("(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)")) 
        		&& (!date.matches("((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])")))) 
        {
            throw new Exception("Veuillez entrer un date au format JJ/MM/AAAA");
        }
    }
    
    /**
     * Validates matter's id of a test
     * 
     * @param matterId
     * @throws Exception
     */
    private void validateMatterId(String matterId) throws Exception 
    {
        if (matterId == null) throw new Exception("Veuillez sélectionner une matière");
        
    }
    
    /**
     * Validates test's teacher
     * 
     * @param teacher
     * @throws Exception
     */
    private void validateTeacher(Teacher teacher) throws Exception 
    {
        if (teacher == null) throw new Exception("Professeur inconnu");
        
    }
    
    /**
     * Validates group's id of a test
     * 
     * @param groupId
     * @throws Exception
     */
    private void validateGroupId(String groupId) throws Exception 
    {
        if (groupId == null) throw new Exception("Veuillez sélectionner un groupe");
        
    }
    
    /**
     * Validates student's id of a test
     * 
     * @param studentId
     * @throws Exception
     */
    private void validateStudentId(String studentId) throws Exception 
    {
        if (studentId == null) throw new Exception("Etudiant inconnu");
        
    }
    
    /**
     * Validates format's id of a test
     * 
     * @param formatId
     * @throws Exception
     */
    private void validateFormatId(String formatId) throws Exception 
    {
        if (formatId == null) throw new Exception("format d'examen inconnu");
        
    }
    
    /**
     * Validates test's average
     * 
     * @param average
     * @throws Exception
     */
    private void validateAverage(String average) throws Exception 
    {
        if (average == null) throw new Exception("Veuillez entrez un nombre décimal");
        
    }
    
    /**
     * Validates test's coefficent
     * 
     * @param coefficient
     * @throws Exception
     */
    private void validateCoefficient(String coefficient) throws Exception 
    {
        if ((coefficient == null) || (!coefficient.matches("[0-9,.]{1,5}"))) 
        {
            throw new Exception("Veuillez entrer un nombre décimal");
        }
    }

    /**
     * Validates a test's score
     * 
     * @param score
     * @throws Exception
     */
    private void validateScore(String score) throws Exception 
    {
        if ((score == null) || (!score.matches("[0-9,.]{1,5}"))) throw new Exception("Veuillez entrer un nombre décimal");
        
    }
    
    /**
     * Returns errors
     * 
     * @return errors
     */
    public Map<String, String> getErrors() 
    {
        return errors;
    }
  
    /**
     * Sets an error
     * 
     * @param field
     * @param message
     */
    private void setError(String field, String message)
    {
        errors.put(field, message);
    }
    
    /**
     * Returns a field variable
     * 
     * @param request
     * @param fieldVar
     * @return var
     */
    private static String getFieldVar(HttpServletRequest request, String fieldVar) 
    {
        String var = request.getParameter(fieldVar);
        
        return ((var == null) || (var.trim().length() == 0) ? null : var.trim());
    }
    
    /**
     * Returns a session variable
     * 
     * @param request
     * @param sessionVar
     * @return object
     */
    private static Object getSessionVar(HttpServletRequest request, String sessionVar) 
    {
    	HttpSession session = request.getSession();
    	Object object       = session.getAttribute(sessionVar);
    	
        return ((object == null) ? null : object);
    }
    
    /**
     * Convert a date format
     * 
     * @param oldFormat
     * @param newFormat
     * @param dateString
     * @return
     */
	private String convertDateFormat(String oldFormat, String newFormat, String dateString)
    {
    	SimpleDateFormat ANC_FORMAT  = new SimpleDateFormat(oldFormat);
    	SimpleDateFormat NOUV_FORMAT = new SimpleDateFormat(newFormat);
    	java.util.Date dateUtil      = new java.util.Date();
    	java.sql.Date dateSQL        = null;
    	try
    	{
    		dateUtil = ANC_FORMAT.parse(dateString);
    		dateSQL  = new java.sql.Date(dateUtil.getTime());
    	} 	
    	catch (Exception e) 
    	{
    		e.printStackTrace();
    	}
    	
    	return NOUV_FORMAT.format(dateSQL);
    }
}
