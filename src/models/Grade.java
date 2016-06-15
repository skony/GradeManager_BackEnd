package models;

import javax.xml.bind.annotation.XmlRootElement;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Reference;

import java.util.Date;

@XmlRootElement
@Embedded
public class Grade {

	double mark;
	Date date;
	@Reference
	Student student;
	
	public Grade(double mark, Date date, Student student) {
		super();
		this.mark = mark;
		this.date = date;
		this.student = student;
	}

	public Grade() {
		super();
	}

	public double getMark() {
		return mark;
	}

	public void setMark(double mark) {
		this.mark = mark;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}
}
