package services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import com.mongodb.MongoClient;

import models.Course;
import models.Grade;
import models.Student;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

@Path("/service")
public class RestService {

	final Morphia morphia;
	final Datastore datastore;
	
	public RestService() {	
		morphia = new Morphia();
		datastore = morphia.createDatastore(new MongoClient(), "morphia_example");
		morphia.mapPackage("models");
		datastore.ensureIndexes();
	}

	/** GET COLLECTIONS METHODS**/
	
	@GET
	@Path("/students")
	@Produces({"application/xml", "application/json"})
	public List<Student> getStudents(@QueryParam("name") String name, @QueryParam("surname") String surname,
									 @DefaultValue("1900-01-01") @QueryParam("minDate") Date minDate,
									 @DefaultValue("2017-01-01") @QueryParam("maxDate") Date maxDate,
                                     @QueryParam("param") String param) {
		Query<Student> query = datastore.createQuery(Student.class);

		/*if(name != null) {
			query = query.field("name").contains(name);
		}
		if(surname != null) {
			query = query.field("surname").contains(surname);
		}*/

        if(param != null) {
            query.or(
                    query.criteria("name").containsIgnoreCase(param),
                    query.criteria("surname").containsIgnoreCase(param)
            );
        }

		final List<Student> students = query
				.field("date")
				.greaterThan(minDate)
				.field("date")
				.lessThan(maxDate)
				.asList();
		
		return students;
	}
	
	@GET
	@Path("/courses")
	@Produces({"application/xml", "application/json"})
	public List<Course> getCourses(@QueryParam("professor") String professor, @QueryParam("param") String param) {
		Query<Course> query = datastore.createQuery(Course.class);

		if(professor != null) {
			query = query.field("professor").contains(professor);
		}

        if(param != null) {
            query.or(
                    query.criteria("name").containsIgnoreCase(param),
                    query.criteria("professor").containsIgnoreCase(param)
            );
        }

		final List<Course> courses = query.asList();

		return courses;
	}

	@GET
	@Path("/grades/{course}")
	@Produces({"application/xml", "application/json"})
	public List<Grade> getGrades(@PathParam("course") ObjectId course) {
		return Course.getCourseGrade(datastore, course);
	}

	/** GET OBJECTS METHODS**/

	@GET
	@Path("/students/{index}")
	@Produces({"application/xml", "application/json"})
	public Student getStudent(@PathParam("index") int index) {
		return Student.findStudentbyId(datastore, index);
	}

	@GET
	@Path("/courses/{id}")
	@Produces({"application/xml", "application/json"})
	public Course getCourse(@PathParam("id") ObjectId id) {
		return Course.findCoursebyId(datastore, id);
	}

	@GET
	@Path("/grades/{course}/{id}")
	@Produces({"application/xml", "application/json"})
	public Grade getGrade(@PathParam("course") ObjectId course, @PathParam("id") int id) {
		Course courseObj = Course.findCoursebyId(datastore, course);
		List<Grade> grades = new ArrayList<Grade>();

		if(courseObj != null) {
			grades = courseObj.getGrades();
		}

        for(Grade grade : grades) {
            if(grade.getId() == id) {
                return grade;
            }
        }

		return null;
	}

	/** POST METHODS**/
	
