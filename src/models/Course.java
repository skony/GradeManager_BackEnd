package models;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.query.Query;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement
@Entity("courses")
public class Course {
	
	@Id
    ObjectId id;
	String name;
	String professor;
	@Embedded
	List<Grade> grades = new ArrayList<Grade>();
	
	public Course(String name, String professor) {
		super();
		this.name = name;
		this.professor = professor;
	}

	public Course() {
		super();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProfessor() {
		return professor;
	}

	public void setProfessor(String professor) {
		this.professor = professor;
	}

	public List<Grade> getGrades() {
		return grades;
	}

	public void setGrades(List<Grade> grades) {
		this.grades = grades;
	}
	
	public void addGrade(Grade grade) {
		grades.add(grade);
	}
		
	public List<Grade> getStudentGrade(int student) {
		List<Grade> studentsGrades = new ArrayList<Grade>();
		
		for( Grade g : grades) {
			if(g.getStudent().getIndex() == student) {
				studentsGrades.add(g);
			}
		}
		
		return studentsGrades;
	}

	public int getGradesNum() {
		return grades.size();
	}

	public int getNewGradeId() {
		if(grades.size() == 0) {
			return 0;
		}
		else {
			return grades.get( grades.size() - 1).getId() + 1;
		}
	}

	public static List<Grade> getCourseGrade(Datastore datastore, ObjectId course) {
		Course courseObj = findCoursebyId(datastore, course);
		return courseObj.getGrades();
	}
	
	public static Course findCoursebyName(Datastore datastore, String name) {
		final List<Course> courses = datastore.createQuery(Course.class)
				.field("name")
				.equal(name)
				.asList();
		
		return courses.get(0);
	}

	public static Course findCoursebyId(Datastore datastore, ObjectId id) {
		final List<Course> courses = datastore.createQuery(Course.class)
				.field("id")
				.equal(id)
				.asList();

		return courses.get(0);
	}
	
	public static void deleteCourseByName(Datastore datastore, String name) {
		final Query<Course> courses = datastore.createQuery(Course.class)
				.field("name")
				.equal(name);
		
		datastore.delete(courses);
	}

	public static void deleteCourseById(Datastore datastore, ObjectId id) {
		final Query<Course> courses = datastore.createQuery(Course.class)
				.field("id")
				.equal(id);

		datastore.delete(courses);
	}

	public static void deleteGradeById(Datastore datastore, ObjectId course, int id) {
		Course courseObj = findCoursebyId(datastore, course);
		List<Grade> grades = courseObj.getGrades();

		for(Grade grade : grades) {
			if(grade.getId() == id) {
				grades.remove(grade);
				datastore.save(courseObj);
				break;
			}
		}
	}
}
