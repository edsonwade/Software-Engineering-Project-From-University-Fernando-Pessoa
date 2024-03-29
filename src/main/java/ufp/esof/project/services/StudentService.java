package ufp.esof.project.services;

import org.springframework.stereotype.Service;
import ufp.esof.project.exception.ObjectNotFoundById;
import ufp.esof.project.persistence.model.Appointment;
import ufp.esof.project.persistence.model.Student;
import ufp.esof.project.persistence.repositories.AppointmentRepo;
import ufp.esof.project.persistence.repositories.StudentRepo;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class StudentService {

    private final StudentRepo studentRepo;

    private final AppointmentRepo appointmentRepo;

    public StudentService(StudentRepo studentRepo, AppointmentRepo appointmentRepo) {
        this.studentRepo = studentRepo;
        this.appointmentRepo = appointmentRepo;
    }

    public Iterable<Student> findAll() {
        return this.studentRepo.findAll();
    }

    public Optional<Student> findById(Long id) {
       return Optional.of(studentRepo.findById(1L))
               .orElseThrow(()-> new ObjectNotFoundById("Student with " + id + " not found"));
    }

    public boolean deleteById(Long id) {
        Optional<Student> optionalStudent = this.findById(id);
        if (optionalStudent.isPresent()) {
            if (optionalStudent.get().getAppointments().isEmpty()) {
                this.studentRepo.deleteById(id);
                return true;
            }
            return false;
        }
        return false;
    }

    public Optional<Student> createStudent(Student student) {
        Student newStudent = new Student();
        Optional<Student> optionalStudent = this.validateAppointments(student, student);
        if (optionalStudent.isPresent())
            newStudent = optionalStudent.get();

        optionalStudent = this.studentRepo.findByStudentName(student.getStudentName());
        if (optionalStudent.isPresent())
            return Optional.empty();

        return Optional.of(this.studentRepo.save(newStudent));
    }

    public Optional<Student> editStudent(Student currentStudent, Student student, Long id) {
        Student newStudent = new Student();
        Optional<Student> optionalStudent = this.validateAppointments(currentStudent, student);
        if (optionalStudent.isPresent())
            newStudent = optionalStudent.get();

        optionalStudent = this.studentRepo.findByStudentName(student.getStudentName());
        if (optionalStudent.isPresent() && (!optionalStudent.get().getStudentId().equals(id)))
            return Optional.empty();

        newStudent.setStudentName(student.getStudentName());

        return Optional.of(this.studentRepo.save(newStudent));
    }

    public Optional<Student> validateAppointments(Student currentStudent, Student student) {
        Set<Appointment> newAppointments = new HashSet<>();
        for (Appointment appointment : student.getAppointments()) {
            Optional<Appointment> optionalAppointment = this.appointmentRepo.findById(appointment.getAppointmentId());
            if (optionalAppointment.isEmpty())
                return Optional.empty();
            Appointment foundAppointment = optionalAppointment.get();
            foundAppointment.setStudent(currentStudent);
            newAppointments.add(foundAppointment);
        }

        currentStudent.setAppointments(newAppointments);
        return Optional.of(currentStudent);
    }
}