	@POST
	@Path("/students")
	@Consumes({"application/xml", "application/json"})
	public Response createStudent(Student studentArg) {
		final Query<Student> query = datastore.createQuery(Student.class);
		final List<Student> students = query.asList();
		int studentsNum = students.size();
		int newIndex = 1;
		
		if(studentsNum > 0) {
			newIndex = students.get(studentsNum - 1).getIndex() + 1;
		}
	
		Student student = new Student(newIndex, studentArg.getName(), studentArg.getSurname(), studentArg.getDate());
		datastore.save(student);
		URI location = null;
		try {
			location = new URI("http:/localhost:9998/service/students/" + Integer.toString(newIndex));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return Response.created(location).build();
	}
	
	@POST
	@Path("/courses")
	@Consumes({"application/xml", "application/json"})
	public Response createCourse(Course courseArg) {
		Course course = new Course(courseArg.getName(), courseArg.getProfessor());
		datastore.save(course);
		URI location = null;
		try {
			location = new URI("http:/localhost:9998/service/courses/" + course.getId().toString());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return Response.created(location).build();
	}
	
	@POST
	@Path("/grades/{course}/{student}")
	@Consumes({"application/xml", "application/json"})
	public Response createGrade(Grade gradeArg, @PathParam("course") ObjectId course, @PathParam("student") int student) {
		Student studentObj = Student.findStudentbyId(datastore, student);
		Course courseObj = Course.findCoursebyId(datastore, course);
		int gradeId = courseObj.getNewGradeId();
		Grade grade = new Grade(gradeArg.getMark(), gradeArg.getDate(), studentObj, gradeId);
		
		if(courseObj != null) {
			courseObj.addGrade(grade);
			datastore.save(courseObj);;
			URI location = null;
			try {
				location = new URI("http:/localhost:9998/service/grades/" + course.toString() + "/" + gradeId);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}

			return Response.created(location).build();
		}
				
		return Response.status(403).build();
	}

	/** DELETE METHODS**/

	@DELETE
	@Path("/students/{index}")
	public Response deleteStudent(@PathParam("index") int index) {
		Student.deleteStudentById(datastore, index);

		return Response.status(204).build();
	}
	
	@DELETE
	@Path("/courses/{id}")
	public Response deleteCourse(@PathParam("id") ObjectId id) {
		Course.deleteCourseById(datastore, id);
		
		return Response.status(204).build();
	}

	@DELETE
	@Path("/grades/{course}/{id}")
	public Response deleteGrade(@PathParam("course") ObjectId course, @PathParam("id") int id) {
		Course.deleteGradeById(datastore, course, id);

		return Response.status(204).build();
	}

	/** PUT METHODS**/

	@PUT
	@Path("/students/{index}")
	@Consumes({"application/xml", "application/json"})
	public Response updateStudent(@PathParam("index") int index, Student studentArg) {
		final Query<Student> studentQuery = datastore.createQuery(Student.class)
				.field("index")
				.equal(index);

		final UpdateOperations<Student> updateOperations = datastore.createUpdateOperations(Student.class)
				.set("name", studentArg.getName())
				.set("surname", studentArg.getSurname())
				.set("date", studentArg.getDate());

		final UpdateResults results = datastore.update(studentQuery, updateOperations);

		return Response.status(200).build();
	}

	@PUT
	@Path("/courses/{id}")
	@Consumes({"application/xml", "application/json"})
	public Response updateCourse(@PathParam("id") ObjectId id, Course courseArg) {
		final Query<Course> courseQuery = datastore.createQuery(Course.class)
				.field("id")
				.equal(id);

		final UpdateOperations<Course> updateOperations = datastore.createUpdateOperations(Course.class)
				.set("name", courseArg.getName())
				.set("professor", courseArg.getProfessor());

		final UpdateResults results = datastore.update(courseQuery, updateOperations);

		return Response.status(200).build();
	}

	@PUT
	@Path("grades/{course}/{id}")
	@Consumes({"application/xml", "application/json"})
	public Response updateGrade(@PathParam("course") ObjectId course, @PathParam("id") int id, Grade gradeArg) {
		Course courseObj = Course.findCoursebyId(datastore, course);
		List<Grade> grades = courseObj.getGrades();

		for(Grade grade : grades) {
			if(grade.getId() == id) {
				grade.setMark(gradeArg.getMark());
				grade.setDate(gradeArg.getDate());
				datastore.save(courseObj);
				break;
			}
		}

		return Response.status(200).build();
	}
}
