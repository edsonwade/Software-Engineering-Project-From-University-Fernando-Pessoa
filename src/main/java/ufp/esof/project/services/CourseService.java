package ufp.esof.project.services;

import org.springframework.stereotype.Service;
import ufp.esof.project.persistence.model.Course;
import ufp.esof.project.persistence.model.Degree;
import ufp.esof.project.persistence.model.Explainer;
import ufp.esof.project.persistence.repositories.CourseRepo;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class CourseService {

    private final CourseRepo courseRepo;
    private final DegreeService degreeService;
    private final ExplainerService explainerService;


    public CourseService(CourseRepo courseRepo, DegreeService degreeService, ExplainerService explainerService) {
        this.courseRepo = courseRepo;
        this.degreeService = degreeService;
        this.explainerService = explainerService;
    }

    public Iterable<Course> findAllCourses() {
        return this.courseRepo.findAll();
    }

    public Optional<Course> findById(Long id) {
        return this.courseRepo.findById(id);
    }

    public boolean deleteById(Long id) {
        Optional<Course> optionalCourse = this.findById(id);
        if (optionalCourse.isPresent()) {
            this.courseRepo.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Course> createCourse(Course course) {
        Course newCourse = new Course();
        Optional<Course> optionalCourse = this.validateExplainers(course, course);
        if (optionalCourse.isPresent())
            newCourse = optionalCourse.get();

        optionalCourse = this.courseRepo.findByCourseName(course.getCourseName());
        if (optionalCourse.isPresent())
            return Optional.empty();

        Degree degree = course.getDegree();
        Optional<Degree> optionalDegree = this.degreeService.findDegreeByName(degree.getDegreeName());
        if (optionalDegree.isPresent()) {
            newCourse.setDegree(optionalDegree.get());
            return Optional.of(this.courseRepo.save(newCourse));
        }
        return Optional.empty();
    }

    public Optional<Course> editCourse(Course currentCourse, Course course, Long id) {
        Course newCourse = new Course();
        Optional<Course> optionalCourse = this.validateExplainers(currentCourse, course);
        if (optionalCourse.isPresent())
            newCourse = optionalCourse.get();

        optionalCourse = this.courseRepo.findByCourseName(course.getCourseName());
        if (optionalCourse.isPresent() && (!optionalCourse.get().getCourseId().equals(id)))
            return Optional.empty();

        newCourse.setCourseName(course.getCourseName());

        Optional<Degree> optionalDegree = this.degreeService.findDegreeByName(course.getDegree().getDegreeName());
        if (optionalDegree.isEmpty())
            return Optional.empty();

        newCourse.setDegree(optionalDegree.get());

        return Optional.of(this.courseRepo.save(newCourse));
    }

    public Optional<Course> validateExplainers(Course currentCourse, Course course) {
        Set<Explainer> newExplainers = new HashSet<>();
        for (Explainer explainer : course.getExplainers()) {
            Optional<Explainer> optionalExplainer = this.explainerService
                    .findExplainerByName(explainer.getExplainerName());
            if (optionalExplainer.isEmpty())
                return Optional.empty();
            Explainer foundExplainer = optionalExplainer.get();
            foundExplainer.addCourse(currentCourse);
            newExplainers.add(foundExplainer);
        }

        currentCourse.setExplainers(newExplainers);
        return Optional.of(currentCourse);
    }
}
