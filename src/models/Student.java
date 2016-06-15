package models;

import java.util.Date;
import java.util.List;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.query.Query;

@XmlRootElement
@Entity("students")
@Indexes(
	    @Index(value = "index", fields = @Field("index"))
	)
public class Student {

	@Id
	@XmlTransient
	//@XmlJavaTypeAdapter(ObjectIdJaxbAdapter.class)
    ObjectId id;
	int index;
	String name;
	String surname;
	@JsonFormat(shape=JsonFormat.Shape.STRING,
			pattern="yyyy-MM-dd", timezone="CET")
	Date date;
//	@InjectLinks({
//			@InjectLink(resource = models.Student.class, rel = "self"),
//			@InjectLink(resource = resources.Students.class, rel = "parent"),
//			@InjectLink(resource = resources.Grades.class, rel = "grades")
//	})
//	@XmlElement(name="link")
//	@XmlElementWrapper(name = "links")
//	@XmlJavaTypeAdapter(Link.JaxbAdapter.class)
//	List<Link> links;
	
	public Student(int index, String name, String surname, Date date) {
		super();
		this.index = index;
		this.name = name;
		this.surname = surname;
		this.date = date;
	}

	public Student() {
		super();
	}

	@XmlTransient
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public static Student findStudentbyId(Datastore datastore, int id) {
		final List<Student> students = datastore.createQuery(Student.class)
				.field("index")
				.equal(id)
				.asList();
		
		return students.get(0);
	}

	public static void deleteStudentById(Datastore datastore, int id) {
		final Query<Student> students = datastore.createQuery(Student.class)
				.field("index")
				.equal(id);

		datastore.delete(students);
	}
}
