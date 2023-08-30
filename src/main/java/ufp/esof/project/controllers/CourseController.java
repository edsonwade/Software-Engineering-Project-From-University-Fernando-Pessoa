package ufp.esof.project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ufp.esof.project.persistence.model.Course;
import ufp.esof.project.services.CourseService;

import java.util.Optional;

@RestController
@RequestMapping("/course")
public class CourseController {

    private CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<Course>> getAllCourses() {
        return ResponseEntity.ok(this.courseService.findAllCourses());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Course> getCourseById(@PathVariable("id") Long id) {
        Optional<Course> courseOptional = this.courseService.findById(id);
        if (courseOptional.isPresent())
            return ResponseEntity.ok(courseOptional.get());
        throw new InvalidCourseException(id);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteCourse(@PathVariable("id") Long id) {
        boolean res = this.courseService.deleteById(id);
        Optional<Course> optionalCourse = this.courseService.findById(id);
        if (optionalCourse.isPresent())
            throw new CourseNotDeletedException(optionalCourse.get().getCourseName());

        if (res)
            return ResponseEntity.ok("Course deleted successfully!");
        else
            throw new InvalidCourseException(id);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        Optional<Course> optionalCourse = this.courseService.createCourse(course);
        if (optionalCourse.isPresent())
            return ResponseEntity.ok(optionalCourse.get());
        throw new CourseNotCreatedException(course.getCourseName());
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Course> editCourse(@PathVariable("id") Long id, @RequestBody Course course) {
        Optional<Course> optionalCourse = this.courseService.findById(id);
        if (optionalCourse.isEmpty())
            throw new InvalidCourseException(id);

        optionalCourse = this.courseService.editCourse(optionalCourse.get(), course, id);
        if (optionalCourse.isPresent())
            return ResponseEntity.ok(optionalCourse.get());

        throw new CourseNotEditedException(id);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid Course")
    public static class InvalidCourseException extends RuntimeException {
        public InvalidCourseException(Long id) {
            super("The course with id " + id + " does not exist");
        }
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Course not deleted")
    public static class CourseNotDeletedException extends RuntimeException {
        public CourseNotDeletedException(String name) {
            super("The course with name \"" + name + "\" was not deleted");
        }
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Course not created")
    public static class CourseNotCreatedException extends RuntimeException {
        public CourseNotCreatedException(String name) {
            super("The course with name \"" + name + "\" was not created");
        }
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Course not edited")
    public static class CourseNotEditedException extends RuntimeException {
        public CourseNotEditedException(Long id) {
            super("The course with id \"" + id + "\" was not edited");
        }
    }
}
