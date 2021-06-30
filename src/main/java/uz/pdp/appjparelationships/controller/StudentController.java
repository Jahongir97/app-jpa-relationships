package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.entity.Subject;
import uz.pdp.appjparelationships.payload.StudentDto;
import uz.pdp.appjparelationships.repository.AddressRepository;
import uz.pdp.appjparelationships.repository.GroupRepository;
import uz.pdp.appjparelationships.repository.StudentRepository;
import uz.pdp.appjparelationships.repository.SubjectRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    SubjectRepository subjectRepository;


    //CREATE
    @PostMapping()
    public String addStudent(StudentDto studentDto) {
        Optional<Address> optionalAddress = addressRepository.findById(studentDto.getAddressId());
        Optional<Group> optionalGroup = groupRepository.findById(studentDto.getGroupId());
        List<Subject> subjectList = subjectRepository.findAllById(studentDto.getSubjectsId());
        Student student = new Student();
        student.setFirstName(studentDto.getFirstName());
        student.setLastName(studentDto.getLastName());
        optionalAddress.ifPresent(student::setAddress);
        optionalGroup.ifPresent(student::setGroup);
        if (!subjectList.isEmpty()) {
            student.setSubjects(subjectList.subList(0, subjectList.size()));
        }
        studentRepository.save(student);
        return "Successfully added";
    }

    //READ
    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAll(pageable);
    }

    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam int page) {

        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
    }

    //3. FACULTY DEKANAT
    @GetMapping("/forFaculty/{facultyId}")
    public Page<Student> getStudentListForFaculty(@PathVariable Integer facultyId,
                                                  @RequestParam int page) {

        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAllByGroup_FacultyId(facultyId, pageable);
    }

    //4. GROUP OWNER
    @GetMapping("/forGroup/{groupId}")
    public Page<Student> getStudentListForGroup(@PathVariable Integer groupId,
                                                @RequestParam int page) {

        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAllByGroupId(groupId, pageable);
    }

    // UPDATE
    @PutMapping("/{id}")
    public String editStudent(@PathVariable Integer id, @RequestBody StudentDto studentDto) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        Optional<Group> optionalGroup = groupRepository.findById(studentDto.getGroupId());
        List<Subject> subjectList = subjectRepository.findAllById(studentDto.getSubjectsId());

        if (optionalStudent.isPresent()) {
            Student editingStudent = optionalStudent.get();
            Group group = editingStudent.getGroup();
            List<Subject> subject = editingStudent.getSubjects();
            editingStudent.setFirstName(studentDto.getFirstName());
            editingStudent.setLastName(studentDto.getLastName());
            optionalGroup.ifPresent(editingStudent::setGroup);
            editingStudent.setSubjects(subjectList.subList(0, subjectList.size()));
            groupRepository.save(group);
            subjectRepository.save(subject.listIterator().next());
            studentRepository.save(editingStudent);
            return "Successfully edited";
        }
        return "Student not found";
    }

    //DELETE

    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Integer id) {
        studentRepository.deleteById(id);
        boolean deleted = studentRepository.existsById(id);
        if (deleted) {
            return "Successfully deleted";
        } else {
            return "Student not found";
        }
    }

}
